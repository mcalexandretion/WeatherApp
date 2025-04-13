
# Мобильное приложение "Погода" на Kotlin с Jetpack Compose

app/src/main  
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


# Контрольная точка №1

### Запуск приложения
При первом запуске приложения отображается погода для города по умолчанию(Санкт-Петербург)(Рис. 1).  
<p align="center">
  <img src="https://github.com/user-attachments/assets/55970848-3330-4926-a415-f7d489549e71" alt="Рис. 1" />
</p>

<p align="center">Рис. 1</p>

### Смена города
Чтобы сменить город необходимо нажать соответствующую кнопку. Далее можно выбрать город из списка или написать название с клавиатуры(Рис.2 и Рис.3)  
<p align="center">
  <img src="https://github.com/user-attachments/assets/1638f1a0-30d9-4a5a-8ae7-16846e8d58f4" alt="Рис. 1" />
</p>

<p align="center">Рис. 2</p>
<p align="center">
  <img src="https://github.com/user-attachments/assets/3f216142-560e-4450-a374-1068fcb440f7" />
</p>
<p align="center">Рис. 3</p>


### Итог

<p align="center">
  <img src="https://github.com/user-attachments/assets/08b22f4b-1c86-4352-afcd-0c23687058c2" alt="" />
</p>
