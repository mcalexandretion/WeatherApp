package com.example.myweatherapp.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweatherapp.domain.usecases.GetCityCoordinatesUseCase
import com.example.myweatherapp.domain.usecases.GetWeatherForecastUseCase
import com.example.myweatherapp.data.models.WeatherForecastResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val getCityCoordinatesUseCase: GetCityCoordinatesUseCase,
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase
) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherForecastResponse?>(null)
    val weatherState: StateFlow<WeatherForecastResponse?> = _weatherState

    fun fetchWeatherForCity(cityName: String) {
        viewModelScope.launch {
            try {

                // Получаем список координат из City API
                val coordinatesList = getCityCoordinatesUseCase.execute(cityName)
                if (coordinatesList.isNotEmpty()) {
                    val coordinates = coordinatesList.first()
                    Log.d("WeatherViewModel", "Coordinates: Latitude = ${coordinates.latitude}, Longitude = ${coordinates.longitude}")

                    // Логируем параметры запроса для прогнозов
                    Log.d("WeatherViewModel", "Fetching weather with Latitude = ${coordinates.latitude}, Longitude = ${coordinates.longitude}")

                    // Логируем URL запроса вручную
                    val url = "https://api.open-meteo.com/v1/forecast?latitude=${coordinates.latitude}&longitude=${coordinates.longitude}&hourly=temperature_2m&forecast_days=16"
                    Log.d("WeatherViewModel", "API Call URL: $url")

                    // Получаем прогноз погоды по координатам
                    val forecast = getWeatherForecastUseCase.execute(coordinates.latitude, coordinates.longitude)
                    Log.d("WeatherViewModel", "Forecast received: ${forecast.hourly.temperature_2m[0]}")

                    _weatherState.value = forecast
                } else {
                    Log.d("WeatherViewModel", "No coordinates found for city: $cityName")
                    _weatherState.value = null
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching data", e)
                _weatherState.value = null
            }
        }
    }
}
