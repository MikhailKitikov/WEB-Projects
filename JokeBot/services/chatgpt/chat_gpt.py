import os
import openai


openai.api_key = os.getenv("OPENAI_API_KEY")
temperature = 0.2


def chat_gpt_grade(beginning: str, punchline: str):
    message = f"""I will provide you the beginning and punchline of the joke. 
    Grade punchline from 0 to 10, just write the number. And also give the text feedback. 
    Divide grade and feedback by new line: \n\nBeginning: "{beginning}" \nPunchline: "{punchline}" """

    message_2 = f"""Please give the text feedback"""
    completion = openai.ChatCompletion.create(
        temperature=temperature,
        model="gpt-3.5-turbo",
        messages=[
            {"role": "user", "content": message},
        ]
    )

    output = completion['choices'][0]['message']['content']

    return float(output.split('\n')[0]) / 10.0, output.split('\n')[1]

