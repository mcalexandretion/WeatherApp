

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

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun fetchWeatherForCity(cityName: String) {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val coordinatesList = getCityCoordinatesUseCase.execute(cityName)
                if (coordinatesList.isNotEmpty()) {
                    val coordinates = coordinatesList.first()
                    Log.d("WeatherViewModel", "Coordinates: Latitude = ${coordinates.latitude}, Longitude = ${coordinates.longitude}")


                    val forecast = getWeatherForecast(coordinates.latitude, coordinates.longitude)
                    _weatherState.value = forecast
                } else {
                    Log.d("WeatherViewModel", "No coordinates found for city: $cityName")
                    _errorMessage.value = "No coordinates found for city"
                    _weatherState.value = null
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error fetching data: ${e.message}", e)
                _errorMessage.value = "Error fetching data: ${e.message}"
                _weatherState.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun getWeatherForecast(latitude: Double, longitude: Double): WeatherForecastResponse {
        Log.d("WeatherViewModel", "Fetching weather with Latitude = $latitude, Longitude = $longitude")
        return getWeatherForecastUseCase.execute(latitude, longitude)
    }
}

