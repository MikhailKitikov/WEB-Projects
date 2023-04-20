import json
import os
import sys

import aiogram

from aiogram import Bot, Dispatcher, types
from aiogram.utils import executor
from aiogram.utils.exceptions import *
from sqlalchemy import URL
from sqlalchemy.ext.asyncio import create_async_engine

from utils.keyboard_utils import get_keyboard
from services.assessment_service import assess_guess
from services.joke_service import *
from utils.l10n_utils import l10n
from services.user_service import get_user, change_language, update_state, send_feedback
from services.translation.google_translate import translate

import direnv
direnv.load('.')

API_TOKEN = os.getenv('API_TOKEN')
POSTGRES_HOST = os.getenv('POSTGRES_HOST')
POSTGRES_PORT = os.getenv('POSTGRES_PORT')
POSTGRES_DB = os.getenv('POSTGRES_DB')
POSTGRES_USER = os.getenv('POSTGRES_USER')
POSTGRES_PASS = os.getenv('POSTGRES_PASS')
BUTTON_PREFIX = 'btn_'

bot = Bot(token=API_TOKEN)
dp = Dispatcher(bot)


@dp.message_handler(commands=['start'])
async def handle_welcome(message: types.Message):
    user: User = await get_user(message.from_user.id, session_maker, message.from_user.language_code)
    print(f"\n[INFO] function: handle_welcome, state: {user.state.name}\n")

    user = await update_state(user, FSMState.NOTHING, session_maker)

    await message.reply(l10n['Hi'][user.language], reply_markup=get_keyboard(user.state, user.language))


@dp.message_handler(lambda msg: msg.text.lower() in list(map(str.lower, l10n['/change_language'].values())))
async def handle_change_language(message: types.Message):
    user: User = await get_user(message.from_user.id, session_maker, message.from_user.language_code)
    print(f"\n[INFO] function: handle_change_language, state: {user.state.name}\n")

    user = await change_language(user, session_maker=session_maker)

    await message.reply(f"{l10n['New language'][user.language]}: {user.language.name}",
                        reply_markup=get_keyboard(user.state, user.language))


@dp.message_handler(lambda msg: msg.text.lower() in list(map(str.lower, l10n['/new_joke'].values())))
async def handle_new_joke(message: types.Message):
    user: User = await get_user(message.from_user.id, session_maker, message.from_user.language_code)
    print(f"\n[INFO] function: handle_new_joke, state: {user.state.name}\n")

    joke: Optional[Joke] = await generate_joke(user, session_maker=session_maker)

    if not joke:
        await message.reply(f"{l10n['Sorry, no jokes left'][user.language]}",
                            reply_markup=get_keyboard(user.state, user.language))
    else:
        user = await update_state(user, FSMState.JOKE_IN_PROGRESS, session_maker)
        await message.reply(f"{joke.text_begin} \n\n{l10n['Continue the joke'][user.language]}",
                            reply_markup=get_keyboard(user.state, user.language))


@dp.message_handler(lambda msg: msg.text.lower() in list(map(str.lower, l10n['/hint'].values())))
async def handle_hint(message: types.Message):
    user: User = await get_user(message.from_user.id, session_maker, message.from_user.language_code)
    print(f"\n[INFO] function: handle_hint, state: {user.state.name}\n")

    if user.state != FSMState.JOKE_IN_PROGRESS:
        return

    hint: Optional[JokeHint] = await get_hint(user, session_maker=session_maker)

    if not hint:
        await message.reply(f"{l10n['No hints left'][user.language]}",
                            reply_markup=get_keyboard(user.state, user.language))
    else:
        await message.reply(f"{l10n['Here is the hint'][user.language]} \n\n {hint.text}",
                            reply_markup=get_keyboard(user.state, user.language))


@dp.message_handler(lambda msg: msg.text.lower() in list(map(str.lower, l10n['/feedback'].values())))
async def handle_feedback(message: types.Message):
    user: User = await get_user(message.from_user.id, session_maker, message.from_user.language_code)
    print(f"\n[INFO] function: handle_feedback, state: {user.state.name}\n")

    user = await update_state(user, FSMState.FEEDBACK_IN_PROGRESS, session_maker)

    await message.reply(f"{l10n['Leave your feedback'][user.language]}",
                        reply_markup=get_keyboard(user.state, user.language))


@dp.message_handler()
async def handle_text(message: types.Message):
    user: User = await get_user(message.from_user.id, session_maker, message.from_user.language_code)
    print(f"\n[INFO] function: handle_text, state: {user.state.name}\n")

    if user.state == FSMState.JOKE_IN_PROGRESS:
        reference_joke: Joke = await get_curr_joke(user, session_maker=session_maker)
        guess: JokeGuess = await make_guess(user, reference_joke, message.text, session_maker=session_maker)
        grade, feedback = await assess_guess(guess, reference_joke, session_maker, user_language=user.language, return_verbal=True)
        user = await update_state(user, FSMState.NOTHING, session_maker)

        like_button = types.InlineKeyboardButton(f"👍 ({reference_joke.likes})",
                                                 callback_data=BUTTON_PREFIX + json.dumps(
                                                     {'type': 'LIKE', 'obj': 'joke', 'obj_id': reference_joke.id}))
        dislike_button = types.InlineKeyboardButton(f"👎 ({reference_joke.dislikes})",
                                                    callback_data=BUTTON_PREFIX + json.dumps(
                                                        {'type': 'DISLIKE', 'obj': 'joke',
                                                         'obj_id': reference_joke.id}))
        other_guesses_button = types.InlineKeyboardButton(l10n['/other_guesses'][user.language],
                                                          callback_data=BUTTON_PREFIX + json.dumps(
                                                              {'type': 'guess', 'joke_id': reference_joke.id,
                                                               'guess_id': guess.id}))
        keyboard = types.InlineKeyboardMarkup(row_width=2, resize_keyboard=True)
        keyboard.row(like_button, dislike_button)
        keyboard.row(other_guesses_button)

        optional_feedback = ''
        if feedback is not None:
            if user.language != JokeLanguages.EN:
                optional_feedback = f"\n\n{translate([feedback], user.language.value.lower())[0]}"
            else:
                optional_feedback = f"\n\n{feedback}"

        await message.reply(f"""{grade} {optional_feedback} \n\n{l10n['Look what I came up with'][user.language]}: \n\n"""
                            f"{reference_joke.text_end}",
                            reply_markup=keyboard)

        await message.answer(l10n['Select action'][user.language],
                             reply_markup=get_keyboard(user.state, user.language))

    elif user.state == FSMState.FEEDBACK_IN_PROGRESS:
        await send_feedback(user, message.text, session_maker=session_maker)
        user = await update_state(user, FSMState.NOTHING, session_maker)
        await message.reply(l10n['Thanks for your opinion'][user.language],
                            reply_markup=get_keyboard(user.state, user.language))

    else:
        await message.answer(l10n['Select action'][user.language],
                             reply_markup=get_keyboard(user.state, user.language))


