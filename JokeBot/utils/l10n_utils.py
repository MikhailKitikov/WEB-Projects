from db.models import JokeLanguages

grade_high_en = [
    "Haha, that's hilarious!",
    "Great sense of humor!",
    "You really know how to make me laugh!",
    "I'm impressed by your wit!",
    "Clever joke, I like it!",
    "That's a good one!",
    "Brilliant joke, you made my day!",
    "You're a natural comedian!",
    "That's so funny, you made me chuckle!",
    "I'm cracking up, that's hilarious!",
    "You have a great sense of humor!",
    "You're quite the jokester!",
    "That's a knee-slapper!",
    "What a witty joke, I love it!",
    "You always know how to make me laugh!",
    "You're hilarious, keep 'em coming!",
    "I can't stop laughing, that's too funny!",
    "Your jokes are always on point!",
    "You're killing me with these jokes!",
    "That's a real rib-tickler!"
]

grade_high_ru = [
    "Отличный анекдот! Очень остроумно!",
    "Хороший юмор!",
    "Вы действительно умеете меня рассмешить!",
    "Восхитительный анекдот! Молодец!",
    "Умный анекдот, мне нравится!",
    "Здорово придумано!",
    "Блестящий юмор, вы сделали мой день!",
    "Вы настоящий комик!",
    "Это так смешно, я не могу перестать смеяться!",
    "Вы мне подняли настроение!",
    "Вы имеете превосходное чувство юмора!",
    "Вы настоящий шутник!",
    "Отличный повод для смеха!",
    "Какой остроумный анекдот, мне очень понравилось!",
    "Вы всегда знаете, как меня рассмешить!",
    "Вы - просто смехотворный!",
    "Я не могу перестать смеяться, это слишком смешно!",
    "Ваши шутки всегда остроумны!",
    "Вы меня убиваете своими шутками!",
    "Это действительно забавно!"
]

grade_avg_en = [
    "It was okay, not your best though!",
    "Decent joke, but not amazing!",
    "It made me smile, but not laugh out loud!",
    "Not bad, but not great either!",
    "A little funny, but nothing special!",
    "It was okay, not very clever though!",
    "Meh, it was an average joke!",
    "It was alright, but not outstanding!",
    "Not your funniest joke, but not bad!",
    "It was average, nothing more!",
    "Could have been better, but not terrible!",
    "It was a bit ordinary, not very witty!",
    "It was alright, but not hilarious!",
    "Not your best effort, but not terrible either!",
    "It was decent, but not outstanding!",
    "It was okay, but not particularly memorable!",
    "It was a fair attempt, but not your finest!",
    "It was an okay joke, but not amazing!",
    "Not your most brilliant joke, but not bad either!",
    "It was an average joke, nothing more!"
]

grade_avg_ru = [
    "Неплохой анекдот, но не блестящий.",
    "Обычный анекдот, не более того.",
    "Нормально, но не супер.",
    "Ничего особенного, но и не плохо.",
    "Средний анекдот, но не больше.",
    "Неплохо, но не великолепно.",
    "Не самый лучший анекдот, но и не плохой.",
    "Неплохо, но не очень остроумно.",
    "Не твой лучший анекдот, но и не ужасный.",
    "Нормально, но не сильно смешно.",
    "Можно было и получше, но и не ужасно.",
    "Средний анекдот, но не больше.",
    "Неплохо, но не до смеха.",
    "Обычно, но не очень забавно.",
    "Нормально, но не более того.",
    "Неплохой анекдот, но не смешной.",
    "Средний анекдот, но не более того.",
    "Неплохо, но не весело.",
    "Нормальный анекдот, но не гениальный.",
    "Это была обычная шутка, не более того!"
]

grade_low_en = [
    "I'm not really laughing, but keep trying!",
    "Sorry, not funny. Keep working at it!",
    "I didn't find that humorous. Don't give up though!",
    "Nice effort, but it's not quite there yet.",
    "I'm not amused, but don't lose hope!",
    "It's not making me laugh, but keep practicing!",
    "Not quite funny yet, but keep at it!",
    "I'm not finding the humor in this, but don't give up!",
    "It's not hilarious, but keep working on it!",
    "I'm not laughing, but don't be discouraged!",
    "Not quite there yet, but keep trying!",
    "I'm not finding this amusing, but keep practicing!",
    "It's not quite funny, but don't give up!",
    "I'm not seeing the humor, but keep at it!",
    "Sorry, not funny. Keep practicing though!",
    "I'm not chuckling, but don't lose hope!",
    "Not making me laugh yet, but keep working on it!",
    "It's not quite humorous, but keep trying!",
    "I'm not finding it funny, but keep at it!",
    "Not quite humorous yet, but don't be discouraged!"
]

