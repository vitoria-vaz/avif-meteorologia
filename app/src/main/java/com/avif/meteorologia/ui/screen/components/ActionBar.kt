package com.avif.meteorologia.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.avif.meteorologia.ui.theme.ColorGradient1
import com.avif.meteorologia.ui.theme.ColorGradient2
import com.avif.meteorologia.ui.theme.ColorText
import com.avif.meteorologia.ui.theme.ColorTextSecondary

@Composable
fun ActionBar(
    modifier: Modifier = Modifier,
    locationName: String = "Unknown",
    onLocationClick: () -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        LocationInfo(
            modifier = Modifier.padding(vertical = 10.dp),
            location = locationName,
            onLocationClick = onLocationClick
        )
    }
}

@Composable
private fun LocationInfo(
    modifier: Modifier = Modifier,
    location: String,
    onLocationClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.clickable { onLocationClick() }
        ) {
            Text(
                text = location,
                style = MaterialTheme.typography.titleLarge,
                color = ColorText,
                fontWeight = FontWeight.Bold
            )
            
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search location",
                tint = ColorText,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        ProgressBar()
    }
}

@Composable
private fun ProgressBar(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(
                    0f to ColorGradient1,
                    0.5f to ColorGradient2,
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(
                vertical = 2.dp,
                horizontal = 10.dp
            )
    ) {
        Text(
            text = "Updating...",
            style = MaterialTheme.typography.labelSmall,
            color = ColorTextSecondary.copy(alpha = 0.7f)
        )
    }
} 