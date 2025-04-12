package com.example.myweatherapp.domain.usecases

import com.example.myweatherapp.data.models.WeatherForecastResponse
import com.example.myweatherapp.data.repositories.WeatherRepository

class GetWeatherForecastUseCase(private val weatherRepository: WeatherRepository) {
    suspend fun execute(latitude: Double, longitude: Double): WeatherForecastResponse {
        return weatherRepository.getWeatherForecast(latitude, longitude)
    }
}
