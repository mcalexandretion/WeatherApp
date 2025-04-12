package com.example.myweatherapp.data.api

import com.example.myweatherapp.data.models.CityCoordinatesResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface CityApiService {
    @GET("v1/city")
    suspend fun getCityCoordinates(
        @Query("name") cityName: String,
        @Header("X-Api-Key") apiKey: String = "ET9dAMUzFtPCCsO6yu3SbQ==6afrxBiKX5HfLhVV"
    ): List<CityCoordinatesResponse>
}