grade_low_ru = [
    "Я не смеюсь, но продолжайте стараться!",
    "Прости, не смешно. Но не отчаивайся!",
    "Я не нашел это забавным. Но не сдавайся!",
    "Хорошая попытка, но пока не получается.",
    "Я не в восторге, но не теряй надежды!",
    "Это не заставляет меня смеяться, но продолжайте тренироваться!",
    "Пока не смешно, но не останавливайтесь!",
    "Я не нахожу забавы в этом, но не отчаивайтесь!",
    "Это не очень смешно, но продолжайте работать над этим!",
    "Я не смеюсь, но не расстраивайтесь!",
    "Пока не достигнуто, но продолжайте стараться!",
    "Я не нахожу это забавным, но продолжайте тренироваться!",
    "Это пока не очень смешно, но не сдавайтесь!",
    "Я не вижу здесь юмора, но продолжайте стараться!",
    "Прости, не смешно. Но продолжай тренироваться!",
    "Я не хохочу, но не теряй надежды!",
    "Пока не смешно, но продолжайте работать над этим!",
    "Это пока не совсем забавно, но продолжайте стараться!",
    "Я не нахожу это смешным, но продолжайте работать над этим!",
    "Пока не очень забавно, но не теряйте мотивации!"
]


l10n = {
    '/feedback': {
        JokeLanguages.EN: "✏️ Leave feedback",
        JokeLanguages.RU: "✏️ Оставить отзыв",
    },
    '/hint': {
        JokeLanguages.EN: "💡 Hint",
        JokeLanguages.RU: "💡 Подсказка",
    },
    '/other_guesses': {
        JokeLanguages.EN: "🔎 See other people's guesses",
        JokeLanguages.RU: "🔎 Смотреть чужие ответы",
    },
    '/new_joke': {
        JokeLanguages.EN: "🤡 New joke",
        JokeLanguages.RU: "🤡 Новая шутка",
    },
    '/change_language': {
        JokeLanguages.EN: "🇷🇺 Change language",
        JokeLanguages.RU: "🇬🇧 Сменить язык",
    },
    'New language': {
        JokeLanguages.EN: "🇬🇧 New language",
        JokeLanguages.RU: "🇷🇺 Новый язык",
    },
    'Continue the joke': {
        JokeLanguages.EN: "Write a funny/witty sequel...",
        JokeLanguages.RU: "Напишите смешное/остроумное продолжение...",
    },
    'Sorry, no jokes left': {
        JokeLanguages.EN: "Sorry, no jokes left for now :(((",
        JokeLanguages.RU: "К сожалению, у нас пока нет новых шуток для Вас :(((",
    },
    'No hints left': {
        JokeLanguages.EN: "Sorry, no hints left",
        JokeLanguages.RU: "Увы, подсказок больше нет",
    },
    'No other guesses': {
        JokeLanguages.EN: "Sorry, no other users' guesses left",
        JokeLanguages.RU: "Увы, ответов других пользователей больше нет",
    },
    'Here is the hint': {
        JokeLanguages.EN: "Here is the hint!",
        JokeLanguages.RU: "Лови подсказку!",
    },
    'Select action': {
        JokeLanguages.EN: "Select action",
        JokeLanguages.RU: "Выберите действие",
    },
    'Thanks for your opinion': {
        JokeLanguages.EN: "Thanks for your opinion!",
        JokeLanguages.RU: "Спасибо за ваше мнение!",
    },
    'Thanks for your reaction': {
        JokeLanguages.EN: "Thanks for your reaction!",
        JokeLanguages.RU: "Спасибо за вашу реакцию!",
    },
    'Look what I came up with': {
        JokeLanguages.EN: "Look what I came up with",
        JokeLanguages.RU: "Смотри, что придумала я",
    },
    'Leave your feedback': {
        JokeLanguages.EN: "Leave your feedback:",
        JokeLanguages.RU: "Оставьте ваш отзыв:",
    },
    'Other guess': {
        JokeLanguages.EN: "Here's other user's guess:",
        JokeLanguages.RU: "Вот ответ другого пользователя:",
    },
    'Hi': {
        JokeLanguages.EN:
            "Hello! I'm Smeliana - an artificial intelligence that will help you practice and improve your humor and "
            "wit!😃 \n\n"
            "If you wonder how some people can find witty and funny answers in any situation, "
            "then in a month you will not be surprised at this.😎 \n\n"
            "Let's get started, here are some simple rules of my game:\n"
            "1️⃣ I will offer you the beginning of the situation.\n"
            "2️⃣ Your task is to write a funny / witty sequel, there is at least one for sure!\n"
            "3️⃣ After that I will evaluate your answer and offer my continuation🤩",
        JokeLanguages.RU:
            "Привет! Я Смельяна - искусственный интеллект, который поможет тебе практиковать и улучшать свой юмор и "
            "остроумие!😃 \n\n"
            "Если ты удивляешься, как некоторые могут находить остроумные и смешные ответы в любой ситуации, "
            "то через месяц ты не будешь этому удивляться.😎 \n\n"
            "Давай начнем, вот несколько простых правил моей игры:\n"
            "1️⃣ Я буду предлагать тебе начало ситуации.\n"
            "2️⃣ Твоя задача - написать смешное/остроумное продолжение, как минимум одно точно есть!\n"
            "3️⃣ После этого я оценю твой ответ и предложу свое продолжение🤩",
    },
    'grade_low': {
        JokeLanguages.EN: grade_low_en,
        JokeLanguages.RU: grade_high_ru,
    },
    'grade_avg': {
        JokeLanguages.EN: grade_avg_en,
        JokeLanguages.RU: grade_avg_ru,
    },
    'grade_high': {
        JokeLanguages.EN: grade_high_en,
        JokeLanguages.RU: grade_high_ru,
    },
}