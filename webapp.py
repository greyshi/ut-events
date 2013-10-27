import os
from flask import Flask
from flask import request
from flask import render_template

app = Flask(__name__)

@app.route('/')
def root():
    return 'TODO: Splash page!'

@app.route('/about/')
def about():
    return 'TODO: About page'

@app.route('/submit/', methods=['GET', 'POST', 'PUT'])
def submit():
    """TODO: submission form"""
    if request.method == 'POST':
        title = request.form('title')
        """Add row(s) to the database"""
    else:
        return render_template('submit.html')
    
"""This is the RESTful API call to get """
@app.route('/events/')
@app.route('/events/<int:event_id>')
def events(event_id=None):
    return 'TODO: Return XML of request event(s) from database'
    
@app.errorhandler(404)
def page_not_found(error):
    return render_template('page_not_found.html'), 404
