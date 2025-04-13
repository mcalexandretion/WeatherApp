package com.example.myweatherapp.data.models

data class WeatherForecastResponse(
    val daily: DailyForecast,
    val hourly: HourlyForecast
)


data class DailyForecast(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val precipitation_sum: List<Double>,
    val wind_speed_10m_max: List<Double>
)

data class HourlyForecast(
    val time: List<String>,
    val temperature_2m: List<Double>,
    val precipitation: List<Double>,
    val wind_speed_10m: List<Double>,
    val weather_code: List<Int>
)

//
//data class WeatherForecastResponse(
//    val daily: Daily
//)
//
//data class Daily(
//    val time: List<String>,
//    val temperature_2m_max: List<Double>,
//    val temperature_2m_min: List<Double>,
//    val precipitation_sum: List<Double>,
//    val wind_speed_10m_max: List<Double>,
//    val weathercode: List<Int>
//)
//


//data class WeatherForecastResponse(
//    val daily: Daily
//)
//
//data class Daily(
//    val temperature_2m_max: List<Double>,  // Максимальная температура на каждый день
//    val temperature_2m_min: List<Double>   // Минимальная температура на каждый день
//)
