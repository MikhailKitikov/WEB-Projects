import datetime
import os
from typing import Optional

import numpy as np

from sqlalchemy import insert, update
from sqlalchemy.ext.asyncio import async_sessionmaker
from sqlalchemy import select

from db.models import *
from db.utils import *


async def generate_joke(user: User, session_maker: async_sessionmaker) -> Optional[Joke]:
    used_joke_ids = await fetch_execute(
        select(JokeSession.joke_id)
        .where(JokeSession.user_id.__eq__(user.id))
        .where(JokeSession.ended_at.is_not(None)),
        session_maker=session_maker
    )
    joke_ids = await fetch_execute(
        select(Joke.id)
        .where(Joke.id.not_in(used_joke_ids))
        .where(Joke.language.__eq__(user.language)),
        session_maker=session_maker
    )
    if not joke_ids:
        return None

    joke_id = np.random.choice(joke_ids)
    jokes = await fetch_execute(
        select(Joke)
        .where(Joke.id.__eq__(joke_id)),
        session_maker=session_maker
    )
    if not jokes:
        return None

    joke = jokes[0]
    await execute(
        insert(JokeSession)
        .values(user_id=user.id, joke_id=joke.id),
        session_maker=session_maker
    )
    await execute(
        update(Joke)
        .where(Joke.id.__eq__(joke.id))
        .values(displays=Joke.displays + 1),
        session_maker=session_maker
    )

    return joke


async def get_curr_joke_session(user: User, session_maker: async_sessionmaker) -> Optional[JokeSession]:
    current_joke_sessions = await fetch_execute(
        select(JokeSession)
        .where(JokeSession.user_id.__eq__(user.id))
        .where(JokeSession.ended_at.is_(None))
        .order_by(JokeSession.created_at.desc())
        .limit(1),
        session_maker=session_maker
    )
    if not current_joke_sessions:
        return None

    return current_joke_sessions[0]


async def get_curr_joke(user: User, session_maker: async_sessionmaker) -> Optional[Joke]:
    joke_session: JokeSession = await get_curr_joke_session(user, session_maker=session_maker)
    if not joke_session:
        return None

    jokes = await fetch_execute(
        select(Joke)
        .where(Joke.id.__eq__(joke_session.joke_id)),
        session_maker=session_maker
    )
    if not jokes:
        return None

    return jokes[0]


async def get_hint(user: User, session_maker: async_sessionmaker) -> Optional[Joke]:
    joke_session: JokeSession = await get_curr_joke_session(user, session_maker=session_maker)

    hints = await fetch_execute(
        select(JokeHint)
        .where(JokeHint.joke_id.__eq__(joke_session.joke_id))
        .order_by(JokeHint.id.asc()),
        session_maker=session_maker
    )
    if not hints or len(hints) <= joke_session.hints_shown:
        return None

    hint = hints[joke_session.hints_shown]
    await execute(
        update(JokeSession)
        .where(JokeSession.id.__eq__(joke_session.id))
        .values(hints_shown=joke_session.hints_shown + 1),
        session_maker=session_maker
    )

    return hint


async def make_guess(user: User, joke: Joke, text: str, session_maker: async_sessionmaker) -> JokeGuess:
    joke_session: JokeSession = await get_curr_joke_session(user, session_maker=session_maker)
    guesses = await fetch_execute(
        insert(JokeGuess)
        .values(text=text, user_id=user.id, joke_id=joke.id, hints_shown=joke_session.hints_shown)
        .returning(JokeGuess),
        session_maker=session_maker
    )

    await execute(
        update(JokeSession)
        .where(JokeSession.id.__eq__(joke_session.id))
        .values(ended_at=datetime.datetime.utcnow()),
        session_maker=session_maker
    )

    return guesses[0]


async def react_to_joke(user: User, joke_id: int, reaction: JokeReactionTypes,
                        session_maker: async_sessionmaker) -> Joke:
    reactions = await fetch_execute(
        select(JokeReaction)
        .where(JokeReaction.user_id.__eq__(user.id))
        .where(JokeReaction.joke_id.__eq__(joke_id)),
        session_maker=session_maker
    )

    prev_reaction = None
    if reactions:
        prev_reaction = reactions[0]

    if prev_reaction and prev_reaction.reaction == reaction:
        likes_delta = dislikes_delta = 0
    elif prev_reaction:
        await execute(
            update(JokeReaction)
            .where(JokeReaction.id.__eq__(prev_reaction.id))
            .values(reaction=reaction),
            session_maker=session_maker
        )
        likes_delta = 2 * int(reaction == JokeReactionTypes.LIKE) - 1
        dislikes_delta = -likes_delta
    else:
        await execute(
            insert(JokeReaction)
            .values(joke_id=joke_id, user_id=user.id, reaction=reaction),
            session_maker=session_maker
        )
        likes_delta = int(reaction == JokeReactionTypes.LIKE)
        dislikes_delta = 1 - likes_delta

    jokes = await fetch_execute(
        update(Joke)
        .where(Joke.id.__eq__(joke_id))
        .values(likes=Joke.likes + likes_delta, dislikes=Joke.dislikes + dislikes_delta)
        .returning(Joke),
        session_maker=session_maker
    )

    return jokes[0]


async def react_to_joke_guess(user: User, joke_guess_id: int, reaction: JokeReactionTypes,
                              session_maker: async_sessionmaker) -> JokeGuess:
    reactions = await fetch_execute(
        select(JokeGuessReaction)
        .where(JokeGuessReaction.user_id.__eq__(user.id))
        .where(JokeGuessReaction.joke_guess_id.__eq__(joke_guess_id)),
        session_maker=session_maker
    )

    prev_reaction = None
    if reactions:
        prev_reaction = reactions[0]

    if prev_reaction and prev_reaction.reaction == reaction:
        likes_delta = dislikes_delta = 0
    elif prev_reaction:
        await execute(
            update(JokeGuessReaction)
            .where(JokeGuessReaction.id.__eq__(prev_reaction.id))
            .values(reaction=reaction),
            session_maker=session_maker
        )
        likes_delta = 2 * int(reaction == JokeReactionTypes.LIKE) - 1
        dislikes_delta = -likes_delta
    else:
        await execute(
            insert(JokeGuessReaction)
            .values(joke_guess_id=joke_guess_id, user_id=user.id, reaction=reaction),
            session_maker=session_maker
        )
        likes_delta = int(reaction == JokeReactionTypes.LIKE)
        dislikes_delta = 1 - likes_delta

    guesses = await fetch_execute(
        update(JokeGuess)
        .where(JokeGuess.id.__eq__(joke_guess_id))
        .values(likes=JokeGuess.likes + likes_delta, dislikes=JokeGuess.dislikes + dislikes_delta)
        .returning(JokeGuess),
        session_maker=session_maker
    )

    return guesses[0]


async def get_other_guess(user: User, joke_id: int, last_shown_guess_id: Optional[int],
                          session_maker: async_sessionmaker) -> Optional[JokeGuess]:
    guesses = await fetch_execute(
        select(JokeGuess)
        .where(JokeGuess.joke_id.__eq__(joke_id))
        .where(JokeGuess.user_id.__ne__(user.id))
        .where(JokeGuess.id.__gt__(last_shown_guess_id or 0))
        .order_by(JokeGuess.id.asc())
        .limit(1),
        session_maker=session_maker
    )
    if not guesses:
        return None

    guess = guesses[0]
    await execute(
        update(JokeGuess)
        .where(JokeGuess.id.__eq__(guess.id))
        .values(displays=JokeGuess.displays + 1),
        session_maker=session_maker
    )
    return guess
