package com.utevents;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EventListActivity extends Activity {
	
	private final static String EVENTS_URI = "http://utevents.herokuapp.com/events";

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
			events = new FetchEventsTask().execute().get();
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
		
		URL url = new URL(EVENTS_URI);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");
		
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
		
		// TODO: Support XML. Unmarshall XML from responseString into Event objects and
		//       stuff those objects into events. (SAX)
		// 		 Xml.parse(responseString.toString(), null);
		events = new ArrayList<Event>();
		JSONObject jsonResponse = new JSONObject(responseString.toString());
		JSONArray jsonEvents = jsonResponse.getJSONArray("events");
		for (int i = 0; i < jsonEvents.length(); ++i) {
			JSONObject event = jsonEvents.getJSONObject(i);
			// TODO: Handle optional fields (JSONException thrown if a JSONObject
			//       can't find a value for a key.
			events.add(new Event(
								event.getString("title"),
								event.getString("location"),
								new Date(event.getLong("startTime"))
							));
		}
		
		return events;
	}
	
	private class FetchEventsTask extends AsyncTask<Void, Void, ArrayList<Event> > {
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */
	    protected ArrayList<Event> doInBackground(Void... voids) {
	        try {
	        	return fetchEvents();
	        } catch (Exception e) {
	        	// TODO: Error handling
	        	return null;
	        }
	    }
	}

}
