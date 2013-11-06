Team Members: 
	Alex Yu (ayy72)
	Arash Ghoreyshi (ag37233)
	Damien Johnson (dmj559)
	
Instructions:
	After installing and starting the UTEvents app, you will be greeted by a welcoming UI
which will inform you that the app is fetching data from the network in the background
(via the "Fetching Events..." text). Note that a functional network connection is required
for the app to be of any use at this time. After the app has received the data from a
datastore on the network, a list of events will populate the main UI frame. A helpful color
will appear at the top of each event card signifying the category of that event. Each event
will be described in brief in the list view. To access more information regarding an event,
simply tap or click on the card you are interested in. Once your are in the event details
view, you may return to the list view containing all of the activities by either using the
soft back button on your Android device, or by using the "up" navigation feature accessed
by clicking the title. Back at the list view you may view all of the categories that events
are classified under by swiping from the left side of the screen to the right, or by 
clicking on the title. Tapping on a specific category will filter the events in the main
list view to display only events classified in that category. To remove the filter, simply
select the "All" category. Finally, from the main list view there are two action bar items.
The first item (which appears to be two arrows forming a circle) is used to refresh the
list of events (i.e., hitting the networked datastore and refreshing the view). The second
item (which appears to be a magnifying glass) is used to search the events.
	As an aside, to add an event to the database the following web form must be used:
http://utevents.herokuapp.com/create. The http://utevents.herokuapp.com site serves as a
web front-end and back-end server for this app.

Completed Features:
	- Events can be input (via a web form for the time being)
	- Events are listed as cards and sorted chronologically
	- Events are categorized
	- Events can be filtered by category
	- Event submissions are verified as originating from UT students
	- Searching events works

Uncompleted Features:
	- In-app event submission
	- User-defined categories
	- Calendar View for events
	- Multiple colors for event color-to-group indicators

New Features:
	- On-device event cache (for when a network is not available)
	- Timeout/Retry when a network call is sluggish

External Code:
	None

Our Code:
	All of it
		- Category.java
		- ColorTextAdapter.java
		- Event.java
		- EventCardAdapter.java
		- EventDetailsFragment.java
		- EventListActivity.java
		- EventListFragment.java