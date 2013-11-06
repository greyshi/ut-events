package com.utevents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Event implements Serializable {

	private String title;
	private String location;
	private String description;
	private ArrayList<Integer> categories;
	private int color;
	private Date startTime;
	private Date endTime;
	
	Event (String title, ArrayList<Integer> categories, int color, String location, Date startTime) {
		this.title = title;
		this.categories = categories;
		this.color = color;
		this.location = location;
		this.startTime = startTime;
	}
	
	Event (String title, ArrayList<Integer> categories, int color, String location, Date startTime, Date endTime) {
		this.title = title;
		this.categories = categories;
		this.color = color;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	Event (String title, ArrayList<Integer> categories, int color, String location, Date startTime, Date endTime, String description) {
		this.title = title;
		this.categories = categories;
		this.color = color;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
	}
	
	// TODO: Fix constructors so that an Event can be created with either an endTime,
	//       a description, neither, or both.
	
	public String getTitle() {
		return title;
	}
	
	public String getLocation() {
		return location;
	}
	
	public String getDescription() {
		return description;
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	
	public boolean inCategory(int category) {
		return categories.contains(category);
	}
	
	public int getColor() {
		return color;
	}
	
	public String toString() {
		// TODO: Implement
		return title + " @ " + location;
	}
	
}
