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
		
		Event event = (Event) getArguments().getSerializable("current_event");
		titleView.setText(event.getTitle());
		return view;
	}
}
