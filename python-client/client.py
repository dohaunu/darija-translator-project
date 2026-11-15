"""CLI client: pip install requests; python client.py 'Hello, how are you?'"""
import argparse
import os
import requests

parser = argparse.ArgumentParser(description='Translate text to Moroccan Darija')
parser.add_argument('text', help='Text to translate')
parser.add_argument('--language', default='English')
args = parser.parse_args()
url = os.getenv('TRANSLATOR_URL', 'http://localhost:8080/darija-translator/api/translate')
username = os.getenv('TRANSLATOR_USER', 'translator')
password = os.getenv('TRANSLATOR_PASSWORD')
if not password:
    parser.error('Set TRANSLATOR_PASSWORD environment variable')
try:
    response = requests.post(url, json={'text': args.text, 'sourceLanguage': args.language}, auth=(username, password), timeout=45)
    response.raise_for_status()
    print(response.json()['translation'])
except (requests.RequestException, KeyError, ValueError) as error:
    parser.exit(1, f'Translation failed: {error}\n')
