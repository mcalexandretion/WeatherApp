package com.example.myweatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myweatherapp.data.api.CityApiService
import com.example.myweatherapp.data.api.WeatherApiService
import com.example.myweatherapp.data.repositories.WeatherRepository
import com.example.myweatherapp.domain.usecases.GetCityCoordinatesUseCase
import com.example.myweatherapp.domain.usecases.GetWeatherForecastUseCase
import com.example.myweatherapp.presentation.ui.DetailScreen
import com.example.myweatherapp.presentation.ui.WeatherScreen
import com.example.myweatherapp.presentation.viewmodel.WeatherViewModel
import com.example.myweatherapp.presentation.viewmodel.WeatherViewModelFactory
import com.example.myweatherapp.ui.theme.MyWeatherAppTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URLDecoder
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    private lateinit var weatherViewModel: WeatherViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cityApi = Retrofit.Builder()
            .baseUrl("https://api.api-ninjas.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CityApiService::class.java)


        val weatherApi = Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)

//        val cityApi = Retrofit.Builder()
//            .baseUrl("https://api.api-ninjas.com/v1/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(CityApiService::class.java)
//
//        val weatherApi = Retrofit.Builder()
//            .baseUrl("https://api.open-meteo.com/v1/")
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(WeatherApiService::class.java)

        val repository = WeatherRepository(cityApi, weatherApi)
        val getCityCoordinatesUseCase = GetCityCoordinatesUseCase(repository)
        val getWeatherForecastUseCase = GetWeatherForecastUseCase(repository)

        val factory = WeatherViewModelFactory(getCityCoordinatesUseCase, getWeatherForecastUseCase)
        weatherViewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        setContent {
            MyWeatherAppTheme {
                WeatherNavGraph(viewModel = weatherViewModel)
            }
        }
    }
}

@Composable
fun WeatherNavGraph(viewModel: WeatherViewModel) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "weather_list") {
        composable("weather_list") {
            WeatherScreen(
                viewModel = viewModel,
                onDayClick = { dateIndex, city ->
                    // navigate with arguments
                    val encodedCity = URLEncoder.encode(city, "UTF-8")
                    navController.navigate("weather_detail/$encodedCity/$dateIndex")
                }
            )
        }
        composable(
            route = "weather_detail/{city}/{dayIndex}",
            arguments = listOf(
                navArgument("city") { type = NavType.StringType },
                navArgument("dayIndex") { type = NavType.IntType }
            )
        ) { backStack ->
            val city = backStack.arguments?.getString("city")?.let { URLDecoder.decode(it, "UTF-8") } ?: ""
            val dayIndex = backStack.arguments?.getInt("dayIndex") ?: 0
            DetailScreen(
                cityName = city,
                dayIndex = dayIndex,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}