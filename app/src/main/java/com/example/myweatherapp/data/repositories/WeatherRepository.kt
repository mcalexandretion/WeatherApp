package com.example.myweatherapp.data.repositories

import com.example.myweatherapp.data.api.CityApiService
import com.example.myweatherapp.data.api.WeatherApiService
import com.example.myweatherapp.data.models.CityCoordinatesResponse
import com.example.myweatherapp.data.models.WeatherForecastResponse

class WeatherRepository(
    private val cityApiService: CityApiService,
    private val weatherApiService: WeatherApiService
) {

    // Получение координат города
    suspend fun getCityCoordinates(cityName: String): List<CityCoordinatesResponse> {
        return cityApiService.getCityCoordinates(cityName)
    }

    // Получение прогноза погоды по координатам
    suspend fun getWeatherForecast(latitude: Double, longitude: Double): WeatherForecastResponse {
        return weatherApiService.getWeatherForecast(latitude, longitude)
    }
}
