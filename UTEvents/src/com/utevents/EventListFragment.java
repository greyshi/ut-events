package com.utevents;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class EventListFragment extends Fragment {

	private ArrayList<Event> events = new ArrayList<Event>();
	private ArrayList<Event> filteredEvents = new ArrayList<Event>();

	private final static String EVENTS_URI = "http://utevents.herokuapp.com/api/v1/events/";
	private View view;
	private boolean mLoaded = false;
	private static final Integer OK_LOADED = 1;
	private static final Integer CONN_FAIL = -1;
	private ListView listView;
	private TextView fetchingText;
	public static final Integer CATEGORIES_ALL = 0;
	private int mCategory = CATEGORIES_ALL;
	private EventListActivity mParent;
	private CharSequence mTitle = "UT Events";
	private TextView failedText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_event_list, container, false);

		listView = (ListView) view.findViewById(R.id.content_list);
		fetchingText = (TextView) view.findViewById(R.id.fetching_text);
		failedText = (TextView) view.findViewById(R.id.failed_text);
		
		mParent = ((EventListActivity)getActivity());

		if(mLoaded) {
			listView.setAdapter(new EventCardAdapter(view.getContext(), R.layout.list_item, filteredEvents));
			listView.setOnItemClickListener(new ListItemClickListener());
		}
		return view;
	}

	public void asyncFetch() {
		// Make an API call to our web service to get the events
		try {
			mLoaded = false;
			listView.setVisibility(View.GONE);
			failedText.setVisibility(View.GONE);
			fetchingText.setAlpha(1f);
			fetchingText.setVisibility(View.VISIBLE);
			new FetchEventsTask(listView, fetchingText).execute();
		} catch (Exception e) {
			// TODO: Error handling
			Log.d("wut", e.toString());
		}
	}

	// A helper method to make a call to our web API and return a list of events.
	private int fetchEvents() throws Exception {
		if(!mLoaded) {

			BufferedReader response;
			String responseLine;
			StringBuilder responseString = null;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);

			URL url = new URL(EVENTS_URI + "?start_time__gte=" + dateFormat.format(new Date()));
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			
			for (byte i = 0; i < 3; ++i) {
				try {
					if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
						response = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
						responseString = new StringBuilder();
						while ((responseLine = response.readLine()) != null) {
							responseString.append(responseLine);
						}
		
						response.close();
					} else {
						return CONN_FAIL;
					}
					
					break;
				} catch (SocketTimeoutException ste) {
					// Could not connect/read to/from server
					if (i >= 2) return CONN_FAIL;
				} catch (UnknownHostException e) {
					if (i >= 2) return CONN_FAIL;
				}
			}

			// TODO: Support XML. Unmarshal XML from responseString into Event objects and
			//       stuff those objects into events. (SAX)
			// 		 Xml.parse(responseString.toString(), null);

			events.clear();
			JSONObject objects = new JSONObject (responseString.toString());
			JSONArray jsonEvents = objects.getJSONArray("objects");
			for (int i = 0; i < jsonEvents.length(); ++i) {
				JSONObject event = jsonEvents.getJSONObject(i);
				JSONArray catArray = event.getJSONArray("categories");
				ArrayList<Integer> categories = new ArrayList<Integer>();
				for(int k = 0;  k < catArray.length(); k++) {
					String[] cat = catArray.getString(k).split("/");
					categories.add(Integer.parseInt(cat[4]));
				}

				Date endDate = null;
				String description = null;
				try {
					endDate = dateFormat.parse(event.getString("end_time"));
				} catch (Exception e) {

				}
				try {
					description = event.getString("description");
				} catch (JSONException e) {

				}
				// TODO: Handle optional fields (JSONException thrown if a JSONObject
				//       can't find a value for a key.
				events.add(new Event(
						event.getString("title"),
						categories,
						mParent.getCategoryColor(categories.get(0)),
						event.getString("location"),
						dateFormat.parse(event.getString("start_time")),
						endDate,
						description
						));
			}

			mLoaded = true;
			return OK_LOADED;
		}

		return 0;

	}

	private class FetchEventsTask extends AsyncTask<Void, Void, Integer> {
		/** The system calls this to perform work in a worker thread and
		 * delivers it the parameters given to AsyncTask.execute() */

		private ListView mListView;
		private TextView mLoading;

		FetchEventsTask(ListView listView, TextView fetchingText) {
			mListView = listView;
			mLoading = fetchingText;
		}

		protected Integer doInBackground(Void... voids) {
			try {
				return fetchEvents();
			} catch (Exception e) {
				// TODO: Error handling
				Log.d("wut", e.toString());
				return CONN_FAIL;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(result == OK_LOADED) {
				// Translate the events into view(s) (TextViews with background colors
				// and specific formatting? width=fill_parent, add side padding, length=1 or
				// whatever weight works to fix x events on a page) The parent View for the
				// events should be scrollable (ListView).
				// NOTE: To use something other than TextViews for the array display, for instance, ImageViews, 
				//       or to have some of data besides toString() results fill the views, override 
				//       getView(int, View, ViewGroup) to return the type of view you want.
				filterEvents();
				mListView.setAdapter(new EventCardAdapter(view.getContext(), R.layout.list_item, filteredEvents));
				mListView.setOnItemClickListener(new ListItemClickListener());

				mListView.setAlpha(0f);
				mListView.setVisibility(View.VISIBLE);

				mListView.animate()
				.alpha(1f)
				.setDuration(300)
				.setListener(null);

				mLoading.animate()
				.alpha(0f)
				.setDuration(300)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						mLoading.setVisibility(View.GONE);
					}
				});
			} else if (result == CONN_FAIL) {
				failedText.setAlpha(0f);
				failedText.setVisibility(View.VISIBLE);
				
				failedText.animate()
				.alpha(1f)
				.setDuration(300)
				.setListener(null);
				
				mLoading.animate()
				.alpha(0f)
				.setDuration(300)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						mLoading.setVisibility(View.GONE);
					}
				});
			}
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
		// Create a new fragment
		Fragment fragment = new EventDetailsFragment();
		Bundle args = new Bundle();
		args.putSerializable("current_event", filteredEvents.get(position));
		fragment.setArguments(args);

		// Insert the fragment by replacing any existing fragment
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.addToBackStack(null)
		.commit();

		mParent.navigate("Details");
	}

	public void search(String keyword) {
		filteredEvents.clear();
		String ciKey = keyword.toLowerCase();
		for(Event e : events) {
			if(e.getTitle().toLowerCase().contains(ciKey)) {
				filteredEvents.add(e);
			}
		}
		
		getFragmentManager().beginTransaction()
		.replace(R.id.content_frame, this)
		.addToBackStack(null)
		.commit();

		mParent.navigate("Searched For: \"" + keyword + "\"");
		listView.setAdapter(new EventCardAdapter(view.getContext(), R.layout.list_item, filteredEvents));
		listView.setOnItemClickListener(new ListItemClickListener());
	}
	
	public void setCategoryFilter(int category) {
		mCategory = category;
		filterEvents();
		listView.setAdapter(new EventCardAdapter(view.getContext(), R.layout.list_item, filteredEvents));
		listView.setOnItemClickListener(new ListItemClickListener());
	}
	
	public int getCategoryFilter() {
		return mCategory;
	}

	public void filterEvents() {
		if(mCategory == CATEGORIES_ALL) {
			filteredEvents = new ArrayList<Event>(events);
		} else {
			filteredEvents.clear();
			for(Event e : events) {
				if(e.inCategory(mCategory)) {
					filteredEvents.add(e);
				}
			}
		}
	}
}
