package com.avif.meteorologia.data.remote

import com.avif.meteorologia.data.remote.response.ForecastResponse
import com.avif.meteorologia.data.remote.response.WeatherDataResponse

interface RemoteDataSource {

    suspend fun getWeatherDataResponse(lat: Float, lng: Float): WeatherDataResponse
    
    suspend fun getForecastResponse(lat: Float, lng: Float): ForecastResponse
}