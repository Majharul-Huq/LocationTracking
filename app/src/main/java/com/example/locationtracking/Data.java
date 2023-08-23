package com.example.locationtracking;

import com.google.gson.annotations.SerializedName;

public class Data{

	@SerializedName("message")
	private String message;

	public String getMessage(){
		return message;
	}
}