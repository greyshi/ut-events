package com.utevents;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Event implements Serializable {

	private String title;
	private String location;
	private String description;
	private ArrayList<Integer> categories;
	private int[] colors;
	private Date startTime;
	private Date endTime;
	
	Event (String title, ArrayList<Integer> categories, int[] colors, String location, Date startTime, Date endTime, String description) {
		this.title = title;
		this.categories = categories;
		this.colors = colors;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
	}
	
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
	
	public ArrayList<Integer> getCategories() {
		return categories;
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
	
	public int[] getColors() {
		return colors;
	}
	
	public String toString() {
		// TODO: Implement
		return title + " @ " + location;
	}
	
}
