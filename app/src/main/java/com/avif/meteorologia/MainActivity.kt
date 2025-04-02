package com.avif.meteorologia

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.avif.meteorologia.data.location.LocationService
import com.avif.meteorologia.ui.WeatherViewModel
import com.avif.meteorologia.ui.screen.WeatherScreen
import com.avif.meteorologia.ui.theme.MeteorologiaTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    // Use viewModels() delegate instead of direct injection
    private val viewModel: WeatherViewModel by viewModels()
    
    @Inject
    lateinit var locationService: LocationService
    
    // Store current location for configuration changes
    private var currentLat: Float = 0f
    private var currentLng: Float = 0f
    
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
                
        if (locationGranted) {
            // Location permission granted, fetch weather data with location
            fetchWeatherWithLocation()
        } else {
            // Use default location
            Toast.makeText(
                this,
                "Location permission denied. Using default location.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize location service
        locationService.initialize(this, permissionLauncher)
        
        // Check and request permissions if needed
        checkLocationPermission()
        
        setContent {
            MeteorologiaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherScreen(viewModel = viewModel)
                }
            }
        }
    }
    
    private fun checkLocationPermission() {
        if (locationService.hasLocationPermission()) {
            fetchWeatherWithLocation()
        } else {
            locationService.requestLocationPermission()
        }
    }
    
    private fun fetchWeatherWithLocation() {
        locationService.getCurrentLocation(
            onSuccess = { location ->
                val lat = location.latitude.toFloat()
                val lng = location.longitude.toFloat()
                // Save current location for rotation changes
                currentLat = lat
                currentLng = lng
                
                // Use the new method specifically for GPS coordinates
                viewModel.fetchWeatherFromGPS(lat, lng)
            },
            onError = {
                // If location retrieval fails, fall back to default
                Toast.makeText(
                    this,
                    "Could not get location. Using default location.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
    
    override fun onDestroy() {
        super.onDestroy()
        locationService.cleanup()
    }
}