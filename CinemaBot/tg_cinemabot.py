import asyncio
from aiogram import Bot, types
from aiogram.dispatcher import Dispatcher
from aiogram.utils import executor
import imdb
from flask import Flask
from typing import Any
from kinopoisk.movie import Movie
import urllib.parse
import urllib.request
import re
import pytube
import os

BOT_TOKEN = "######################################"
admin_id = '##########'

loop = asyncio.get_event_loop()
bot = Bot(token=BOT_TOKEN, parse_mode='Markdown')
server = Flask(__name__)
dp = Dispatcher(bot, loop=loop)
moviesDB = imdb.IMDb()


async def send_to_admin(*args: Any) -> None:
    await bot.send_message(chat_id=admin_id, text="Bot started.")


@dp.message_handler(commands=['start', 'help'])
async def send_welcome(message: types.Message) -> None:
    st1 = "\nFind movie: /search <title>"
    st2 = "\nFind person: /person <name>"
    await message.reply("Hi!\nI'm CinemaBot!\nI know everything about cinema world.\n" + st1 + st2)


async def search_movie_imdb(movie_name: Any, chat_id: Any) -> None:
    movies_result = moviesDB.search_movie(movie_name)
    if len(movies_result) == 0:
        await bot.send_message(chat_id=chat_id, text="Nothing found :(")
        return

    movie = movies_result[0]
    try:
        moviesDB.update(movie, info=['main'])

        if 'full-size cover url' in movie.keys():
            await bot.send_photo(chat_id=chat_id, photo=movie['full-size cover url'])
        elif 'cover url' in movie.keys():
            await bot.send_photo(chat_id=chat_id, photo=movie['cover url'])

    except Exception:
        pass

    if 'title' in movie.keys():
        await bot.send_message(chat_id=chat_id, text="*Title:* {}".format(movie['title']))
    if 'year' in movie.keys():
        await bot.send_message(chat_id=chat_id, text="*Year:* {}".format(movie['year']))
    if 'kind' in movie.keys():
        await bot.send_message(chat_id=chat_id, text="*Kind:* {}".format(movie['kind']))

    try:
        moviesDB.update(movie, info=['plot'])
        if 'plot' in movie.keys():
            await bot.send_message(chat_id=chat_id, text="*Plot:* _{}_".format(movie['plot'][0]))
    except Exception:
        pass

    try:
        moviesDB.update(movie, info=['vote details', 'reviews'])
        if 'rating' in movie.keys():
            await bot.send_message(chat_id=chat_id, text="*Rating:* {}".format(movie['rating']))
        if 'director' in movie.keys():
            director = movie['director'][0]['name']
            await bot.send_message(chat_id=chat_id, text="*Director:* {}".format(director))
        if 'producers' in movie.keys():
            producers = '\n'.join([x['name'] for x in movie['producers'][:3]])
            await bot.send_message(chat_id=chat_id, text="*Producers:* \n{}".format(producers))
        if 'cast' in movie.keys():
            actors = '\n'.join([x['name'] + " _(" + str(x.currentRole) + ")_" for x in movie['cast'][:10]])
            await bot.send_message(chat_id=chat_id, text="*Actors:* \n{}".format(actors))
        if 'reviews' in movie.keys():
            reviews = movie.get('reviews', [])
            if reviews and len(reviews):
                await bot.send_message(chat_id=chat_id, text="*Reviews:* \n")
                for review in reviews[:5]:
                    content = review['content'][:100]
                    await bot.send_message(chat_id=chat_id, text="_{}..._\n".format(content))
    except Exception:
        pass

    try:
        moviesDB.update(movie, info=['video clips'])
        video_clips = movie.get('video clips and trailers', [])
        if video_clips and len(video_clips):
            url = video_clips[0][1]
            url = "".join(url.split("\t"))
            url = "".join(url.split("\n"))
            if url.startswith("https:"):
                url = "http" + url[5:]
            bot.__setattr__('parse_mode', None)
            await bot.send_message(chat_id=chat_id, text="Trailer link: {}".format(url))
            bot.__setattr__('parse_mode', 'Markdown')
    except Exception:
        pass


async def search_movie_kinopoisk(movie_name: Any, chat_id: Any) -> None:
    movies_result = Movie.objects.search(movie_name)
    if len(movies_result) == 0:
        await bot.send_message(chat_id=chat_id, text="Nothing found :(")
        return

    movie = movies_result[0]
    try:
        movie.get_content('posters')
        if len(movie.posters):
            await bot.send_photo(chat_id=chat_id, photo=movie.posters[0])
    except Exception:
        pass

    if movie.title:
        await bot.send_message(chat_id=chat_id, text="*Title:* {}".format(movie.title))
    if movie.year:
        await bot.send_message(chat_id=chat_id, text="*Year:* {}".format(movie.year))

    try:
        movie.get_content('main_page')
        if movie.plot:
            await bot.send_message(chat_id=chat_id, text="*Plot:* {}".format(movie.plot))
        if movie.rating:
            await bot.send_message(chat_id=chat_id, text="*Rating:* {}".format(movie.rating))
        if movie.tagline:
            await bot.send_message(chat_id=chat_id, text="*Tagline:* {}".format(movie.tagline))
        if len(movie.directors):
            await bot.send_message(chat_id=chat_id, text="*Director:* {}".format(movie.directors[0]))
        if len(movie.producers):
            producers = '\n'.join([x.name for x in movie.producers[:3]])
            await bot.send_message(chat_id=chat_id, text="*Producers:* \n{}".format(producers))
        if len(movie.actors):
            actors = '\n'.join([x.name for x in movie.actors[:10]])
            await bot.send_message(chat_id=chat_id, text="*Actors:* \n{}".format(actors))
    except Exception:
        pass

    try:
        movie.get_content('trailers')
        if movie.trailers and movie.trailers[0].is_valid:
            trailer = "https://www.kinopoisk.ru/trailer/player/share/" + \
                      str(movie.trailers[0].id) + "/?share=true"
            await bot.send_message(chat_id=chat_id, text="*Trailer link:* \n{}".format(trailer))
    except Exception:
        pass


