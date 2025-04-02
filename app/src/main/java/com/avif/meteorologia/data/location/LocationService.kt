package com.avif.meteorologia.data.location

import android.app.Activity
import android.location.Location
import androidx.activity.result.ActivityResultLauncher

interface LocationService {
    /**
     * Initialize the location service with activity and permission launcher
     * @param activity The activity for context
     * @param permissionLauncher The launcher for requesting permissions
     */
    fun initialize(activity: Activity, permissionLauncher: ActivityResultLauncher<Array<String>>)
    
    /**
     * Get the current location if available
     * @param onSuccess Called when location is retrieved successfully
     * @param onError Called when an error occurs retrieving location
     */
    fun getCurrentLocation(
        onSuccess: (Location) -> Unit,
        onError: (Exception) -> Unit
    )
    
    /**
     * Checks if the app has required location permissions
     * @return true if permissions are granted, false otherwise
     */
    fun hasLocationPermission(): Boolean
    
    /**
     * Request location permissions from the user
     */
    fun requestLocationPermission()
    
    /**
     * Clean up resources used by the location service
     */
    fun cleanup()
} 