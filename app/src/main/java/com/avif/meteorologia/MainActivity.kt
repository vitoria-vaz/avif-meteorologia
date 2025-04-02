package com.avif.meteorologia

import android.Manifest
import android.os.Bundle
import android.util.Log
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
    companion object {
        private const val TAG = "MainActivity"
        
        // Default coordinates for Rome, Italy - as a fallback if GPS fails
        private const val DEFAULT_LAT = 41.9028f
        private const val DEFAULT_LNG = 12.4964f
    }
    
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
            Log.d(TAG, "Location permission granted")
            // Location permission granted, fetch weather data with location
            fetchWeatherWithLocation()
        } else {
            Log.d(TAG, "Location permission denied, using default location")
            // Use fallback location if GPS permission denied
            Toast.makeText(
                this,
                "Location permission denied. Using default location.",
                Toast.LENGTH_SHORT
            ).show()
            // Use Rome as fallback
            useFallbackLocation()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        Log.d(TAG, "onCreate - Initializing location service")
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
                    
                    // If we already have saved coordinates, use them immediately
                    if (currentLat != 0f && currentLng != 0f) {
                        LaunchedEffect(key1 = true) {
                            Log.d(TAG, "Using saved coordinates: lat=$currentLat, lng=$currentLng")
                            viewModel.fetchWeatherFromGPS(currentLat, currentLng)
                        }
                    }
                }
            }
        }
    }
    
    private fun checkLocationPermission() {
        Log.d(TAG, "Checking location permission")
        if (locationService.hasLocationPermission()) {
            Log.d(TAG, "Location permission already granted")
            fetchWeatherWithLocation()
        } else {
            Log.d(TAG, "Requesting location permission")
            locationService.requestLocationPermission()
        }
    }
    
    private fun fetchWeatherWithLocation() {
        Log.d(TAG, "Attempting to fetch weather with location")
        locationService.getCurrentLocation(
            onSuccess = { location ->
                val lat = location.latitude.toFloat()
                val lng = location.longitude.toFloat()
                Log.d(TAG, "Location retrieved: lat=$lat, lng=$lng")
                
                // Save current location for rotation changes
                currentLat = lat
                currentLng = lng
                
                // Use the new method specifically for GPS coordinates
                viewModel.fetchWeatherFromGPS(lat, lng)
            },
            onError = { error ->
                Log.e(TAG, "Error getting location: ${error.message}", error)
                // If location retrieval fails, fall back to default
                Toast.makeText(
                    this,
                    "Could not get location. Using default location.",
                    Toast.LENGTH_SHORT
                ).show()
                useFallbackLocation()
            }
        )
    }
    
    private fun useFallbackLocation() {
        // Use Rome, Italy as fallback location
        Log.d(TAG, "Using fallback location: Rome, Italy")
        currentLat = DEFAULT_LAT
        currentLng = DEFAULT_LNG
        viewModel.fetchWeatherFromGPS(DEFAULT_LAT, DEFAULT_LNG)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy - Cleaning up location service")
        locationService.cleanup()
    }
}