package com.avif.meteorologia.ui.screen.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.Thunderstorm
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material.icons.rounded.AcUnit
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avif.meteorologia.R
import com.avif.meteorologia.ui.screen.util.ForecastData
import com.avif.meteorologia.ui.screen.util.ForecastItem


@Composable
fun WeeklyForecast(
    modifier: Modifier = Modifier,
    forecastData: List<ForecastItem> = ForecastData // Use API data if provided, otherwise fall back to static data
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
                // Title now inside the box
                Text(
                    text = "Weekly Forecast",
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(forecastData) { item ->
                        ForecastItemView(item = item, textColor = textColor)
                    }
                }
            }
        }
    }
}

@Composable
private fun ForecastItemView(
    item: ForecastItem,
    textColor: Color
) {
    Column(
        modifier = Modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Day and Date
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.dayOfWeek,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = item.date,
                style = MaterialTheme.typography.bodySmall,
                color = textColor.copy(alpha = 0.7f)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Weather icon - now using Material Symbols
        val weatherIcon = getWeatherIcon(item.image)
        Icon(
            imageVector = weatherIcon,
            contentDescription = null,
            tint = textColor.copy(alpha = 0.8f),
            modifier = Modifier.size(32.dp)
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Temperature (min/max)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.maxTemperature,
                style = MaterialTheme.typography.bodyLarge,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = item.minTemperature,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Maps drawable resource IDs to appropriate Material Icons
 */
@Composable
private fun getWeatherIcon(iconRes: Int): ImageVector {
    return when (iconRes) {
        R.drawable.ic_cloudy -> Icons.Rounded.Cloud
        R.drawable.ic_rainy -> Icons.Rounded.WaterDrop
        R.drawable.ic_thunderstorm -> Icons.Rounded.Thunderstorm
        R.drawable.ic_clear_day, R.drawable.ic_clear_night -> Icons.Rounded.WbSunny
        R.drawable.ic_snowy -> Icons.Rounded.AcUnit
        else -> Icons.Rounded.Cloud // Default icon
    }
} 