from sqlalchemy import update
from sqlalchemy.ext.asyncio import async_sessionmaker
from sqlalchemy import select, insert

from db.utils import *
from db.models import *


async def get_user(user_id: int, session_maker: async_sessionmaker, language_code: str = JokeLanguages.EN) -> User:
    users = await fetch_execute(
        select(User)
        .where(User.id.__eq__(user_id)),
        session_maker=session_maker
    )

    if not users:
        language = language_code
        if language not in [e.value for e in JokeLanguages]:
            language = JokeLanguages.EN

        users = await fetch_execute(
            insert(User)
            .values(id=user_id, language=language, state=FSMState.NOTHING)
            .returning(User),
            session_maker=session_maker
        )

    return users[0]


async def update_state(user: User, state: FSMState, session_maker: async_sessionmaker) -> User:
    await execute(
        update(User)
        .where(User.id.__eq__(user.id))
        .values(state=state),
        session_maker=session_maker
    )

    return await get_user(user.id, session_maker)


async def change_language(user: User, session_maker: async_sessionmaker) -> User:
    new_language = JokeLanguages.EN if user.language == JokeLanguages.RU else JokeLanguages.RU
    await execute(
        update(User)
        .where(User.id.__eq__(user.id))
        .values(language=new_language),
        session_maker=session_maker
    )

    return await get_user(user.id, session_maker)


async def send_feedback(user: User, text: str, session_maker: async_sessionmaker) -> None:
    await execute(
        insert(UserFeedback)
        .values(user_id=user.id, text=text),
        session_maker=session_maker
    )
