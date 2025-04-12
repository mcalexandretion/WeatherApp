package com.example.myweatherapp.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.myweatherapp.domain.usecases.GetCityCoordinatesUseCase
import com.example.myweatherapp.domain.usecases.GetWeatherForecastUseCase

class WeatherViewModelFactory(
    private val getCityCoordinatesUseCase: GetCityCoordinatesUseCase,
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(
                getCityCoordinatesUseCase,
                getWeatherForecastUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
