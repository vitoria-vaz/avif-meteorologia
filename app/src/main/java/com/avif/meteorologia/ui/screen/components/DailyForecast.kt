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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.avif.meteorologia.R
import com.avif.meteorologia.data.model.WeatherInfo
import com.avif.meteorologia.ui.theme.ColorBlue
import com.avif.meteorologia.ui.theme.CardGradient1
import com.avif.meteorologia.ui.theme.CardGradient2
import com.avif.meteorologia.ui.theme.CardGradient3
import com.avif.meteorologia.ui.theme.ColorIconTint
import com.avif.meteorologia.ui.theme.ColorLightBlue
import com.avif.meteorologia.ui.theme.ColorText
import com.avif.meteorologia.ui.theme.ColorTextSecondary

@Composable
fun DailyForecast(
    modifier: Modifier = Modifier,
    weatherInfo: WeatherInfo? = null
) {
    // Create a gradient brush for the card background
    val cardGradientBrush = Brush.verticalGradient(
        colors = listOf(CardGradient1, CardGradient2, CardGradient3)
    )
    
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.Transparent // Set to transparent to allow gradient background
    ) {
        // Apply gradient background with a Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(cardGradientBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                WeatherStatus(weatherInfo = weatherInfo)
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    WeatherDetail(
                        iconRes = R.drawable.ic_thunderstorm,
                        title = "UV Index",
                        value = "2"
                    )
                    WeatherDetail(
                        iconRes = R.drawable.ic_rainy,
                        title = "Rain Chance",
                        value = "2%"
                    )
                    WeatherDetail(
                        iconRes = R.drawable.ic_cloudy,
                        title = "Wind",
                        value = "18 km/h"
                    )
                }
            }
        }
    }
}

@Composable
private fun WeatherStatus(
    weatherInfo: WeatherInfo?
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
                color = ColorTextSecondary
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = "${weatherInfo?.temperature ?: 18}",
                    style = MaterialTheme.typography.displayLarge,
                    color = ColorText,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "°C",
                    style = MaterialTheme.typography.headlineSmall,
                    color = ColorTextSecondary,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = weatherInfo?.condition ?: "Cloudy",
                style = MaterialTheme.typography.bodyMedium,
                color = ColorTextSecondary
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
    @androidx.annotation.DrawableRes iconRes: Int,
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    color = ColorBlue.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = ColorIconTint,
                modifier = Modifier.size(20.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = ColorTextSecondary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = ColorText,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
} 