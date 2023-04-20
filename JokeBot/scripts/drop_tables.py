import asyncio
import asyncpg
import direnv
import os
import shutil


direnv.load()

POSTGRES_HOST = os.getenv('POSTGRES_HOST')
POSTGRES_PORT = os.getenv('POSTGRES_PORT')
POSTGRES_DB = os.getenv('POSTGRES_DB')
POSTGRES_USER = os.getenv('POSTGRES_USER')
POSTGRES_PASS = os.getenv('POSTGRES_PASS')


async def main():
    conn = await asyncpg.connect(database=POSTGRES_DB,
                                 password=POSTGRES_PASS,
                                 port=POSTGRES_PORT,
                                 host=POSTGRES_HOST,
                                 user=POSTGRES_USER)

    await conn.execute('''DROP TABLE IF EXISTS users CASCADE;''')
    await conn.execute('''DROP TABLE IF EXISTS jokes CASCADE;''')
    await conn.execute('''DROP TABLE IF EXISTS joke_sessions CASCADE;''')
    await conn.execute('''DROP TABLE IF EXISTS joke_guesses CASCADE;''')
    await conn.execute('''DROP TABLE IF EXISTS joke_hints CASCADE;''')
    await conn.execute('''DROP TABLE IF EXISTS user_feedbacks CASCADE;''')
    await conn.execute('''DROP TABLE IF EXISTS joke_reactions CASCADE;''')
    await conn.execute('''DROP TABLE IF EXISTS joke_guess_reactions CASCADE;''')
    await conn.execute('''DROP TABLE IF EXISTS alembic_version CASCADE;''')
    await conn.execute('''DROP TYPE IF EXISTS jokelanguages CASCADE;''')
    await conn.execute('''DROP TYPE IF EXISTS fsmstate CASCADE;''')
    await conn.execute('''DROP TYPE IF EXISTS jokereactiontypes CASCADE;''')

    await conn.close()

    if os.path.exists('db/migrations/versions'):
        shutil.rmtree('db/migrations/versions')
        os.mkdir('db/migrations/versions')
    else:
        os.mkdir('db/migrations/versions')

asyncio.get_event_loop().run_until_complete(main())
