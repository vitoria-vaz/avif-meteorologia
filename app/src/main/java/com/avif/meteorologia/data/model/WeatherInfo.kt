package com.avif.meteorologia.data.model

data class WeatherInfo(
    val locationName: String,
    val conditionIcon: String,
    val condition: String,
    val temperature: Int,
    val dayOfWeek: String,
    val isDay: Boolean,
) {
    override fun toString(): String {
        return "WeatherInfo(location='$locationName', condition='$condition', temperature=$temperature°C)"
    }
}