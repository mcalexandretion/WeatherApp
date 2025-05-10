package com.example.myweatherapp.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myweatherapp.presentation.viewmodel.WeatherViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.myweatherapp.presentation.ui.CurrentWeatherCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = viewModel(),
    onDayClick: (dayIndex: Int, city: String) -> Unit
) {
    val weatherState by viewModel.weatherState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val cityName by viewModel.selectedCity.collectAsState()
    var isCitySelectionMode by remember { mutableStateOf(false) }
    var textFieldValue by remember { mutableStateOf(cityName) }
    val availableCities = listOf("Saint Petersburg", "Moscow", "New York", "London", "Berlin")

    // Получаем данные о погоде для города при изменении cityName
    LaunchedEffect(cityName) { viewModel.fetchWeatherForCity(cityName) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Weather in $cityName") },
                actions = {
                    Text(
                        text = if (isCitySelectionMode) "Done" else "Change",
                        modifier = Modifier
                            .clickable {
                                if (isCitySelectionMode) {
                                    viewModel.updateSelectedCity(textFieldValue.trim())
                                    viewModel.fetchWeatherForCity(textFieldValue.trim())
                                }
                                isCitySelectionMode = !isCitySelectionMode
                            }
                            .padding(horizontal = 16.dp),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            weatherState?.let { state ->
                val nowKey = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00"))
                val idx = state.hourly.time.indexOf(nowKey).coerceAtLeast(0)
                val tempNow = state.hourly.temperature_2m.getOrNull(idx) ?: 0.0

                CurrentWeatherCard(temperature = tempNow)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Режим выбора города
            if (isCitySelectionMode) {
                OutlinedTextField(
                    value = textFieldValue,
                    onValueChange = { textFieldValue = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter city") }
                )
                Spacer(modifier = Modifier.height(12.dp))
                LazyColumn(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    items(availableCities) { city ->
                        Text(
                            text = city,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateSelectedCity(city)
                                    textFieldValue = city
                                    isCitySelectionMode = false
                                    viewModel.fetchWeatherForCity(city)
                                }
                                .padding(12.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Загрузка данных
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                val state = weatherState
                if (state != null) {
                    val times = state.daily.time
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(times.indices.toList()) { i ->
                            val date = formatDate(times[i])
                            val min = state.daily.temperature_2m_min[i]
                            val max = state.daily.temperature_2m_max[i]
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                                tonalElevation = 4.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onDayClick(i, cityName) }
                            ) {
                                Row(
                                    Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = date, style = MaterialTheme.typography.bodyLarge)
                                    Column(
                                        horizontalAlignment = Alignment.End
                                    ) {
                                        Text(text = "Max: ${max}°C", style = MaterialTheme.typography.bodyMedium)
                                        Text(text = "Min: ${min}°C", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Text(
                        text = errorMessage?.let { "Error: $it" } ?: "No data",
                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error)
                    )
                }
            }
        }
    }
}
