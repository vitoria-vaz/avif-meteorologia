package com.avif.meteorologia.data.repository

import com.avif.meteorologia.data.model.WeatherInfo

interface WeatherRepository {

    suspend fun getWeatherData(lat: Float, lng: Float): WeatherInfo
}