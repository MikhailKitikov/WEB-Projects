from aiogram import types

from db.models import FSMState, JokeLanguages
from utils.l10n_utils import l10n


def get_keyboard(state: FSMState, language: JokeLanguages) -> types.ReplyKeyboardMarkup:
    keyboards = {
        FSMState.NOTHING: types.ReplyKeyboardMarkup(keyboard=[
            [types.KeyboardButton(text=l10n['/new_joke'][language])],
            [types.KeyboardButton(text=l10n['/change_language'][language]),
             types.KeyboardButton(text=l10n['/feedback'][language])]
        ], resize_keyboard=True),
        FSMState.FEEDBACK_IN_PROGRESS: types.ReplyKeyboardMarkup(keyboard=[
            [types.KeyboardButton(text=l10n['/new_joke'][language]),
             types.KeyboardButton(text=l10n['/change_language'][language])],
        ], resize_keyboard=True),
        FSMState.JOKE_IN_PROGRESS: types.ReplyKeyboardMarkup(keyboard=[
            [types.KeyboardButton(text=l10n['/hint'][language]),
             types.KeyboardButton(text=l10n['/new_joke'][language])],
            [types.KeyboardButton(text=l10n['/change_language'][language]),
             types.KeyboardButton(text=l10n['/feedback'][language])]
        ], resize_keyboard=True)
    }

    return keyboards[state]
