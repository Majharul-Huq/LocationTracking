package com.example.locationtracking;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiEndPoint {

    @POST("location/login")
    @FormUrlEncoded
    Call<ResponseBody> login(@Field("id") String id,
                             @Field("password") String password);

    @POST("store-location")
    @FormUrlEncoded
    Call<LocationResponse> storeLocation(@Field("name") String name,
                                         @Field("device") String device,
                                         @Field("latitude") double latitude,
                                         @Field("longitude") double longitude);


}