async def show_trailer(movie_name: Any, chat_id: Any) -> None:
    try:
        query = urllib.parse.urlencode({"search_query": movie_name + " trailer"})
        content = urllib.request.urlopen("http://www.youtube.com/results?" + query)
        results = re.findall(r'href=\"\/watch\?v=(.{11})', content.read().decode())

        youtube = None
        for item in results:
            try:
                youtube = pytube.YouTube("http://www.youtube.com/watch?v=" + item)
                break
            except Exception:
                pass

        if not youtube:
            raise Exception()

        videos = youtube.streams.filter(file_extension='mp4').all()
        if not videos or not len(videos):
            raise Exception()
        videos[0].download()

        for obj in os.listdir('.'):
            if os.path.isfile(obj) and obj.endswith(".mp4"):
                with open(obj, "rb") as file:
                    await bot.send_video(chat_id=chat_id, video=file, supports_streaming=True)
                os.remove(obj)

    except Exception:
        await bot.send_message(chat_id=chat_id, text="No results found :(")


@dp.message_handler(commands=['search'])
async def search(message: types.Message) -> None:
    movie_name = message.text[8:]
    chat_id = message.from_user.id

    await bot.send_message(chat_id=chat_id, text="*Searching for '{}' in imdb*...".format(movie_name))
    await search_movie_imdb(movie_name=movie_name, chat_id=chat_id)

    await bot.send_message(chat_id=chat_id, text="*Searching for '{}' in kinopoisk*...".format(movie_name))
    await search_movie_kinopoisk(movie_name=movie_name, chat_id=chat_id)

    await bot.send_message(chat_id=chat_id, text="*Loading trailer*...".format(movie_name))
    await show_trailer(movie_name=movie_name, chat_id=chat_id)

    await bot.send_message(chat_id=chat_id, text="--------------end----------------")


async def search_person_imdb(person_name: Any, chat_id: Any) -> None:
    people = moviesDB.search_person(person_name)
    if not len(people):
        await bot.send_message(chat_id=chat_id, text="Nothing found :(")
        return

    person = people[0]
    try:
        moviesDB.update(person, info=['main'])
        if 'full-size headshot' in person.keys():
            await bot.send_photo(chat_id=chat_id, photo=person['full-size headshot'])
        if 'name' in person.keys():
            await bot.send_message(chat_id=chat_id, text="*Name:* {}".format(person['name']))
        if 'birth info' in person.keys() and 'birth place' in person['birth info'].keys():
            await bot.send_message(chat_id=chat_id, text="*Born:* {}".format(person['birth info']['birth place']),
                                   parse_mode=None)
    except Exception:
        pass

    try:
        moviesDB.update(person, info=['biography'])
        if 'mini biography' in person.keys():
            await bot.send_message(chat_id=chat_id, text="*Biography:* {}".format(person['mini biography']))
    except Exception:
        pass
    try:
        moviesDB.update(person, info=['filmography'])
        if 'filmography' in person.keys():
            await bot.send_message(chat_id=chat_id, text="*Career*: \n")
            for key, val in person['filmography'].items():
                await bot.send_message(chat_id=chat_id,
                                       text="*As " + str(key) + ":*\n" + "\n".join(str(x) for x in val[:7]))
        if 'trade mark' in person.keys():
            mark = '\n'.join(person['trade mark'])
            await bot.send_message(chat_id=chat_id, text="*Trade mark:* \n{}".format(mark))
    except Exception as e:
        await bot.send_message(chat_id=chat_id, text="*wtf* {0}:".format(e))
        pass

@dp.message_handler(commands=['person'])
async def person(message: types.Message) -> None:
    person_name = message.text[8:]
    chat_id = message.from_user.id

    await bot.send_message(chat_id=chat_id, text="*Searching for '{}'*...".format(person_name))
    await search_person_imdb(person_name=person_name, chat_id=chat_id)
    await bot.send_message(chat_id=chat_id, text="--------------end----------------")


@dp.message_handler()
async def echo(message: types.Message) -> None:
    await message.reply("What do you mean?")


if __name__ == '__main__':
    executor.start_polling(dp, on_startup=send_to_admin)

