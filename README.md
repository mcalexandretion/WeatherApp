
# Мобильное приложение "Погода" на Kotlin с Jetpack Compose

app  
├── manifests  
│   └── AndroidManifest.xml  
├── kotlin+java  
│   └── com.example.myweatherapp  
│       ├── data  
│       │   ├── api  
│       │   │   ├── CityApiService.kt  
│       │   │   └── WeatherApiService.kt  
│       │   ├── models  
│       │   │   ├── CityCoordinatesResponse.kt  
│       │   │   └── WeatherForecastResponse.kt  
│       │   └── repositories  
│       │       └── WeatherRepository.kt  
│       ├── domain  
│       │   └── usecases  
│       │       ├── GetCityCoordinatesUseCase.kt  
│       │       └── GetWeatherForecastUseCase.kt  
│       ├── presentation  
│       │   ├── ui  
│       │   │   ├── FormatDate.kt  
│       │   │   ├── WeatherDayBlock.kt  
│       │   │   └── WeatherScreen.kt  
│       │   └── viewmodel  
│       │       ├── WeatherViewModel.kt  
│       │       └── WeatherViewModelFactory.kt  
│       └── MainActivity.kt  

## 1. **Data Layer**
### **API**  
- **WeatherApiService.kt** - Сервис для работы с API прогноза погоды, получающий данные о погоде по заданным координатам.  
- **CityApiService.kt** - Сервис для работы с API города, получающий координаты города по его названию.  
### **Models**  
- **CityCoordinatesResponse.kt** — модель для хранения координат города.  
- **WeatherForecastResponse.kt** — модель для хранения данных прогноза погоды (максимальная и минимальная температура, осадки).  
###**Repositories**  
- **WeatherRepository.kt**— репозиторий, который взаимодействует с сервисами API для получения данных о погоде и координатах города.  

## 2. **Domain Layer**
### **Use Cases**  
- **GetCityCoordinatesUseCase.kt** — кейс для получения координат города.  
- **GetWeatherForecastUseCase.kt** — кейс для получения прогноза погоды по координатам города.  

## 3. **Presentation Layer**
### **UI**  
- **WeatherScreen.kt** — основной экран для отображения прогноза погоды.  
- **WeatherDayBlock.kt** — компонент для отображения прогноза погоды для одного дня.  
- **FormatDate.kt**— утилита для преобразования даты в нужный формат.  
### **ViewModel**  
- **WeatherViewModel.kt** — ViewModel, который управляет состоянием экрана, получает данные о погоде через Use Cases и передаёт их в UI.  
- **WeatherViewModelFactory.kt** — фабрика для создания экземпляров WeatherViewModel, с передачей зависимостей.  

## 4. **MainActivity**  
Главная активность приложения, которая инициализирует все необходимые компоненты и отображает основной экран (WeatherScreen).  

