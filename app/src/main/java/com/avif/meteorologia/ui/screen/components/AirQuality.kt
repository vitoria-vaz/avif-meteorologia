package com.avif.meteorologia.ui.screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avif.meteorologia.R
import com.avif.meteorologia.data.model.WeatherInfo
import com.avif.meteorologia.ui.screen.util.fromHex

@Composable
fun AirQuality(
    modifier: Modifier = Modifier,
    weatherInfo: WeatherInfo? = null
) {
    // Set solid white color with 75% opacity
    val backgroundColor = Color.White.copy(alpha = 0.75f)
    val textColor = Color.Black
    
    // Determine air quality rating based on available data
    // Since we don't have direct air quality data from OpenWeatherMap's free API,
    // we'll estimate based on visibility, cloudiness, and humidity
    val airQualityRating = when {
        weatherInfo == null -> "Unknown"
        weatherInfo.visibility >= 9000 && weatherInfo.clouds < 30 -> "Good"
        weatherInfo.visibility >= 6000 && weatherInfo.clouds < 60 -> "Moderate"
        weatherInfo.clouds > 80 || weatherInfo.visibility < 3000 -> "Poor"
        else -> "Fair"
    }
    
    // Set color based on rating
    val airQualityColor = when(airQualityRating) {
        "Good" -> Color.fromHex("#2dbe8d")
        "Moderate" -> Color.fromHex("#f9cf5f")
        "Fair" -> Color.fromHex("#ef974b")
        "Poor" -> Color.fromHex("#ff7676")
        else -> Color.Gray
    }
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent // Set to transparent to allow custom background
    ) {
        // Apply solid white background with a Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Title
                    Text(
                        text = "Air Quality",
                        style = MaterialTheme.typography.titleMedium,
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Value
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(airQualityColor)
                        )
                        Text(
                            text = airQualityRating,
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.7f)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AirQualityItem(
                        title = "Humidity",
                        value = if (weatherInfo != null) "${weatherInfo.humidity}%" else "0%",
                        iconRes = R.drawable.ic_rainy
                    )
                    AirQualityItem(
                        title = "Visibility",
                        value = if (weatherInfo != null) "${weatherInfo.visibility / 1000} km" else "0 km",
                        iconRes = R.drawable.ic_foggy
                    )
                    AirQualityItem(
                        title = "Pressure",
                        value = if (weatherInfo != null) "${weatherInfo.pressure} hPa" else "0 hPa",
                        iconRes = R.drawable.ic_cloudy
                    )
                    AirQualityItem(
                        title = "Feels Like",
                        value = if (weatherInfo != null) "${weatherInfo.feelsLike.toInt()}°" else "0°",
                        iconRes = R.drawable.ic_clear_day
                    )
                }
            }
        }
    }
}

@Composable
private fun AirQualityItem(
    title: String,
    value: String,
    iconRes: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .customShadow(
                    color = Color.Black,
                    alpha = 0.1f,
                    shadowRadius = 16.dp,
                    borderRadius = 16.dp,
                    offsetY = 4.dp
                )
                .background(
                    color = Color.White.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Black.copy(alpha = 0.7f)
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black,
            fontWeight = FontWeight.Bold
        )
    }
} 