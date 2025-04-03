package com.avif.meteorologia.data.repository

import com.avif.meteorologia.data.model.WeatherInfo
import com.avif.meteorologia.ui.screen.util.ForecastItem

interface WeatherRepository {

    suspend fun getWeatherData(lat: Float, lng: Float): WeatherInfo
    
    suspend fun getForecastData(lat: Float, lng: Float): List<ForecastItem>
}