package com.utevents;

import java.io.Serializable;
import java.util.Date;

public class Category implements Serializable {

	private String title;
	private String color;
	
	Category (String title, String color) {
		this.title = title;
		this.color = color;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getColor() {
		return color;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setColor(String color) {
		this.color = color;
	}
	
	public String toString() {
		// TODO: Implement
		return title;
	}
	
}
