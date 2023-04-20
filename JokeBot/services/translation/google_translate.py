from typing import List

from google.cloud import translate_v2 as translate
from google.oauth2 import service_account
import os

credentials = service_account.Credentials.from_service_account_file(os.getenv('GOOGLE_TRANSLATE_CONFIG'))

translate_client = translate.Client(credentials=credentials)


def translate(text: List[str], target_lan, model='nmt') -> [str]:

    if isinstance(text, bytes):
        text = text.decode("utf-8")

    result = translate_client.translate(text, target_language=target_lan, model=model)

    return [trans['translatedText'] for trans in result]
