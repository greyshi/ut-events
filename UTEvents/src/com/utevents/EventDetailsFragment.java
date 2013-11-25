package com.utevents;

import java.text.SimpleDateFormat;
import java.util.HashMap;

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
		HashMap<Integer, Category> categories = (HashMap<Integer, Category>) getArguments().getSerializable("categories");
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, MMMMMMMMM dd, yyyy 'at' hh:mma");
		
		titleView.setText(event.getTitle());
		locView.setText(event.getLocation());
		stView.setText(dateFormat.format(event.getStartTime()));
		if (event.getEndTime() != null) {
			etView.setText(dateFormat.format(event.getEndTime()));
			etView.setVisibility(View.VISIBLE);
		} else {
			etView.setVisibility(View.GONE);
			view.findViewById(R.id.event_end_header).setVisibility(View.GONE);
		}
		if (event.getDescription() != null && event.getDescription().length() > 0) {
			descView.setText(event.getDescription().trim());
			descView.setVisibility(View.VISIBLE);
		} else {
			descView.setVisibility(View.GONE);
			view.findViewById(R.id.event_description_header).setVisibility(View.GONE);
		}
		if (event.getCategories() != null){
			StringBuilder sb = new StringBuilder();
			for (Integer i : event.getCategories()) {
				sb.append(categories.get(i) + "\n");
			}
			catView.setText(sb.toString());
		} else {
			catView.setVisibility(View.GONE);
			view.findViewById(R.id.event_categories_header).setVisibility(View.GONE);
		}
		return view;
	}
}
