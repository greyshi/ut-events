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
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EventListActivity extends Activity {

	private final static String EVENTS_URI = "http://utevents.herokuapp.com/events";
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_list);

		mTitle = getTitle();
		mDrawerTitle = "Categories";
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
				R.string.drawer_open,  /* "open drawer" description */
				R.string.drawer_close  /* "close drawer" description */
				) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ColorTextAdapter(this,
                R.layout.drawer_list_item, R.id.option_text, getResources().getStringArray(R.array.test)));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

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
		// NOTE: To use something other than TextViews for the array display, for instance, ImageViews, 
		//       or to have some of data besides toString() results fill the views, override 
		//       getView(int, View, ViewGroup) to return the type of view you want.
		eventsArray = events.toArray(new Event[events.size()]);
		listView.setAdapter(new ArrayAdapter<Event>(this, R.layout.list_item, eventsArray));

		// Replace the initial TextView with the new TextViews created from the
		// data fetched from the database.
		fetchingText.setVisibility(View.GONE); // Remove the starter text
		listView.setVisibility(View.VISIBLE); // Display list view
	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
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
			return new ArrayList<Event>();
		}

		// TODO: Support XML. Unmarshal XML from responseString into Event objects and
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
				return new ArrayList<Event>();
			}
		}
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
//	        selectItem(position);
	    }
	}

	/** Swaps fragments in the main content view */
//	private void selectItem(int position) {
//	    // Create a new fragment and specify the planet to show based on position
//	    Fragment fragment = new PlanetFragment();
//	    Bundle args = new Bundle();
//	    args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
//	    fragment.setArguments(args);
//
//	    // Insert the fragment by replacing any existing fragment
//	    FragmentManager fragmentManager = getFragmentManager();
//	    fragmentManager.beginTransaction()
//	                   .replace(R.id.content_frame, fragment)
//	                   .commit();
//
//	    // Highlight the selected item, update the title, and close the drawer
//	    mDrawerList.setItemChecked(position, true);
//	    setTitle(mPlanetTitles[position]);
//	    mDrawerLayout.closeDrawer(mDrawerList);
//	}

}
