package com.utevents;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EventDetailsFragment extends Fragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_event_details, container, false);
		TextView titleView = (TextView)view.findViewById(R.id.event_title);
		TextView locView = (TextView)view.findViewById(R.id.event_location);
		TextView stView = (TextView)view.findViewById(R.id.event_start);
		TextView etView = (TextView)view.findViewById(R.id.event_end);
		TextView descView = (TextView)view.findViewById(R.id.event_description);
		TextView catView = (TextView)view.findViewById(R.id.event_categories);
		
		Event event = (Event) getArguments().getSerializable("current_event");
		
		titleView.setText(event.getTitle());
		locView.setText("Location: " + event.getLocation());
		stView.setText("Start Time: " + event.getStartTime().toString());
		if (event.getEndTime() != null) {
			etView.setText("End Time: " + event.getEndTime().toString());
			etView.setVisibility(View.VISIBLE);
		} else {
			etView.setVisibility(View.GONE);
		}
		if (event.getDescription() != null && event.getDescription().length() > 0) {
			descView.setText("Description: " + event.getDescription());
			descView.setVisibility(View.VISIBLE);
		} else {
			descView.setVisibility(View.GONE);
		}
		// TODO: catView.setText(event.getCategories().toString());
		return view;
	}
}
