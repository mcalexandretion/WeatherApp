package com.example.myweatherapp.data.api

import com.example.myweatherapp.data.models.WeatherForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("hourly") hourly: String = "temperature_2m,precipitation,wind_speed_10m,weather_code",
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max",
        @Query("timezone") timezone: String = "auto",  // Use 'auto' to get the local timezone
        @Query("forecast_days") forecastDays: Int = 7 // Default is 7 days forecast
    ): WeatherForecastResponse
}

//interface WeatherApiService {
//    @GET("v1/forecast")
//    suspend fun getWeatherForecast(
//        @Query("latitude") latitude: Double,
//        @Query("longitude") longitude: Double,
//        @Query("hourly") hourly: String = "temperature_2m,precipitation,wind_speed_10m,weather_code",
//        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,precipitation_sum,wind_speed_10m_max",
//        @Query("timezone") timezone: String = "auto"
//    ): WeatherForecastResponse

//    @GET("forecast")
//    suspend fun getWeatherForecast(
//        @Query("latitude") latitude: Double,
//        @Query("longitude") longitude: Double,
//        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min",  // Добавляем запрос для дневных температур
//        @Query("forecast_days") forecastDays: Int = 7 // Указываем количество дней (например, 7)
//    ): WeatherForecastResponse

