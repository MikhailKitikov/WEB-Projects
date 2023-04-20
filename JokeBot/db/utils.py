from sqlalchemy import Executable
from sqlalchemy.ext.asyncio import async_sessionmaker, AsyncSession


async def fetch_execute(q: Executable, session_maker: async_sessionmaker) -> list:
    session: AsyncSession
    async with session_maker() as session:
        result = [obj[0] for obj in (await session.execute(q)).fetchall()]
        await session.commit()
    return result


async def execute(q: Executable, session_maker: async_sessionmaker) -> None:
    session: AsyncSession
    async with session_maker() as session:
        await session.execute(q)
        await session.commit()
