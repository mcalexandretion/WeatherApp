package com.example.myweatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import com.example.myweatherapp.data.api.CityApiService
import com.example.myweatherapp.data.api.WeatherApiService
import com.example.myweatherapp.data.repositories.WeatherRepository
import com.example.myweatherapp.domain.usecases.GetCityCoordinatesUseCase
import com.example.myweatherapp.domain.usecases.GetWeatherForecastUseCase
import com.example.myweatherapp.presentation.ui.WeatherScreen
import com.example.myweatherapp.presentation.viewmodel.WeatherViewModel
import com.example.myweatherapp.presentation.viewmodel.WeatherViewModelFactory
import com.example.myweatherapp.ui.theme.MyWeatherAppTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity() {

    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Создаём Retrofit для City API и Weather API
        val cityApi = Retrofit.Builder()
            .baseUrl("https://api.api-ninjas.com/") // Убедись, что это правильный базовый URL
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CityApiService::class.java)


        val weatherApi = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/") // Базовый URL для Weather API
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)

        // Создаём репозитории и UseCases
        val repository = WeatherRepository(cityApi, weatherApi)
        val getCityCoordinatesUseCase = GetCityCoordinatesUseCase(repository)
        val getWeatherForecastUseCase = GetWeatherForecastUseCase(repository)

        // Создаём ViewModel через Factory
        val factory = WeatherViewModelFactory(getCityCoordinatesUseCase, getWeatherForecastUseCase)
        weatherViewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        // Устанавливаем контент для активити
        setContent {
            MyWeatherAppTheme {
                WeatherScreen(viewModel = weatherViewModel)
            }
        }
    }
}
