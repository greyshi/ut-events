package com.utevents;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;

public class EventListActivity extends Activity {

	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;
	private CharSequence mDefaultTitle;
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	private MenuItem mRefreshButton;
	private MenuItem mAddButton;
	private MenuItem mSearchButton;
	private final static String CATEGORIES_URI = "http://utevents.herokuapp.com/api/v1/categories/";
	private final static String CREATE_URI = "http://utevents.herokuapp.com/create";
	private ArrayList<Integer> mCategoryIds = new ArrayList<Integer>();
	private HashMap<Integer, Category> mCategories = new HashMap<Integer, Category>();
	private EventListFragment mEventList;
	private int mNavCounter = 0;
	private Stack<CharSequence> mTitles = new Stack<CharSequence>();
	private SearchView mSearchView;
	private FetchCategoriesTask fct;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_list);

		mTitle = mDefaultTitle = getTitle();
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
		
	    mEventList = new EventListFragment();

	    // Insert the fragment by replacing any existing fragment
	    FragmentManager fragmentManager = getFragmentManager();
	    fragmentManager.beginTransaction()
	                   .replace(R.id.content_frame, mEventList)
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
				if (fct == null || fct.getStatus() != AsyncTask.Status.RUNNING) {
					fct = new FetchCategoriesTask(mDrawerList);
					fct.execute();
				}
				break;
			case R.id.add_event:
				try {
					Intent i = Intent.parseUri(CREATE_URI, 0);
					startActivity(i);
				} catch (URISyntaxException urise) {
					// We should never reach this. Display error?
				}
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
		mAddButton = menu.findItem(R.id.add_event);
		mSearchButton = menu.findItem(R.id.search);
		
		// Associate searchable configuration with the SearchView
	    SearchManager searchManager =
	           (SearchManager) getSystemService(Context.SEARCH_SERVICE);
	    mSearchView = (SearchView) menu.findItem(R.id.search).getActionView();
	    mSearchView.setSearchableInfo(
	            searchManager.getSearchableInfo(getComponentName()));
	    mSearchView.setOnSearchClickListener(new OnClickListener() {
	    	@Override
	    	public void onClick(View v) {
	    		View view;
	    		
	    		view = findViewById(R.id.refresh);
	    		if (view != null) view.setVisibility(View.GONE);
	    		
	    		view = findViewById(R.id.add_event);
	    		if (view != null) view.setVisibility(View.GONE);
	    	}
	    });
	    mSearchView.setOnCloseListener(new OnCloseListener() {
	    	@Override
	    	public boolean onClose() {
	    		View view;
	    		
	    		view = findViewById(R.id.refresh);
	    		if (view != null) view.setVisibility(View.VISIBLE);
	    		
	    		view = findViewById(R.id.add_event);
	    		if (view != null) view.setVisibility(View.VISIBLE);
	    		
	    		return false;
	    	}
	    });

		return true;
	}
	
	@Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
    	
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
			for(;mNavCounter > 0; mNavCounter--) {
				getFragmentManager().popBackStack();
				mTitles.pop();
			}
			mEventList.search(query);
			mSearchView.onActionViewCollapsed();
			mSearchView.setQuery("", false);
        }
    }
	
	private void setHomeStatus(boolean home) {
		mRefreshButton.setVisible(home);
		mAddButton.setVisible(home);
		mSearchButton.setVisible(home);
		
		mDrawerToggle.setDrawerIndicatorEnabled(home);
		if(!home) {
			getActionBar().setTitle(mTitles.peek());
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		} else {
			mEventList.setCategoryFilter(mEventList.getCategoryFilter());
			getActionBar().setTitle(mTitle);
			mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
		}
	}
	
	public void navigate(String title) {
		mNavCounter++;
		mTitles.push(title);
		setHomeStatus(mNavCounter == 0);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//turn on the Navigation Drawer image; this is called in the LowerLevelFragments
		if(mNavCounter > 0) {
			mNavCounter--;
			mTitles.pop();
		}
		setHomeStatus(mNavCounter == 0); 
	}
	
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
	    @Override
	    public void onItemClick(AdapterView parent, View view, int position, long id) {
	        Category cat = mCategories.get(mCategoryIds.get(position));
	        if(cat.getId() == EventListFragment.CATEGORIES_ALL) {
	        	setTitle(mDefaultTitle);
	        } else {
	        	setTitle(cat.getTitle());
	        }
	        mEventList.setCategoryFilter(cat.getId());
	        mDrawerLayout.closeDrawers();
	    }
	}
	
	private int fetchCategories() throws Exception {
			BufferedReader response;
			String responseLine;
			StringBuilder responseString = null;

			URL url = new URL(CATEGORIES_URI);
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
						return -1;
					}
					
					break;
				} catch (SocketTimeoutException ste) {
					// Could not connect/read to/from server
					if (i >= 2) return -1;
				} 
			}

			// TODO: Support XML. Unmarshal XML from responseString into Event objects and
			//       stuff those objects into events. (SAX)
			// 		 Xml.parse(responseString.toString(), null);

			mCategoryIds.clear();
			mCategoryIds.add(EventListFragment.CATEGORIES_ALL);
			mCategories.put(EventListFragment.CATEGORIES_ALL, new Category(EventListFragment.CATEGORIES_ALL, "All", ""));
			JSONObject object = new JSONObject(responseString.toString());
			JSONArray jsonCategories = object.getJSONArray("objects");
			for (int i = 0; i < jsonCategories.length(); ++i) {
				JSONObject category = jsonCategories.getJSONObject(i);
				int cid = category.getInt("id");
				// TODO: Handle optional fields (JSONException thrown if a JSONObject
				//       can't find a value for a key.
				mCategoryIds.add(cid);
				mCategories.put(cid, new Category(cid, category.getString("title"), category.getString("color")));
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
				return -1;
			}
		}

		@Override
		protected void onPostExecute(Integer result) {
			if(result == 1) {
				// Translate the events into view(s) (TextViews with background colors
				// and specific formatting? width=fill_parent, add side padding, length=1 or
				// whatever weight works to fix x events on a page) The parent View for the
				// events should be scrollable (ListView).
				mListView.setAdapter(new ColorTextAdapter(EventListActivity.this,
	                R.layout.drawer_list_item, R.id.option_text, mCategoryIds, mCategories));
				mListView.setOnItemClickListener(new DrawerItemClickListener());

				mListView.setAlpha(0f);
				mListView.setVisibility(View.VISIBLE);

				mListView.animate()
				.alpha(1f)
				.setDuration(300)
				.setListener(null);
			} else if (result == -1) {
				// Update TextView to display connection failure
			}
			// Move this so it doesn't fetch events if fetching categories fails
			mEventList.asyncFetch();
		}
	}

	public int getCategoryColor(int category) {
		return mCategories.get(category).getColor();
	}
	
	public HashMap<Integer, Category> getAllCategories() {
		return mCategories;
	}
}
