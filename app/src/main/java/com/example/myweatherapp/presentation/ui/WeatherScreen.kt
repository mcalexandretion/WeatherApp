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

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    val weatherState by viewModel.weatherState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var cityName by remember { mutableStateOf("Saint Petersburg") }
    var isCitySelectionMode by remember { mutableStateOf(false) }
    val availableCities = listOf("Saint Petersburg", "Moscow", "New York", "London", "Berlin")

    // Состояние для текстового поля
    var textFieldValue by remember { mutableStateOf(cityName) }
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

        if (isCitySelectionMode) {
            OutlinedTextField(
                value = textFieldValue,
                onValueChange = { textFieldValue = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .padding(8.dp),
                placeholder = { Text("Enter city") },
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

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

        if (isLoading) {
            CircularProgressIndicator(
                modifier=Modifier.padding(16.dp),
                color=MaterialTheme.colorScheme.primary
            )
        } else {
            weatherState?.let { state ->
                LazyColumn(modifier=Modifier.fillMaxWidth()) {
                    items(state.daily.temperature_2m_max.indices.toList()) { index ->
                        val tempMax=state.daily.temperature_2m_max[index]
                        val tempMin=state.daily.temperature_2m_min.getOrElse(index){0.0} // Получаем минимальную температуру

                        // Предположим, что у вас есть массив с датами:
                        val date=state.daily.time[index] // Здесь предполагается, что у вас есть массив строковых дат

                        WeatherDayBlock(date=formatDate(date), dayIndex=index+1, minTemp=tempMin, maxTemp=tempMax)
                    }
                }
            } ?: run {
                if (errorMessage != null) {
                    Text(
                        text="Error: $errorMessage",
                        style=MaterialTheme.typography.bodyLarge.copy(color=MaterialTheme.colorScheme.error)
                    )
                } else {
                    Text(
                        text="No data",
                        style=MaterialTheme.typography.bodyLarge.copy(color=MaterialTheme.colorScheme.onBackground)
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherDayBlock(date: String, dayIndex: Int, minTemp: Double, maxTemp: Double) {
    Card(
        modifier=Modifier.padding(vertical=4.dp),
    ) {
        Column(modifier=Modifier.padding(16.dp)) {
            Text(text=date)

            Row(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement=Arrangement.SpaceBetween,
            ) {
                Text(text="Min Temp: ${minTemp}°C")

                Text(text="Max Temp: ${maxTemp}°C")
            }
        }
    }
}

// Функция для форматирования даты из формата ISO в дд.мм.гггг
fun formatDate(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    return try {
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateString // Возвращаем исходную строку в случае ошибки парсинга
    }
}

//@Composable
//fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
//    val weatherState by viewModel.weatherState.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val errorMessage by viewModel.errorMessage.collectAsState()
//
//    var cityName by remember { mutableStateOf("Saint Petersburg") }
//    var isCitySelectionMode by remember { mutableStateOf(false) }
//    val availableCities = listOf("Saint Petersburg", "Moscow", "New York", "London", "Berlin")
//
//    // Состояние для текстового поля
//    var textFieldValue by remember { mutableStateOf(cityName) }
//
//    // Функция для форматирования города: первая буква заглавная, остальные маленькие
//    fun formatCityName(name: String): String {
//        return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Weather Forecast",
//            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary),
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Ряд с кнопкой "Сменить город"
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            if (!isCitySelectionMode) {
//                Text(
//                    text = formatCityName(cityName),
//                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary),
//                    modifier = Modifier.weight(1f)
//                )
//            }
//
//            Button(
//                onClick = {
//                    isCitySelectionMode = !isCitySelectionMode
//                    if (!isCitySelectionMode) {
//                        cityName = textFieldValue.trim() // Сохраняем введенное название города
//                        viewModel.fetchWeatherForCity(cityName)  // Обновляем погоду для нового города
//                    }
//                },
//                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
//                shape = MaterialTheme.shapes.medium
//            ) {
//                Text(if (isCitySelectionMode) "Done" else "Change City", color = MaterialTheme.colorScheme.onPrimary)
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Если в режиме смены города, показываем поле для ввода и список доступных городов
//        if (isCitySelectionMode) {
//            OutlinedTextField(
//                value = textFieldValue,
//                onValueChange = { textFieldValue = it },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(60.dp)
//                    .padding(8.dp),
//                placeholder = { Text("Enter city") },
//                shape = MaterialTheme.shapes.medium,
//                singleLine = true
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
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
//                        Text(
//                            text = city,
//                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
//                        )
//                    }
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (isLoading) {
//            CircularProgressIndicator(
//                modifier = Modifier.padding(16.dp),
//                color = MaterialTheme.colorScheme.primary
//            )
//        } else {
//            weatherState?.let { state ->
//                LazyColumn(modifier = Modifier.fillMaxWidth()) {
//                    items(state.daily.temperature_2m_max.indices.toList()) { index ->
//                        val tempMax = state.daily.temperature_2m_max[index]
//                        val tempMin =
//                            state.daily.temperature_2m_min.getOrElse(index) { 0.0 } // Получаем минимальную температуру
//
//                        WeatherDayBlock(dayIndex = index + 1, minTemp = tempMin, maxTemp = tempMax)
//                    }
//                }
//            } ?: run {
//                if (errorMessage != null) {
//                    Text(
//                        text = "Error: $errorMessage",
//                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error)
//                    )
//                } else {
//                    Text(
//                        text = "No data",
//                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun WeatherDayBlock(dayIndex: Int, minTemp: Double, maxTemp: Double) {
//    Card(
//        modifier=Modifier.padding(vertical=4.dp),
//
//    ) {
//        Column(modifier=Modifier.padding(16.dp)) {
//            Text(text="Day $dayIndex")
//
//            Row(
//                modifier=Modifier.fillMaxWidth(),
//                horizontalArrangement=Arrangement.SpaceBetween,
//            ) {
//                Text(text="Min Temp: ${minTemp}°C")
//
//                Text(text="Max Temp: ${maxTemp}°C")
//            }
//        }
//    }
//}

//@Composable
//fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
//    val weatherState by viewModel.weatherState.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val errorMessage by viewModel.errorMessage.collectAsState()
//
//    var cityName by remember { mutableStateOf("Saint Petersburg") }
//    var isCitySelectionMode by remember { mutableStateOf(false) }
//    val availableCities = listOf("Saint Petersburg", "Moscow", "New York", "London", "Berlin")
//
//    // Состояние для текстового поля
//    var textFieldValue by remember { mutableStateOf(cityName) }
//
//    // Функция для форматирования города: первая буква заглавная, остальные маленькие
//    fun formatCityName(name: String): String {
//        return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Weather Forecast",
//            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary),
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Ряд с кнопкой "Сменить город"
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            if (!isCitySelectionMode) {
//                Text(
//                    text = formatCityName(cityName),
//                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary),
//                    modifier = Modifier.weight(1f)
//                )
//            }
//
//            Button(
//                onClick = {
//                    isCitySelectionMode = !isCitySelectionMode
//                    if (!isCitySelectionMode) {
//                        cityName = textFieldValue.trim() // Сохраняем введенное название города
//                        viewModel.fetchWeatherForCity(cityName)  // Обновляем погоду для нового города
//                    }
//                },
//                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
//                shape = MaterialTheme.shapes.medium
//            ) {
//                Text(if (isCitySelectionMode) "Done" else "Change City", color = MaterialTheme.colorScheme.onPrimary)
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Если в режиме смены города, показываем поле для ввода и список доступных городов
//        if (isCitySelectionMode) {
//            OutlinedTextField(
//                value = textFieldValue,
//                onValueChange = { textFieldValue = it },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(60.dp)
//                    .padding(8.dp),
//                placeholder = { Text("Enter city") },
//                shape = MaterialTheme.shapes.medium,
//                singleLine = true
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
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
//                        Text(
//                            text = city,
//                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
//                        )
//                    }
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (isLoading) {
//            CircularProgressIndicator(
//                modifier = Modifier.padding(16.dp),
//                color = MaterialTheme.colorScheme.primary
//            )
//        } else {
//            weatherState?.let {
//                Column(modifier = Modifier.fillMaxWidth()) {
//                    it.daily.temperature_2m_max.forEachIndexed { index, tempMax ->
//                        val tempMin = it.daily.temperature_2m_min.getOrElse(index) { 0.0 }
//                        Text(
//                            text = "Day ${index + 1}: Max Temp: ${tempMax}°C, Min Temp: ${tempMin}°C",
//                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary),
//                            modifier = Modifier.padding(8.dp)
//                        )
//                    }
//                }
//            } ?: run {
//                if (errorMessage != null) {
//                    Text(
//                        text = "Error: $errorMessage",
//                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error)
//                    )
//                } else {
//                    Text(
//                        text = "No data",
//                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground)
//                    )
//                }
//            }
//        }
//    }
//}

//import com.example.myweatherapp.presentation.viewmodel.WeatherPeriod

//
//@Composable
//fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
//    val weatherState by viewModel.weatherState.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val errorMessage by viewModel.errorMessage.collectAsState()
//
//    var cityName by remember { mutableStateOf("Saint Petersburg") }
//    var isCitySelectionMode by remember { mutableStateOf(false) }
//    val availableCities = listOf("Saint Petersburg", "Moscow", "New York", "London", "Berlin")
//    var textFieldValue by remember { mutableStateOf(cityName) }
//
//    fun formatCityName(name: String): String {
//        return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Weather Forecast",
//            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary),
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            if (!isCitySelectionMode) {
//                Text(
//                    text = formatCityName(cityName),
//                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary),
//                    modifier = Modifier.weight(1f)
//                )
//            }
//
//            Button(
//                onClick = {
//                    isCitySelectionMode = !isCitySelectionMode
//                    if (!isCitySelectionMode) {
//                        cityName = textFieldValue.trim()
//                        viewModel.fetchWeatherForCity(cityName)
//                    }
//                },
//                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
//                shape = MaterialTheme.shapes.medium
//            ) {
//                Text(if (isCitySelectionMode) "Done" else "Change City", color = MaterialTheme.colorScheme.onPrimary)
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (isCitySelectionMode) {
//            OutlinedTextField(
//                value = textFieldValue,
//                onValueChange = { textFieldValue = it },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(60.dp)
//                    .padding(8.dp),
//                placeholder = { Text("Enter city") },
//                shape = MaterialTheme.shapes.medium,
//                singleLine = true
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Column(modifier = Modifier.fillMaxWidth()) {
//                availableCities.forEach { city ->
//                    TextButton(
//                        onClick = {
//                            cityName = city
//                            textFieldValue = city
//                            isCitySelectionMode = false
//                            viewModel.fetchWeatherForCity(cityName)
//                        }
//                    ) {
//                        Text(
//                            text = city,
//                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
//                        )
//                    }
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (isLoading) {
//            CircularProgressIndicator(
//                modifier = Modifier.padding(16.dp),
//                color = MaterialTheme.colorScheme.primary
//            )
//        } else {
//            weatherState?.let { state ->
//                LazyColumn(
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    item {
//                        displayWeatherPeriod("Morning", state.morning)
//                    }
//                    item {
//                        displayWeatherPeriod("Afternoon", state.afternoon)
//                    }
//                    item {
//                        displayWeatherPeriod("Evening", state.evening)
//                    }
//                    item {
//                        displayWeatherPeriod("Night", state.night)
//                    }
//                }
//            } ?: run {
//                Text(text = errorMessage ?: "No weather data available")
//            }
//        }
//    }
//}
//
//@Composable
//fun displayWeatherPeriod(period: String, weatherPeriod: WeatherPeriod) {
//    // Aggregate data for the period
//    val averageTemperature = weatherPeriod.temperatures.average()
//    val totalPrecipitation = weatherPeriod.precipitations.sum()
//    val averageWindSpeed = weatherPeriod.windSpeeds.average()
//
//    Text(
//        text = "$period: ${"%.1f".format(averageTemperature)}°C, ${"%.1f".format(totalPrecipitation)}mm, ${"%.1f".format(averageWindSpeed)}m/s",
//        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary)
//    )
//}


//@Composable
//fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
//    val weatherState by viewModel.weatherState.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val errorMessage by viewModel.errorMessage.collectAsState()
//
//    var cityName by remember { mutableStateOf("Saint Petersburg") }
//    var isCitySelectionMode by remember { mutableStateOf(false) }
//    val availableCities = listOf("Saint Petersburg", "Moscow", "New York", "London", "Berlin")
//    var textFieldValue by remember { mutableStateOf(cityName) }
//
//    fun formatCityName(name: String): String {
//        return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Weather Forecast",
//            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary),
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            if (!isCitySelectionMode) {
//                Text(
//                    text = formatCityName(cityName),
//                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary),
//                    modifier = Modifier.weight(1f)
//                )
//            }
//
//            Button(
//                onClick = {
//                    isCitySelectionMode = !isCitySelectionMode
//                    if (!isCitySelectionMode) {
//                        cityName = textFieldValue.trim()
//                        viewModel.fetchWeatherForCity(cityName)
//                    }
//                },
//                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
//                shape = MaterialTheme.shapes.medium
//            ) {
//                Text(if (isCitySelectionMode) "Done" else "Change City", color = MaterialTheme.colorScheme.onPrimary)
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (isCitySelectionMode) {
//            OutlinedTextField(
//                value = textFieldValue,
//                onValueChange = { textFieldValue = it },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(60.dp)
//                    .padding(8.dp),
//                placeholder = { Text("Enter city") },
//                shape = MaterialTheme.shapes.medium,
//                singleLine = true
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Column(modifier = Modifier.fillMaxWidth()) {
//                availableCities.forEach { city ->
//                    TextButton(
//                        onClick = {
//                            cityName = city
//                            textFieldValue = city
//                            isCitySelectionMode = false
//                            viewModel.fetchWeatherForCity(cityName)
//                        }
//                    ) {
//                        Text(
//                            text = city,
//                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
//                        )
//                    }
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (isLoading) {
//            CircularProgressIndicator(
//                modifier = Modifier.padding(16.dp),
//                color = MaterialTheme.colorScheme.primary
//            )
//        } else {
//            weatherState?.let { state ->
//                LazyColumn(
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    item {
//                        displayWeatherPeriod("Morning", state.morning)
//                    }
//                    item {
//                        displayWeatherPeriod("Afternoon", state.afternoon)
//                    }
//                    item {
//                        displayWeatherPeriod("Evening", state.evening)
//                    }
//                    item {
//                        displayWeatherPeriod("Night", state.night)
//                    }
//                }
//            } ?: run {
//                Text(text = errorMessage ?: "No weather data available")
//            }
//        }
//    }
//}
//
//@Composable
//fun displayWeatherPeriod(period: String, weatherPeriod: WeatherPeriod) {
//    Text(text = "$period Weather:")
//    weatherPeriod.temperatures.forEachIndexed { index, temperature ->
//        Text(text = "Hour ${index + 1}: Temperature = $temperature°C")
//    }
//
//    weatherPeriod.precipitations.forEachIndexed { index, precipitation ->
//        Text(text = "Hour ${index + 1}: Precipitation = $precipitation mm")
//    }
//
//    weatherPeriod.windSpeeds.forEachIndexed { index, windSpeed ->
//        // Assuming `windSpeeds` is a list or array of wind speed data for each hour
//        Text(text = "Hour ${index + 1}: Wind Speed = $windSpeed m/s")
//    }
//}


//@Composable
//fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
//    val weatherState by viewModel.weatherState.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val errorMessage by viewModel.errorMessage.collectAsState()
//
//    var cityName by remember { mutableStateOf("Saint Petersburg") }
//    var isCitySelectionMode by remember { mutableStateOf(false) }
//    val availableCities = listOf("Saint Petersburg", "Moscow", "New York", "London", "Berlin")
//
//    var textFieldValue by remember { mutableStateOf(cityName) }
//
//    // Функция для форматирования названия города
//    fun formatCityName(name: String): String {
//        return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Weather Forecast",
//            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary),
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            if (!isCitySelectionMode) {
//                Text(
//                    text = formatCityName(cityName),
//                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary),
//                    modifier = Modifier.weight(1f)
//                )
//            }
//
//            Button(
//                onClick = {
//                    isCitySelectionMode = !isCitySelectionMode
//                    if (!isCitySelectionMode) {
//                        cityName = textFieldValue.trim()
//                        viewModel.fetchWeatherForCity(cityName)
//                    }
//                },
//                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
//                shape = MaterialTheme.shapes.medium
//            ) {
//                Text(if (isCitySelectionMode) "Done" else "Change City", color = MaterialTheme.colorScheme.onPrimary)
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (isCitySelectionMode) {
//            OutlinedTextField(
//                value = textFieldValue,
//                onValueChange = { textFieldValue = it },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(60.dp)
//                    .padding(8.dp),
//                placeholder = { Text("Enter city") },
//                shape = MaterialTheme.shapes.medium,
//                singleLine = true
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            Column(modifier = Modifier.fillMaxWidth()) {
//                availableCities.forEach { city ->
//                    TextButton(
//                        onClick = {
//                            cityName = city
//                            textFieldValue = city
//                            isCitySelectionMode = false
//                            viewModel.fetchWeatherForCity(cityName)
//                        }
//                    ) {
//                        Text(
//                            text = city,
//                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
//                        )
//                    }
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        if (isLoading) {
//            CircularProgressIndicator(
//                modifier = Modifier.padding(16.dp),
//                color = MaterialTheme.colorScheme.primary
//            )
//        } else {
//            weatherState?.let { state ->
//                // Отображаем прогноз погоды для разных периодов дня
//                displayWeatherPeriod("Morning", state.morning)
//                displayWeatherPeriod("Afternoon", state.afternoon)
//                displayWeatherPeriod("Evening", state.evening)
//                displayWeatherPeriod("Night", state.night)
//            } ?: run {
//                Text(text = errorMessage ?: "No weather data available")
//            }
//        }
//    }
//}
//
//@Composable
//fun displayWeatherPeriod(period: String, weatherPeriod: WeatherPeriod) {
//    Text(text = "$period Weather:")
//    weatherPeriod.temperatures.forEachIndexed { index, temperature ->
//        Text(text = "Hour ${index + 1}: Temperature = $temperature°C")
//    }
//    weatherPeriod.precipitations.forEachIndexed { index, precipitation ->
//        Text(text = "Hour ${index + 1}: Precipitation = $precipitation mm")
//    }
//    weatherPeriod.windSpeeds.forEachIndexed { index, windSpeed ->
//        Text(text = "Hour ${index + 1}: Wind Speed = $windSpeed m/s")
//    }
//}



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
//    // Функция для форматирования города: первая буква заглавная, остальные маленькие
//    fun formatCityName(name: String): String {
//        return name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Weather Forecast",
//            style = MaterialTheme.typography.headlineLarge.copy(color = MaterialTheme.colorScheme.primary),
//            modifier = Modifier.padding(bottom = 16.dp)
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Ряд с кнопкой "Сменить город"
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            // Показываем только название города, если не в режиме редактирования
//            if (!isCitySelectionMode) {
//                Text(
//                    text = formatCityName(cityName),
//                    style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.secondary),
//                    modifier = Modifier.weight(1f)
//                )
//            }
//
//            Button(
//                onClick = {
//                    isCitySelectionMode = !isCitySelectionMode
//                    if (!isCitySelectionMode) {
//                        cityName = textFieldValue.trim() // Сохраняем введенное название города
//                        viewModel.fetchWeatherForCity(cityName)  // Обновляем погоду для нового города
//                    }
//                },
//                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
//                shape = MaterialTheme.shapes.medium
//            ) {
//                Text(if (isCitySelectionMode) "Done" else "Change City", color = MaterialTheme.colorScheme.onPrimary)
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Если в режиме смены города, показываем поле для ввода и список доступных городов
//        if (isCitySelectionMode) {
//            // Используем OutlinedTextField для более современного вида
//            OutlinedTextField(
//                value = textFieldValue,
//                onValueChange = { textFieldValue = it },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(60.dp) // Увеличиваем высоту поля
//                    .padding(8.dp),
//                placeholder = { Text("Enter city") },
//                shape = MaterialTheme.shapes.medium,
//                singleLine = true
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
//                        Text(
//                            text = city,
//                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
//                        )
//                    }
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Показываем данные погоды или индикатор загрузки
//        if (isLoading) {
//            CircularProgressIndicator(
//                modifier = Modifier.padding(16.dp),
//                color = MaterialTheme.colorScheme.primary
//            )
//        } else {
//            weatherState?.let {
//                // Карточка для отображения погоды
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp),
////                    elevation = CardElevation(4.dp),  // Используем CardElevation для задания высоты тени
//                    shape = MaterialTheme.shapes.medium
//                ) {
//                    Column(modifier = Modifier.padding(16.dp)) {
//                        Text(
//                            text = "Temperature: ${it.hourly.temperature_2m[0]} °C",
////                            style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colorScheme.secondary)
//                        )
//                    }
//                }
//
//            } ?: run {
//                // Если данных нет
//                if (errorMessage != null) {
//                    Text(
//                        text = "Error: $errorMessage",
//                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.error)
//                    )
//                } else {
//                    Text(
//                        text = "No data",
//                        style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onBackground)
//                    )
//                }
//            }
//        }
//    }
//}
//
//



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