@dp.callback_query_handler(lambda cb_query: cb_query.data and cb_query.data.startswith(BUTTON_PREFIX))
async def process_callback(callback_query: types.CallbackQuery):
    data = json.loads(callback_query.data[len(BUTTON_PREFIX):])
    user: User = await get_user(callback_query.from_user.id, session_maker, callback_query.from_user.language_code)
    print(f"\n[INFO] function: process_callback, state: {user.state.name}\n")

    if data['type'] in ['LIKE', 'DISLIKE']:
        if data['obj'] == 'joke':
            joke: Joke = await react_to_joke(user, data['obj_id'], JokeReactionTypes(data['type']), session_maker)

            like_button = types.InlineKeyboardButton(f"👍 ({joke.likes})",
                                                     callback_data=BUTTON_PREFIX + json.dumps(
                                                         {'type': 'LIKE', 'obj': 'joke', 'obj_id': joke.id}))
            dislike_button = types.InlineKeyboardButton(f"👎 ({joke.dislikes})",
                                                        callback_data=BUTTON_PREFIX + json.dumps(
                                                            {'type': 'DISLIKE', 'obj': 'joke',
                                                             'obj_id': joke.id}))
            callback_query.message.reply_markup.inline_keyboard[0] = [like_button, dislike_button]
            try:
                await callback_query.message.edit_reply_markup(callback_query.message.reply_markup)
            except MessageNotModified:
                pass

        elif data['obj'] == 'guess':
            guess: JokeGuess = await react_to_joke_guess(user, data['obj_id'], JokeReactionTypes(data['type']),
                                                         session_maker)

            like_button = types.InlineKeyboardButton(f"👍 ({guess.likes})",
                                                     callback_data=BUTTON_PREFIX + json.dumps(
                                                         {'type': 'LIKE', 'obj': 'guess', 'obj_id': guess.id}))
            dislike_button = types.InlineKeyboardButton(f"👎 ({guess.dislikes})",
                                                        callback_data=BUTTON_PREFIX + json.dumps(
                                                            {'type': 'DISLIKE', 'obj': 'guess',
                                                             'obj_id': guess.id}))
            callback_query.message.reply_markup.inline_keyboard[0] = [like_button, dislike_button]

            try:
                await callback_query.message.edit_reply_markup(callback_query.message.reply_markup)
            except MessageNotModified:
                pass

    elif data['type'] == 'guess':
        joke_id: int = data['joke_id']
        last_shown_guess_id: Optional[int] = data['guess_id']
        guess: Optional[JokeGuess] = await get_other_guess(user, joke_id, last_shown_guess_id, session_maker)

        if not guess:
            await callback_query.message.answer(f"{l10n['No other guesses'][user.language]}",
                                                reply_markup=get_keyboard(user.state, user.language))
            await bot.answer_callback_query(callback_query.id)
            return

        like_button = types.InlineKeyboardButton(f"👍 ({guess.likes})",
                                                 callback_data=BUTTON_PREFIX + json.dumps(
                                                     {'type': 'LIKE', 'obj': 'guess', 'obj_id': guess.id}))
        dislike_button = types.InlineKeyboardButton(f"👎 ({guess.dislikes})",
                                                    callback_data=BUTTON_PREFIX + json.dumps(
                                                        {'type': 'DISLIKE', 'obj': 'guess', 'obj_id': guess.id}))
        other_guesses_button = types.InlineKeyboardButton(l10n['/other_guesses'][user.language],
                                                          callback_data=BUTTON_PREFIX + json.dumps(
                                                              {'type': 'guess', 'joke_id': joke_id,
                                                               'guess_id': guess.id})
                                                          )
        keyboard = types.InlineKeyboardMarkup(row_width=2, resize_keyboard=True)
        keyboard.row(like_button, dislike_button)
        keyboard.row(other_guesses_button)

        await callback_query.message.answer(f"{l10n['Other guess'][user.language]} \n\n {guess.text}",
                                            reply_markup=keyboard)


if __name__ == '__main__':
    postgres_url = URL.create('postgresql+asyncpg',
                              username=POSTGRES_USER,
                              database=POSTGRES_DB,
                              host=POSTGRES_HOST,
                              port=POSTGRES_PORT,
                              password=POSTGRES_PASS)
    engine = create_async_engine(url=postgres_url, echo=True, pool_pre_ping=True)
    session_maker = async_sessionmaker(engine, expire_on_commit=False)

    executor.start_polling(dp, skip_updates=True)
