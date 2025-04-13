package com.example.myweatherapp.presentation.ui


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WeatherDayBlock(date: String, dayIndex: Int, minTemp: Double, maxTemp: Double) {
    Card(
        modifier = Modifier.padding(vertical = 4.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = date)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = "Min Temp: ${minTemp}°C")
                Text(text = "Max Temp: ${maxTemp}°C")
            }
        }
    }
}