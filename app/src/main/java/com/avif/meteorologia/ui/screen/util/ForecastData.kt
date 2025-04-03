package com.avif.meteorologia.ui.screen.util

import androidx.annotation.DrawableRes
import com.avif.meteorologia.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import kotlin.random.Random

data class ForecastItem(
    @DrawableRes val image: Int,
    val dayOfWeek: String,
    val date: String,
    val temperature: String,
    val airQuality: String,
    val airQualityIndicatorColorHex: String,
    val isSelected: Boolean = false
)

/**
 * Generate forecast data using real dates and randomized but realistic weather data
 * In a real app, this would fetch from a forecast API
 */
val ForecastData = buildWeeklyForecast()

private fun buildWeeklyForecast(): List<ForecastItem> {
    val today = LocalDate.now()
    val tomorrow = today.plusDays(1) // Start from tomorrow
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM")
    val items = mutableListOf<ForecastItem>()
    
    val currentTemp = 15 + Random.nextInt(10) // Base temperature between 15-25°C
    val weatherTrend = Random.nextInt(-2, 3) // Temperature trend
    
    // Common weather icons
    val weatherIcons = listOf(
        R.drawable.img_cloudy,
        R.drawable.ic_cloudy,
        R.drawable.ic_clear_day,
        R.drawable.ic_rainy
    )
    
    // Create 7 days of forecast starting from tomorrow
    for (i in 0 until 7) {
        val date = tomorrow.plusDays(i.toLong())
        val dayTemp = (currentTemp + (i * weatherTrend) + Random.nextInt(-3, 4)).coerceIn(5, 35)
        
        // Air quality index (0-300 scale)
        val airQualityValue = Random.nextInt(20, 200)
        val airQualityColor = when {
            airQualityValue < 50 -> "#2dbe8d" // Good
            airQualityValue < 100 -> "#f9cf5f" // Moderate
            airQualityValue < 150 -> "#ef974b" // Unhealthy for sensitive groups
            else -> "#ff7676" // Unhealthy
        }
        
        items.add(
            ForecastItem(
                image = weatherIcons[Random.nextInt(weatherIcons.size)],
                dayOfWeek = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                date = date.format(dateFormatter),
                temperature = "${dayTemp}°",
                airQuality = airQualityValue.toString(),
                airQualityIndicatorColorHex = airQualityColor,
                isSelected = i == 0 // Make first day (tomorrow) selected by default
            )
        )
    }
    
    return items
} 