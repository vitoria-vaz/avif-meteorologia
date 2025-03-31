package com.avif.meteorologia.ui.screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.avif.meteorologia.ui.theme.CardGradient1
import com.avif.meteorologia.ui.theme.CardGradient2
import com.avif.meteorologia.ui.theme.CardGradient3
import com.avif.meteorologia.ui.theme.ColorText
import kotlinx.coroutines.delay

data class CitySearchResult(
    val id: Long,
    val name: String,
    val country: String,
    val lat: Float,
    val lon: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchLocationDialog(
    onDismiss: () -> Unit,
    onCitySelected: (CitySearchResult) -> Unit,
    onSearch: suspend (String) -> List<CitySearchResult>
) {
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<CitySearchResult>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var active by remember { mutableStateOf(true) }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFF0F2FF),  // Very light blue
                                Color(0xFFE6EAFF),  // Light blue
                                Color(0xFFDCE4FF)   // Pale blue, much lighter than 172463
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Search for a city",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF333333),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = {
                            isSearching = true
                            active = false
                        },
                        active = active,
                        onActiveChange = { active = it },
                        placeholder = { Text("Enter city name", color = Color(0xFF555555)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF555555)) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color(0xFF555555))
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        tonalElevation = 4.dp
                    ) {
                        // Empty content for the expanded search bar
                    }
                    
                    // Search effect
                    LaunchedEffect(searchQuery) {
                        if (searchQuery.length >= 3) {
                            isSearching = true
                            delay(500) // Debounce
                            searchResults = onSearch(searchQuery)
                            isSearching = false
                        } else {
                            searchResults = emptyList()
                        }
                    }
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(color = Color(0xFF3E9FFD))
                        } else if (searchResults.isEmpty() && searchQuery.length >= 3) {
                            Text(
                                text = "No cities found",
                                color = Color(0xFF555555),
                                modifier = Modifier.padding(16.dp)
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(searchResults) { city ->
                                    CityItem(
                                        city = city,
                                        onClick = {
                                            onCitySelected(city)
                                            onDismiss()
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CityItem(
    city: CitySearchResult,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White.copy(alpha = 0.7f))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Color(0xFF3E9FFD),
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF333333)
                )
                
                Text(
                    text = city.country,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF666666)
                )
            }
        }
    }
} 