package com.utevents;

import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class EventCardAdapter extends ArrayAdapter<Event> {

    private LayoutInflater mInflater;
    
    private ArrayList<Event> mEvents;
    
    private int mViewResourceId;
    
    public EventCardAdapter(Context ctx, int viewResourceId,
            ArrayList<Event> events) {
        super(ctx, viewResourceId, events);
        
        mInflater = (LayoutInflater)ctx.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        
        mEvents = events;
        
        mViewResourceId = viewResourceId;
        
    }

    @Override
    public int getCount() {
        return mEvents.size();
    }

    @Override
    public Event getItem(int position) {
        return mEvents.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mViewResourceId, null);
        
        Event event = getItem(position);
        
        View iv = (View)convertView.findViewById(R.id.color_bar);
        iv.setBackgroundColor( event.getColor() );
        
        TextView tv = (TextView)convertView.findViewById(R.id.event_title);
        tv.setText( event.getTitle() );
        
        tv = (TextView)convertView.findViewById(R.id.event_date);
        tv.setText( getMonthString(event.getStartTime().getMonth()) + " " + event.getStartTime().getDate() );
        
        tv = (TextView)convertView.findViewById(R.id.event_location);
        tv.setText( event.getLocation() );
        
        
        
        return convertView;
    }
    
    private String getMonthString(int month) {
    	switch(month) {
    	case 0:
    		return "January";
    	case 1:
    		return "February";
    	case 2:
    		return "March";
    	case 3:
    		return "April";
    	case 4:
    		return "May";
    	case 5:
    		return "June";
    	case 6:
    		return "July";
    	case 7:
    		return "August";
    	case 8:
    		return "September";
    	case 9:
    		return "October";
    	case 10:
    		return "November";
    	case 11:
    		return "December";
    	default:
    		return "null";
    	}
    }
}
