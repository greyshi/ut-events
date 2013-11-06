package com.utevents;

import java.io.Serializable;

public class Category implements Serializable {

	private int id;
	private String title;
	private int color;
	
	Category (int id, String title, String color) {
		this.id = id;
		this.title = title;

		if(color.length() == 6) {
			int r = parseHex(color.substring(0,2));
			int g = parseHex(color.substring(2,4));
			int b = parseHex(color.substring(4,6));
			this.color = 0xFF000000 | r << 16 | g << 8 | b;
		} else {
			this.color = 0xFF000000;
		}
	}
	
	private int parseHex(String hex) {
		try {
			return Integer.parseInt(hex, 16);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
	public String getTitle() {
		return title;
	}
	
	public int getColor() {
		return color;
	}
	
	public int getId() {
		return id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String toString() {
		// TODO: Implement
		return title;
	}
	
}
