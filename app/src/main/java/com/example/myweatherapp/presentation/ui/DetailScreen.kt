package com.example.myweatherapp.presentation.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myweatherapp.presentation.viewmodel.WeatherViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailScreen(
    cityName: String,
    dayIndex: Int,
    viewModel: WeatherViewModel,
    onBack: () -> Unit
) {
    val state = viewModel.weatherState.value ?: return
    val daily = state.daily
    val hourly = state.hourly
    val date = daily.time.getOrNull(dayIndex) ?: ""

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Back button
        Button(onClick = onBack) {
            Text("Back")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = cityName, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(4.dp))

        // Current temperature (closest to now)
        val now = LocalDateTime.now()
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00")
        val nowKey = now.format(fmt)
        val currentIndex = hourly.time.indexOf(nowKey).let { if (it >= 0) it else 0 }
        val currentTemp = hourly.temperature_2m.getOrNull(currentIndex) ?: 0.0
        Text(text = "Current Temperature: ${currentTemp}°C", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        Text(text = "Hourly details for ${date}", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(4.dp))

        val hourlyForDay = hourly.time.indices.filter { idx ->
            hourly.time[idx].startsWith(date)
        }.map { idx -> hourly.time[idx] to hourly.temperature_2m[idx] }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(hourlyForDay) { (time, temp) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = time.substringAfter('T') + "h")
                    Text(text = "${temp}°C")
                }
            }
        }
    }
}