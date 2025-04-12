package com.example.myweatherapp.domain.usecases


import com.example.myweatherapp.data.models.CityCoordinatesResponse
import com.example.myweatherapp.data.repositories.WeatherRepository

class GetCityCoordinatesUseCase(private val weatherRepository: WeatherRepository) {
    suspend fun execute(cityName: String): List<CityCoordinatesResponse> {
        return weatherRepository.getCityCoordinates(cityName)
    }
}
