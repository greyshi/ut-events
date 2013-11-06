package com.utevents;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class EventListActivity extends Activity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	private MenuItem mRefreshButton;
	private final static String CATEGORIES_URI = "http://utevents.herokuapp.com/categories";
	private ArrayList<Integer> mCategoryIds = new ArrayList<Integer>();
	private HashMap<Integer, Category> mCategories = new HashMap<Integer, Category>();

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
		
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
	    EventListFragment fragment = new EventListFragment();

	    // Insert the fragment by replacing any existing fragment
	    FragmentManager fragmentManager = getFragmentManager();
	    fragmentManager.beginTransaction()
	                   .replace(R.id.content_frame, fragment)
	                   .commit();
	    
	    new FetchCategoriesTask(mDrawerList).execute();
	    
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
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;    
			case R.id.refresh:
				EventListFragment fragment = new EventListFragment();
				FragmentManager fragmentManager = getFragmentManager();
			    fragmentManager.beginTransaction()
			                   .replace(R.id.content_frame, fragment)
			                   .commit();
				break;
			case R.id.search:
				break;
			default:
		}
			

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.event_list, menu);
		mRefreshButton = menu.findItem(R.id.refresh);
		return true;
	}
	
	public void setHomeStatus(boolean home) {
		mRefreshButton.setVisible(home);
		mDrawerToggle.setDrawerIndicatorEnabled(home);
		if(!home) {
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		} else {
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//turn on the Navigation Drawer image; this is called in the LowerLevelFragments
		setHomeStatus(true);
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
//	        selectItem(position);
	    }
	}
	
	private int fetchCategories() throws Exception {
			BufferedReader response;
			String responseLine;
			StringBuilder responseString;

			URL url = new URL(CATEGORIES_URI);
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
				return 0;
			}

			// TODO: Support XML. Unmarshal XML from responseString into Event objects and
			//       stuff those objects into events. (SAX)
			// 		 Xml.parse(responseString.toString(), null);

			mCategoryIds.clear();
			JSONArray jsonEvents = new JSONArray(responseString.toString());
			for (int i = 0; i < jsonEvents.length(); ++i) {
				JSONObject category = jsonEvents.getJSONObject(i);
				JSONObject categoryFields = category.getJSONObject("fields");
				int cid = category.getInt("pk");
				// TODO: Handle optional fields (JSONException thrown if a JSONObject
				//       can't find a value for a key.
				mCategoryIds.add(cid);
				mCategories.put(cid, new Category(cid, categoryFields.getString("title"), categoryFields.getString("color")));
			}

			return 1;

	}

	private class FetchCategoriesTask extends AsyncTask<Void, Void, Integer> {
		/** The system calls this to perform work in a worker thread and
		 * delivers it the parameters given to AsyncTask.execute() */

		private ListView mListView;

		FetchCategoriesTask(ListView listView) {
			mListView = listView;
		}

		protected Integer doInBackground(Void... voids) {
			try {
				return fetchCategories();
			} catch (Exception e) {
				// TODO: Error handling
				Log.d("wut", e.toString());
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(result == 1) {
				// Translate the events into view(s) (TextViews with background colors
				// and specific formatting? width=fill_parent, add side padding, length=1 or
				// whatever weight works to fix x events on a page) The parent View for the
				// events should be scrollable (ListView).
				// NOTE: To use something other than TextViews for the array display, for instance, ImageViews, 
				//       or to have some of data besides toString() results fill the views, override 
				//       getView(int, View, ViewGroup) to return the type of view you want.
				mListView.setAdapter(new ColorTextAdapter(EventListActivity.this,
	                R.layout.drawer_list_item, R.id.option_text, mCategoryIds, mCategories));
				mListView.setOnItemClickListener(new DrawerItemClickListener());

				mListView.setAlpha(0f);
				mListView.setVisibility(View.VISIBLE);

				mListView.animate()
				.alpha(1f)
				.setDuration(300)
				.setListener(null);

			}
		}
	}

}
