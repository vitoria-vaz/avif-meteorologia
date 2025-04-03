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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avif.meteorologia.R
import com.avif.meteorologia.data.model.WeatherInfo

@Composable
fun DailyForecast(
    modifier: Modifier = Modifier,
    weatherInfo: WeatherInfo? = null
) {
    // Set solid white color with 75% opacity
    val backgroundColor = Color.White.copy(alpha = 0.75f)
    val textColor = Color.Black
    
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
                WeatherStatus(weatherInfo = weatherInfo, textColor = textColor)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherDetail(
                        icon = Icons.Filled.Thermostat,
                        title = "UV Index",
                        value = if (weatherInfo != null) "${weatherInfo.uvIndex}" else "0",
                        textColor = textColor
                    )
                    WeatherDetail(
                        icon = Icons.Filled.WaterDrop,
                        title = "Rain Chance",
                        value = if (weatherInfo != null) "${weatherInfo.rainProbability}%" else "0%",
                        textColor = textColor
                    )
                    WeatherDetail(
                        icon = Icons.Filled.Air,
                        title = "Wind",
                        value = if (weatherInfo != null) {
                            // Convert m/s to km/h (1 m/s = 3.6 km/h)
                            val windKmh = (weatherInfo.windSpeed * 3.6)
                            "%.1f km/h".format(windKmh)
                        } else "0 km/h",
                        textColor = textColor
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherStatus(
    weatherInfo: WeatherInfo?,
    textColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            Text(
                text = weatherInfo?.dayOfWeek ?: "Today",
                style = MaterialTheme.typography.bodyLarge,
                color = textColor.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "${weatherInfo?.temperature ?: 18}",
                    style = MaterialTheme.typography.displayLarge,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "°C",
                    style = MaterialTheme.typography.headlineSmall,
                    color = textColor.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = weatherInfo?.condition ?: "Cloudy",
                style = MaterialTheme.typography.bodyMedium,
                color = textColor.copy(alpha = 0.7f)
            )
        }
        
        // Weather Icon
        Image(
            painter = painterResource(
                id = if (weatherInfo?.isDay == true) {
                    when (weatherInfo.conditionIcon) {
                        "01d" -> R.drawable.ic_clear_day
                        "01n" -> R.drawable.ic_clear_night
                        "02d", "03d", "04d" -> R.drawable.ic_cloudy
                        "02n", "03n", "04n" -> R.drawable.ic_cloudy_night
                        "09d", "09n", "10d", "10n" -> R.drawable.ic_rainy
                        "11d", "11n" -> R.drawable.ic_thunderstorm
                        "13d", "13n" -> R.drawable.ic_snowy
                        "50d", "50n" -> R.drawable.ic_foggy
                        else -> R.drawable.ic_cloudy
                    }
                } else {
                    when (weatherInfo?.conditionIcon) {
                        "01d" -> R.drawable.ic_clear_day
                        "01n" -> R.drawable.ic_clear_night
                        "02d", "03d", "04d" -> R.drawable.ic_cloudy
                        "02n", "03n", "04n" -> R.drawable.ic_cloudy_night
                        "09d", "09n", "10d", "10n" -> R.drawable.ic_rainy
                        "11d", "11n" -> R.drawable.ic_thunderstorm
                        "13d", "13n" -> R.drawable.ic_snowy
                        "50d", "50n" -> R.drawable.ic_foggy
                        else -> R.drawable.ic_cloudy_night
                    }
                }
            ),
            contentDescription = null,
            modifier = Modifier.size(100.dp)
        )
    }
}

@Composable
private fun WeatherDetail(
    icon: ImageVector,
    title: String,
    value: String,
    textColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = Color.White.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor.copy(alpha = 0.8f),
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = textColor.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = textColor,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
} 