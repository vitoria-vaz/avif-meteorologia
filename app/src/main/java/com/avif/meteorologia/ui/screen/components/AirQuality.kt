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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avif.meteorologia.R
import com.avif.meteorologia.ui.screen.util.fromHex
import com.avif.meteorologia.ui.theme.CardGradient1
import com.avif.meteorologia.ui.theme.CardGradient2
import com.avif.meteorologia.ui.theme.CardGradient3
import com.avif.meteorologia.ui.theme.ColorText
import com.avif.meteorologia.ui.theme.ColorTextSecondary

@Composable
fun AirQuality(
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Title
                    Text(
                        text = "Air Quality",
                        style = MaterialTheme.typography.titleMedium,
                        color = ColorText,
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
                                .background(Color.fromHex("#2dbe8d"))
                        )
                        Text(
                            text = "Good",
                            style = MaterialTheme.typography.bodySmall,
                            color = ColorTextSecondary
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    AirQualityItem(
                        title = "SO₂",
                        value = "25",
                        iconRes = R.drawable.ic_thunderstorm
                    )
                    AirQualityItem(
                        title = "NO₂",
                        value = "15",
                        iconRes = R.drawable.ic_clear_day
                    )
                    AirQualityItem(
                        title = "O₃",
                        value = "40",
                        iconRes = R.drawable.ic_foggy
                    )
                    AirQualityItem(
                        title = "PM10",
                        value = "32",
                        iconRes = R.drawable.ic_cloudy
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
                    color = CardGradient2.copy(alpha = 0.7f),
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
            color = ColorTextSecondary
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = ColorText,
            fontWeight = FontWeight.Bold
        )
    }
} 