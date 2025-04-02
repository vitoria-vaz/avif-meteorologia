package com.avif.meteorologia.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidLocationService @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationService {
    companion object {
        private const val TAG = "AndroidLocationService"
    }
    
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null
    private var activity: Activity? = null
    
    // For canceling location requests
    private var cancellationTokenSource = CancellationTokenSource()
    
    /**
     * Initialize with activity for permission requests
     */
    override fun initialize(activity: Activity, permissionLauncher: ActivityResultLauncher<Array<String>>) {
        Log.d(TAG, "Initializing AndroidLocationService")
        this.activity = activity
        this.permissionLauncher = permissionLauncher
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }
    
    override fun hasLocationPermission(): Boolean {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        
        val result = hasFineLocation || hasCoarseLocation
        Log.d(TAG, "Has location permission: $result (fine=$hasFineLocation, coarse=$hasCoarseLocation)")
        return result
    }
    
    override fun requestLocationPermission() {
        Log.d(TAG, "Requesting location permissions")
        permissionLauncher?.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) ?: run {
            Log.e(TAG, "Permission launcher is null - cannot request permissions")
        }
    }
    
    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(onSuccess: (Location) -> Unit, onError: (Exception) -> Unit) {
        Log.d(TAG, "Getting current location")
        // Cancel any ongoing request
        cancellationTokenSource.cancel()
        cancellationTokenSource = CancellationTokenSource()
        
        // If no permission, return error
        if (!hasLocationPermission()) {
            Log.e(TAG, "Location permission not granted")
            onError(SecurityException("Location permission not granted"))
            return
        }
        
        fusedLocationClient?.let { client ->
            Log.d(TAG, "Requesting current location with high accuracy")
            client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    Log.d(TAG, "Location retrieved: lat=${location.latitude}, lng=${location.longitude}")
                    onSuccess(location)
                } else {
                    Log.e(TAG, "Location is null")
                    onError(Exception("Location not available"))
                }
            }.addOnFailureListener { exception ->
                Log.e(TAG, "Failed to get location", exception)
                onError(exception)
            }
        } ?: run {
            Log.e(TAG, "Location client is null")
            onError(Exception("Location client not initialized"))
        }
    }
    
    override fun cleanup() {
        Log.d(TAG, "Cleaning up resources")
        cancellationTokenSource.cancel()
        activity = null
        permissionLauncher = null
    }
} 