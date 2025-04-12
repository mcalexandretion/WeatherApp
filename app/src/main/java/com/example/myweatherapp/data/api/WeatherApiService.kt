package com.example.myweatherapp.data.api

import com.example.myweatherapp.data.models.WeatherForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m",
        @Query("forecast_days") forecastDays: Int = 16
    ): WeatherForecastResponse

}
