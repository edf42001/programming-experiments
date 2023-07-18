import os
from dotenv import load_dotenv

import openai

load_dotenv()

openai.api_key = os.getenv("OPENAI_API_KEY")

response = openai.ChatCompletion.create(
  model="gpt-3.5-turbo",
  messages=[
        {"role": "system", "content": "You are SVG Bot, a drawing assistant. Create an accurate drawing of the user's prompt with SVG notation. Respond inside of <svg> tags, with nothing else. Include XML comments in the SVG to describe each component of your drawing."},
        {"role": "user", "content": "A round front-facing dog"},
    ]
)

print(response['choices'][0]['message']['content'])
