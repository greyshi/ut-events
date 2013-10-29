package com.utevents;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class EventListActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        
        // Initially, a single TextView should display 'Fetching events...'
        
        // Make an API call to our web service to get the events
        
        // Translate the XML response into view(s) (TextViews with background colors
        // and specific formatting? width=fill_parent, add side padding, length=1 or
        // whatever weight works to fix x events on a page) The parent View for the
        // events should be scrollable (ListView).
        
        // Replace the initial TextView with the new TextViews created from the
        // data fetched from the database.
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.event_list, menu);
        return true;
    }
    
}
