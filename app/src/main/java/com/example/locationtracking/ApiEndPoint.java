package com.example.locationtracking;


import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Faravy on 2/25/19.
 * faravyn@gmail.com
 */

public interface ApiEndPoint {

    @POST("store-location")
    @FormUrlEncoded
    Call<LocationResponse> postLocation(@Field("name") String name,
                                        @Field("device") String device,
                                        @Field("latitude") double latitude,
                                        @Field("longitude") double longitude);


}