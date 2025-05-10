package com.example.myweatherapp.di

import com.example.myweatherapp.data.api.CityApiService
import com.example.myweatherapp.data.api.WeatherApiService
import com.example.myweatherapp.data.repositories.WeatherRepository
import com.example.myweatherapp.domain.usecases.GetCityCoordinatesUseCase
import com.example.myweatherapp.domain.usecases.GetWeatherForecastUseCase
import com.example.myweatherapp.presentation.viewmodel.WeatherViewModel
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

object AppModule {
    private const val BASE_URL_CITY = "https://api.api-ninjas.com/"
    private const val BASE_URL_WEATHER = "https://api.open-meteo.com/"

    val weatherApiService: WeatherApiService = Retrofit.Builder()
        .baseUrl(BASE_URL_WEATHER)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create()

    val cityApiService: CityApiService = Retrofit.Builder()
        .baseUrl(BASE_URL_CITY)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create()

    val weatherRepository: WeatherRepository = WeatherRepository(cityApiService, weatherApiService)
    val getCityCoordinatesUseCase = GetCityCoordinatesUseCase(weatherRepository)
    val getWeatherForecastUseCase = GetWeatherForecastUseCase(weatherRepository)

    val weatherViewModel: WeatherViewModel = WeatherViewModel(getCityCoordinatesUseCase, getWeatherForecastUseCase)
}
