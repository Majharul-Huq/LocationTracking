package com.example.locationtracking.api;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiEndPoint {

    @POST("location/login")
    @FormUrlEncoded
    Call<ResponseBody> login(@Field("id") String id,
                             @Field("password") String password);

    @POST("location/store")
    @FormUrlEncoded
    Call<ResponseBody> storeLocation(@Field("latitude") double latitude,
                                     @Field("longitude") double longitude,
                                     @Field("address") String address,
                                     @Header("Authorization") String accessToken);


}