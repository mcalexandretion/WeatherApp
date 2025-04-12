package com.example.myweatherapp.data.models

data class WeatherForecastResponse(
    val hourly: Hourly
)

data class Hourly(
    val temperature_2m: List<Double>
)
