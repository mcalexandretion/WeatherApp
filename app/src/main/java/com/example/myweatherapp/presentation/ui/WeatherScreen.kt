package com.example.myweatherapp.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myweatherapp.presentation.viewmodel.WeatherViewModel
import com.example.myweatherapp.presentation.ui.formatDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = viewModel(),
    onDayClick: (dayIndex: Int, city: String) -> Unit
) {
    val weatherState by viewModel.weatherState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var cityName by remember { mutableStateOf("Saint Petersburg") }
    var isCitySelectionMode by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(cityName) }
    val availableCities = listOf("Saint Petersburg", "Moscow", "New York", "London", "Berlin")

    LaunchedEffect(cityName) { viewModel.fetchWeatherForCity(cityName) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Weather",
            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Current temperature
        weatherState?.let { state ->
            val hourly = state.hourly
            val now = LocalDateTime.now()
            val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00")
            val nowKey = now.format(fmt)
            val currentIndex = hourly.time.indexOf(nowKey).coerceAtLeast(0)
            val currentTemp = hourly.temperature_2m.getOrNull(currentIndex) ?: 0.0

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    MaterialTheme.colorScheme.secondaryContainer
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Now: ${currentTemp}°C",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // City selection header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (!isCitySelectionMode) {
                Text(
                    text = cityName,
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.weight(1f)
                )
                Button(onClick = { isCitySelectionMode = true }) { Text("Change City") }
            }
        }

        // City selection UI
        if (isCitySelectionMode) {
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter city") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(onClick = {
                    cityName = textFieldValue.trim()
                    isCitySelectionMode = false
                    viewModel.fetchWeatherForCity(cityName)
                }) { Text("Done") }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // List of preset cities
            Column(modifier = Modifier.fillMaxWidth()) {
                availableCities.forEach { city ->
                    TextButton(
                        onClick = {
                            cityName = city
                            textFieldValue = city
                            isCitySelectionMode = false
                            viewModel.fetchWeatherForCity(cityName)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = city,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            weatherState?.let { state ->
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(state.daily.time.indices.toList()) { index ->
                        val date = state.daily.time[index]
                        val tempMax = state.daily.temperature_2m_max[index]
                        val tempMin = state.daily.temperature_2m_min[index]
                        WeatherDayBlock(
                            date = formatDate(date),
                            dayIndex = index + 1,
                            minTemp = tempMin,
                            maxTemp = tempMax,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onDayClick(index, cityName) }
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            } ?: run {
                Text(
                    text = errorMessage?.let { "Error: $it" } ?: "No data",
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error)
                )
            }
        }
    }
}