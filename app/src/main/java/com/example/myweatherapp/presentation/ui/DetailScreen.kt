package com.example.myweatherapp.presentation.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myweatherapp.presentation.viewmodel.WeatherViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import com.example.myweatherapp.presentation.ui.CurrentWeatherCard
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import com.example.myweatherapp.R

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
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
    val rawDate = daily.time.getOrNull(dayIndex) ?: return

    val parts = rawDate.split("-")
    val month = parts[1].toIntOrNull()?.let { java.time.Month.of(it) } ?: java.time.Month.JANUARY
    val day = parts.getOrNull(2)?.toIntOrNull() ?: 1
    val enMonth = month.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val headerDate = "Weather on $day $enMonth"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(cityName) },
                navigationIcon = {
                    Text(
                        text = "< Back",
                        modifier = Modifier
                            .clickable(onClick = onBack)
                            .padding(horizontal = 16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            val now = LocalDateTime.now()
            val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00")
            val key = now.format(fmt)
            val idx = hourly.time.indexOf(key).coerceAtLeast(0)
            val tempNow = hourly.temperature_2m.getOrNull(idx) ?: 0.0

            CurrentWeatherCard(temperature = tempNow)
            Spacer(modifier = Modifier.height(20.dp))


            Text(
                text = headerDate,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))


            val list = hourly.time.indices.filter { it -> hourly.time[it].startsWith(rawDate) }
                .map { it to hourly.temperature_2m[it] }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(list) { (i, t) ->
                    Surface(
                        shape = MaterialTheme.shapes.small,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        tonalElevation = 4.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.icon2),
                                    contentDescription = "Weather Icon",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .padding(end = 12.dp)
                                )
                                Text(
                                    text = hourly.time[i].substringAfter('T'),
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Text(
                                text = "${t}°C",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }

                    }
                }
            }
        }
    }
}
