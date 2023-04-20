import datetime
import enum

from sqlalchemy import Column, INTEGER, TIMESTAMP, Enum, TEXT, ForeignKey, FLOAT
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship
from sqlalchemy.sql import func

BaseModel = declarative_base()


class JokeGradeMethod(enum.Enum):
    BERT = 'BERT'
    GPT = 'GPT'


class JokeLanguages(enum.Enum):
    RU = 'RU'
    EN = 'EN'


class JokeReactionTypes(enum.Enum):
    LIKE = 'LIKE'
    DISLIKE = 'DISLIKE'


class FSMState(enum.Enum):
    NOTHING = 'NOTHING'
    JOKE_IN_PROGRESS = 'JOKE_IN_PROGRESS'
    FEEDBACK_IN_PROGRESS = 'FEEDBACK_IN_PROGRESS'


class User(BaseModel):
    __tablename__ = 'users'

    id = Column(INTEGER, nullable=False, autoincrement=False, primary_key=True)
    language = Column(Enum(JokeLanguages), nullable=True)
    state = Column(Enum(FSMState), nullable=False)
    created_at = Column(TIMESTAMP, server_default=func.now())


class Joke(BaseModel):
    __tablename__ = 'jokes'

    id = Column(INTEGER, autoincrement=True, primary_key=True)
    text_begin = Column(TEXT, nullable=False)
    text_end = Column(TEXT, nullable=False)
    language = Column(Enum(JokeLanguages), nullable=False)
    likes = Column(INTEGER, default=0)
    dislikes = Column(INTEGER, default=0)
    displays = Column(INTEGER, default=0)
    hints_shown = Column(INTEGER, default=0)
    created_at = Column(TIMESTAMP, server_default=func.now())


class JokeSession(BaseModel):
    __tablename__ = 'joke_sessions'

    id = Column(INTEGER, autoincrement=True, primary_key=True)
    user_id = Column(INTEGER, ForeignKey(User.id), nullable=False)
    joke_id = Column(INTEGER, ForeignKey(Joke.id), nullable=False)
    joke = relationship("Joke", backref="sessions")
    hints_shown = Column(INTEGER, default=0)
    created_at = Column(TIMESTAMP, server_default=func.now())
    ended_at = Column(TIMESTAMP, nullable=True)


class JokeGuess(BaseModel):
    __tablename__ = 'joke_guesses'

    id = Column(INTEGER, autoincrement=True, primary_key=True)
    text = Column(TEXT, nullable=False)
    user_id = Column(INTEGER, ForeignKey(User.id), nullable=False)
    joke_id = Column(INTEGER, ForeignKey(Joke.id), nullable=False)
    grade = Column(FLOAT, nullable=True)
    grade_method = Column(Enum(JokeGradeMethod), nullable=False, server_default='BERT')
    gpt_feedback = Column(TEXT, nullable=True)
    likes = Column(INTEGER, default=0)
    dislikes = Column(INTEGER, default=0)
    displays = Column(INTEGER, default=0)
    hints_shown = Column(INTEGER, default=0)
    created_at = Column(TIMESTAMP, server_default=func.now())


class JokeHint(BaseModel):
    __tablename__ = 'joke_hints'

    id = Column(INTEGER, autoincrement=True, primary_key=True)
    joke_id = Column(INTEGER, ForeignKey(Joke.id), nullable=False)
    text = Column(TEXT, nullable=False)
    created_at = Column(TIMESTAMP, server_default=func.now())


class UserFeedback(BaseModel):
    __tablename__ = 'user_feedbacks'

    id = Column(INTEGER, autoincrement=True, primary_key=True)
    user_id = Column(INTEGER, ForeignKey(User.id), nullable=False)
    text = Column(TEXT, nullable=False)
    created_at = Column(TIMESTAMP, server_default=func.now())


class JokeReaction(BaseModel):
    __tablename__ = 'joke_reactions'

    id = Column(INTEGER, autoincrement=True, primary_key=True)
    joke_id = Column(INTEGER, ForeignKey(Joke.id), nullable=False)
    user_id = Column(INTEGER, ForeignKey(User.id), nullable=False)
    reaction = Column(Enum(JokeReactionTypes), nullable=False)
    created_at = Column(TIMESTAMP, server_default=func.now())


class JokeGuessReaction(BaseModel):
    __tablename__ = 'joke_guess_reactions'

    id = Column(INTEGER, autoincrement=True, primary_key=True)
    joke_guess_id = Column(INTEGER, ForeignKey(JokeGuess.id), nullable=False)
    user_id = Column(INTEGER, ForeignKey(User.id), nullable=False)
    reaction = Column(Enum(JokeReactionTypes), nullable=False)
    created_at = Column(TIMESTAMP, server_default=func.now())
