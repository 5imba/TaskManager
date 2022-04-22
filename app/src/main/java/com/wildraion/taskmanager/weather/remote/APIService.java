package com.wildraion.taskmanager.weather.remote;

import com.wildraion.taskmanager.weather.WeatherModel;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {

    @GET("forecast")
    Single<WeatherModel> getWeatherModel(@Query("lat") String lat,
                                         @Query("lon") String lon,
                                         @Query("appid") String appid,
                                         @Query("units") String units);

}
