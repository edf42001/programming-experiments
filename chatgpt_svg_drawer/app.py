import os

import openai
from flask import Flask, redirect, render_template, request, url_for

app = Flask(__name__)
openai.api_key = os.getenv("OPENAI_API_KEY")


@app.route("/", methods=("GET", "POST"))
def index():
    if request.method == "POST":
        prompt = request.form["prompt"]
        response = openai.ChatCompletion.create(
            model="gpt-3.5-turbo",
            messages=generate_messages(prompt)
        )
        return redirect(url_for("index", result=response['choices'][0]['message']['content']))

    result = request.args.get("result")
    return render_template("index.html", result=result)

def generate_messages(user_prompt):
    return [
        {"role": "system", "content": "You are SVG Bot, a drawing assistant. Create an accurate, detailed, beautiful drawing of the user's prompt with SVG notation. " +
         "Respond inside of <svg> tags, with nothing else. Include XML comments in the SVG to describe each component of your drawing."},
        {"role": "user", "content": user_prompt},
    ]
