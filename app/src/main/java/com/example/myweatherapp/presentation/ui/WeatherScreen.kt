package com.example.myweatherapp.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myweatherapp.presentation.viewmodel.WeatherViewModel
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myweatherapp.data.models.WeatherForecastResponse
import com.example.myweatherapp.data.models.DailyForecast
import com.example.myweatherapp.data.models.HourlyForecast
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.myweatherapp.presentation.ui.WeatherDayBlock
import com.example.myweatherapp.presentation.ui.formatDate

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    val weatherState by viewModel.weatherState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var cityName by remember { mutableStateOf("Saint Petersburg") }
    var isCitySelectionMode by remember { mutableStateOf(false) }
    val availableCities = listOf("Saint Petersburg", "Moscow", "New York", "London", "Berlin")

    var textFieldValue by remember { mutableStateOf(cityName) }
    fun formatCityName(name: String): String {
        return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchWeatherForCity(cityName)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Weather",
            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (!isCitySelectionMode) {
                Text(
                    text = formatCityName(cityName),
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        isCitySelectionMode = !isCitySelectionMode
                        if (!isCitySelectionMode) {
                            cityName = textFieldValue.trim()
                            viewModel.fetchWeatherForCity(cityName)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Change City", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isCitySelectionMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp),
                    placeholder = { Text("Enter city") },
                    shape = MaterialTheme.shapes.medium,
                    singleLine = true
                )

                Button(
                    onClick = {
                        isCitySelectionMode = !isCitySelectionMode
                        if (!isCitySelectionMode) {
                            cityName = textFieldValue.trim()
                            viewModel.fetchWeatherForCity(cityName)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(if (isCitySelectionMode) "Done" else "Change City", color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                availableCities.forEach { city ->
                    TextButton(
                        onClick = {
                            cityName = city
                            textFieldValue = city
                            isCitySelectionMode = false
                            viewModel.fetchWeatherForCity(cityName)
                        }
                    ) {
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(
                modifier=Modifier.padding(16.dp),
                color=MaterialTheme.colorScheme.primary
            )
        } else {
            weatherState?.let { state ->
                LazyColumn(modifier=Modifier.fillMaxWidth()) {
                    items(state.daily.temperature_2m_max.indices.toList()) { index ->
                        val tempMax = state.daily.temperature_2m_max[index]
                        val tempMin = state.daily.temperature_2m_min.getOrElse(index) { 0.0 }
                        val date = state.daily.time[index]
                        WeatherDayBlock(date = formatDate(date), dayIndex = index + 1, minTemp = tempMin, maxTemp = tempMax)
                    }
                }
            } ?: run {
                if (errorMessage != null) {
                    Text(
                        text = "Error: $errorMessage",
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error)
                    )
                } else {
                    Text(
                        text = "No data",
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground)
                    )
                }
            }
        }
    }
}
