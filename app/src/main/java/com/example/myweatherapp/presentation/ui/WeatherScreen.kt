package com.example.myweatherapp.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    val weatherState by viewModel.weatherState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Изначально показываем Питер
    var cityName by remember { mutableStateOf("Saint Petersburg") }
    var isCitySelectionMode by remember { mutableStateOf(false) }
    val availableCities = listOf("Saint Petersburg", "Moscow", "New York", "London", "Berlin")

    // Состояние для текстового поля
    var textFieldValue by remember { mutableStateOf(cityName) }

    // Функция для форматирования города: первая буква заглавная, остальные маленькие
    fun formatCityName(name: String): String {
        return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Weather Forecast",
            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Ряд с кнопкой "Сменить город"
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Показываем только название города, если не в режиме редактирования
            if (!isCitySelectionMode) {
                Text(
                    text = formatCityName(cityName),
                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary),
                    modifier = Modifier.weight(1f)
                )
            }

            Button(
                onClick = {
                    isCitySelectionMode = !isCitySelectionMode
                    if (!isCitySelectionMode) {
                        cityName = textFieldValue.trim() // Сохраняем введенное название города
                        viewModel.fetchWeatherForCity(cityName)  // Обновляем погоду для нового города
                    }
                },
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (isCitySelectionMode) "Done" else "Change City", color = MaterialTheme.colorScheme.onPrimary)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Если в режиме смены города, показываем поле для ввода и список доступных городов
        if (isCitySelectionMode) {
            // Используем OutlinedTextField для более современного вида
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp) // Увеличиваем высоту поля
                    .padding(8.dp),
                placeholder = { Text("Enter city") },
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Список городов
            Column(modifier = Modifier.fillMaxWidth()) {
                availableCities.forEach { city ->
                    TextButton(
                        onClick = {
                            cityName = city
                            textFieldValue = city // Обновляем текстовое поле с выбранным городом
                            isCitySelectionMode = false
                            viewModel.fetchWeatherForCity(cityName) // Обновляем погоду сразу после выбора города
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

        // Показываем данные погоды или индикатор загрузки
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            weatherState?.let {
                // Карточка для отображения погоды
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
//                    elevation = CardElevation(4.dp),  // Используем CardElevation для задания высоты тени
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Temperature: ${it.hourly.temperature_2m[0]} °C",
//                            style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colorScheme.secondary)
                        )
                    }
                }

            } ?: run {
                // Если данных нет
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





//@Composable
//fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
//    val weatherState by viewModel.weatherState.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val errorMessage by viewModel.errorMessage.collectAsState()
//
//    // Изначально показываем Питер
//    var cityName by remember { mutableStateOf("Saint Petersburg") }
//    var isCitySelectionMode by remember { mutableStateOf(false) }
//    val availableCities = listOf("Saint Petersburg", "Moscow", "New York", "London", "Berlin")
//
//    // Состояние для текстового поля
//    var textFieldValue by remember { mutableStateOf(cityName) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Weather Forecast", style = MaterialTheme.typography.headlineMedium)
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Показать город
//        if (!isCitySelectionMode) {
//            Text(text = "Current city: $cityName")
//        } else {
//            // Если в режиме смены города, показываем поле для ввода или список городов
//            BasicTextField(
//                value = textFieldValue,
//                onValueChange = { textFieldValue = it },
//                modifier = Modifier.padding(8.dp),
//                decorationBox = { innerTextField ->
//                    Box(Modifier.padding(8.dp)) {
//                        innerTextField()
//                    }
//                }
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            // Список городов
//            Column(modifier = Modifier.fillMaxWidth()) {
//                availableCities.forEach { city ->
//                    TextButton(
//                        onClick = {
//                            cityName = city
//                            textFieldValue = city // Обновляем текстовое поле с выбранным городом
//                            isCitySelectionMode = false
//                            viewModel.fetchWeatherForCity(cityName) // Обновляем погоду сразу после выбора города
//                        }
//                    ) {
//                        Text(city)
//                    }
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Button(
//            onClick = {
//                // Переключаем режим смены города
//                isCitySelectionMode = !isCitySelectionMode
//                if (!isCitySelectionMode) {
//                    cityName = textFieldValue.trim() // Убираем лишние пробелы в случае ручного ввода
//                    viewModel.fetchWeatherForCity(cityName)  // Обновляем погоду, если выходим из режима смены города
//                }
//            }
//        ) {
//            Text(if (isCitySelectionMode) "Done" else "Change City")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Показываем данные погоды или индикатор загрузки
//        if (isLoading) {
//            CircularProgressIndicator()
//        } else {
//            weatherState?.let {
//                // Показать температуру, если данные получены
//                Text(text = "Temperature: ${it.hourly.temperature_2m[0]} °C")
//            } ?: run {
//                // Если данных нет
//                if (errorMessage != null) {
//                    Text(text = "Error: $errorMessage")
//                } else {
//                    Text(text = "No data")
//                }
//            }
//        }
//    }
//}
