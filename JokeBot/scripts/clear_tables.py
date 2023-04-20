import asyncio
import asyncpg
import direnv
import os


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

    await conn.execute('''TRUNCATE TABLE users CASCADE;''')
    await conn.execute('''TRUNCATE TABLE jokes CASCADE;''')
    await conn.execute('''TRUNCATE TABLE joke_sessions CASCADE;''')
    await conn.execute('''TRUNCATE TABLE joke_guesses CASCADE;''')
    await conn.execute('''TRUNCATE TABLE joke_hints CASCADE;''')
    await conn.execute('''TRUNCATE TABLE user_feedbacks CASCADE;''')
    await conn.execute('''TRUNCATE TABLE joke_reactions CASCADE;''')
    await conn.execute('''TRUNCATE TABLE joke_guess_reactions CASCADE;''')

    await conn.close()

asyncio.get_event_loop().run_until_complete(main())
