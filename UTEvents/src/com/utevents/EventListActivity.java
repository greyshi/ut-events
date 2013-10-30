package com.utevents;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EventListActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_list);

		Event[] eventsArray;
		
		ArrayList<Event> events = new ArrayList<Event>();
		
		final TextView fetchingText = (TextView) findViewById(R.id.fetching_text);
		final ListView listView = (ListView) findViewById(R.id.content_list);
		
		// Initially, a single TextView should display 'Fetching events...'

		// Make an API call to our web service to get the events
		try {
			events = fetchEvents();
		} catch (Exception e) {
			// TODO: Error handling
		}
		
		// Translate the events into view(s) (TextViews with background colors
		// and specific formatting? width=fill_parent, add side padding, length=1 or
		// whatever weight works to fix x events on a page) The parent View for the
		// events should be scrollable (ListView).
		
		//listView.setAdapter(ArrayAdapter.createFromResource(this, R.array.test, R.layout.list_item));
		eventsArray = events.toArray(new Event[events.size()]);
		listView.setAdapter(new ArrayAdapter<Event>(this, R.layout.list_item, eventsArray));
		
		// NOTE: To use something other than TextViews for the array display, for instance, ImageViews, 
		//       or to have some of data besides toString() results fill the views, override 
		//       getView(int, View, ViewGroup) to return the type of view you want.
		
		// Replace the initial TextView with the new TextViews created from the
		// data fetched from the database.
		fetchingText.setVisibility(View.GONE); // Remove the starter text
		listView.setVisibility(View.VISIBLE); // Display list view
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.event_list, menu);
		return true;
	}
	
	// A helper method to make a call to our web API and return a list of events.
	private ArrayList<Event> fetchEvents() throws Exception {
		ArrayList<Event> events;
		BufferedReader response;
		String responseLine;
		StringBuilder responseString;
		
		URL url = new URL("http://utevents.herokuapp.com/events");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/xml");
		
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			response = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			responseString = new StringBuilder();
			while ((responseLine = response.readLine()) != null) {
				responseString.append(responseLine);
			}
			
			response.close();
		} else {
			return null;
		}
		
		events = new ArrayList<Event>();
		// TODO: Unmarshall XML from responseString into Event objects and stuff
		//       those objects into events. (JAXB)
		
		return events;
	}

}
