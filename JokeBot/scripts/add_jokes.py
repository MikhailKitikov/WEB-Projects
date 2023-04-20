import asyncio
import direnv
import os
import json
import sys

from sqlalchemy import URL, insert, select
from sqlalchemy.ext.asyncio import create_async_engine, async_sessionmaker, AsyncSession

sys.path.append(os.getcwd())
from db.models import Joke, JokeHint

direnv.load()

POSTGRES_HOST = os.getenv('POSTGRES_HOST')
POSTGRES_PORT = os.getenv('POSTGRES_PORT')
POSTGRES_DB = os.getenv('POSTGRES_DB')
POSTGRES_USER = os.getenv('POSTGRES_USER')
POSTGRES_PASS = os.getenv('POSTGRES_PASS')


async def main():
    base = '../data/'
    jokes = [joke for file in os.listdir(base)
             for joke in json.load(open(base + file, encoding='utf-8'))
             if file.endswith('.json')]
    jokes_only = [{k: v for k, v in joke.items() if k != 'hints'} for joke in jokes]

    postgres_url = URL.create('postgresql+asyncpg',
                              username=POSTGRES_USER,
                              database=POSTGRES_DB,
                              host=POSTGRES_HOST,
                              port=POSTGRES_PORT,
                              password=POSTGRES_PASS)
    engine = create_async_engine(url=postgres_url, echo=False, pool_pre_ping=True)
    session_maker = async_sessionmaker(engine, expire_on_commit=False)

    session: AsyncSession
    async with session_maker() as session:
        q = insert(Joke).values(jokes_only)
        await session.execute(q)

        q = select(Joke.id).order_by(Joke.id.asc())
        joke_ids = [obj[0] for obj in (await session.execute(q)).fetchall()]

        await session.commit()

    hints = [{'joke_id': joke_id, 'text': hint}
             for joke_dict, joke_id in zip(jokes, joke_ids)
             if 'hints' in joke_dict
             for hint in joke_dict['hints']]

    session: AsyncSession
    async with session_maker() as session:
        q = insert(JokeHint).values(hints)
        await session.execute(q)
        await session.commit()


asyncio.get_event_loop().run_until_complete(main())
