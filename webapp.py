import os
from flask import Flask

app = Flask(__name__)

@app.route('/')
def hello():
    return 'TODO: Splash page!'

@app.route('/submission/')
def submission():
    return 'TODO: Submission form'

@app.route('/about/')
def submission():
    return 'TODO: About page'