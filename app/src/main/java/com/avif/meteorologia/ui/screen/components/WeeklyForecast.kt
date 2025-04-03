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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.avif.meteorologia.ui.screen.util.ForecastData
import com.avif.meteorologia.ui.screen.util.ForecastItem
import com.avif.meteorologia.ui.screen.util.fromHex
import com.avif.meteorologia.ui.theme.CardGradient1
import com.avif.meteorologia.ui.theme.CardGradient2
import com.avif.meteorologia.ui.theme.CardGradient3
import com.avif.meteorologia.ui.theme.ColorText
import com.avif.meteorologia.ui.theme.ColorTextSecondary

@Composable
fun WeeklyForecast(
    modifier: Modifier = Modifier
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
                // Title now inside the box
                Text(
                    text = "Weekly Forecast",
                    style = MaterialTheme.typography.titleMedium,
                    color = ColorText,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(ForecastData) { item ->
                        ForecastItemView(item = item)
                    }
                }
            }
        }
    }
}

@Composable
private fun ForecastItemView(
    item: ForecastItem
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
                color = ColorText,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = item.date,
                style = MaterialTheme.typography.bodySmall,
                color = ColorTextSecondary
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Weather icon
        Image(
            painter = painterResource(id = item.image),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            contentScale = ContentScale.Fit
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Temperature
        Text(
            text = item.temperature,
            style = MaterialTheme.typography.bodyLarge,
            color = ColorText,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Air quality
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(Color.fromHex(item.airQualityIndicatorColorHex))
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            Text(
                text = item.airQuality,
                style = MaterialTheme.typography.bodySmall,
                color = ColorTextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
} 