package com.example.locationtracking;

import com.google.gson.annotations.SerializedName;

public class LocationResponse{

	@SerializedName("data")
	private Data data;

	public Data getData(){
		return data;
	}
}