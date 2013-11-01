package com.utevents;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class EventListFragment extends Fragment {

	private ArrayList<Event> events = new ArrayList<Event>();
	private final static String EVENTS_URI = "http://utevents.herokuapp.com/events";
	private View view;
	private boolean mLoaded = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_event_list, container, false);

		ListView listView = (ListView) view.findViewById(R.id.content_list);

		try {
			new FetchEventsTask().execute().get();
		} catch (Exception e) {
			//TODO
		}

		// Translate the events into view(s) (TextViews with background colors
		// and specific formatting? width=fill_parent, add side padding, length=1 or
		// whatever weight works to fix x events on a page) The parent View for the
		// events should be scrollable (ListView).
		// NOTE: To use something other than TextViews for the array display, for instance, ImageViews, 
		//       or to have some of data besides toString() results fill the views, override 
		//       getView(int, View, ViewGroup) to return the type of view you want.
		listView.setAdapter(new ArrayAdapter<Event>(view.getContext(), R.layout.list_item, events));
		listView.setOnItemClickListener(new ListItemClickListener());

		// Replace the initial TextView with the new TextViews created from the
		// data fetched from the database.


		listView.setVisibility(View.VISIBLE);
		return view;
	}

	public void asyncFetch() {
		// Make an API call to our web service to get the events
		try {
			mLoaded = false;
			new FetchEventsTask().execute();
		} catch (Exception e) {
			// TODO: Error handling
			Log.d("wut", e.toString());
		}
	}

	// A helper method to make a call to our web API and return a list of events.
	private void fetchEvents() throws Exception {
		if(!mLoaded) {
			TextView fetchingText = (TextView) view.findViewById(R.id.fetching_text);

			fetchingText.setVisibility(View.VISIBLE);


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
				return;
			}

			// TODO: Support XML. Unmarshal XML from responseString into Event objects and
			//       stuff those objects into events. (SAX)
			// 		 Xml.parse(responseString.toString(), null);

			events.clear();
			JSONArray jsonEvents = new JSONArray(responseString.toString());
			for (int i = 0; i < jsonEvents.length(); ++i) {
				JSONObject event = jsonEvents.getJSONObject(i);
				JSONObject eventFields = event.getJSONObject("fields");
				// TODO: Handle optional fields (JSONException thrown if a JSONObject
				//       can't find a value for a key.
				events.add(new Event(
						eventFields.getString("title"),
						eventFields.getString("location"),
						new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(eventFields.getString("start_time"))
						));
			}

			mLoaded = true;
			fetchingText.setVisibility(View.GONE);
		}
		// // Display list view

	}

	private class FetchEventsTask extends AsyncTask<Void, Void, Integer> {
		/** The system calls this to perform work in a worker thread and
		 * delivers it the parameters given to AsyncTask.execute() */
		protected Integer doInBackground(Void... voids) {
			try {
				fetchEvents();
			} catch (Exception e) {
				// TODO: Error handling
				Log.d("wut", e.toString());
			}
			return 1;
		}
	}



	private class ListItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			selectEvent(position);
		}
	}

	/** Swaps fragments in the main content view */
	private void selectEvent(int position) {
		// Create a new fragment and specify the planet to show based on position
		Fragment fragment = new EventDetailsFragment();
		Bundle args = new Bundle();
		args.putSerializable("current_event", events.get(position));
		fragment.setArguments(args);

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.addToBackStack(null)
		.commit();
	}
}
