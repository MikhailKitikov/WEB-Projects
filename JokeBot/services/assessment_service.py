import numpy as np

from typing import Union
from typing import Optional

from sqlalchemy import update
from sqlalchemy.ext.asyncio import async_sessionmaker

from db.models import *
from db.utils import *
from services.bert.bert import vector_similarity
from utils.l10n_utils import l10n
from services.chatgpt.chat_gpt import chat_gpt_grade
from services.translation.google_translate import translate

punctuation = r"""!"#$%&'()*+,-—./:;<=>?@[\]^_`{|}~"""


async def assess_guess(guess: JokeGuess, reference_joke: Joke, session_maker: async_sessionmaker,
                       user_language: JokeLanguages,  return_verbal: bool) -> (Union[float, str], str):
    grade_method, grade, feedback = compute_grade(guess.text, reference_joke, user_language)

    await fetch_execute(
        update(JokeGuess)
        .where(JokeGuess.id.__eq__(guess.id))
        .values(grade_method=grade_method, grade=grade, gpt_feedback=feedback)
        .returning(JokeGuess),
        session_maker=session_maker
    )

    if return_verbal:
        grade = get_verbal_grade(grade, user_language)

    return grade, feedback


def get_verbal_grade(grade: float, user_language: JokeLanguages) -> str:
    if grade > 0.7:
        return np.random.choice(l10n['grade_high'][user_language])
    if grade > 0.4:
        return np.random.choice(l10n['grade_avg'][user_language])
    else:
        return np.random.choice(l10n['grade_low'][user_language])


def compute_grade(actual: str, reference_joke: Joke, user_language: JokeLanguages) -> (str, float, Optional[str]):
    """
    :return: (grade_method, grade, feedback)
    """

    bert_grade = vector_similarity(normalize_text(actual), normalize_text(reference_joke.text_end))
    if bert_grade > 0.85:
        return JokeGradeMethod.BERT, bert_grade, None
    try:
        if user_language != JokeLanguages.EN:
            result = translate([reference_joke.text_begin, actual], 'en')
        else:
            result = [reference_joke.text_begin, actual]

        grade, feedback = chat_gpt_grade(result[0], result[1])
        return JokeGradeMethod.GPT, grade, feedback
    except Exception as e:
        print(f"\n[ERROR] function: compute_grade, ChatGPT error: {e}\n")

        return JokeGradeMethod.BERT, bert_grade, None


def normalize_text(text: str):
    return text.lower().translate(str.maketrans('', '', punctuation)).strip()
