package com.example.myweatherapp.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myweatherapp.presentation.viewmodel.WeatherViewModel

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    val weatherState by viewModel.weatherState.collectAsState()
    var cityName by remember { mutableStateOf("Saint Petersburg") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Weather Forecast", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = cityName,
            onValueChange = { cityName = it },
            label = { Text("Enter city") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                isLoading = true
                errorMessage = null
                viewModel.fetchWeatherForCity(cityName)
            }
        ) {
            Text("Get Weather")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        }

        weatherState?.let {
            // Показать температуру, если данные получены
            Text(text = "Temperature: ${it.hourly.temperature_2m[0]} °C")
            isLoading = false
        } ?: run {
            // Если данных нет
            if (errorMessage != null) {
                Text(text = "Error: $errorMessage")
            } else {
                Text(text = "No data")
            }
        }
    }
}
