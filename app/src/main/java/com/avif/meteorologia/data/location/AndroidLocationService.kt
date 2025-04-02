package com.avif.meteorologia.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
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
    
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var permissionLauncher: ActivityResultLauncher<Array<String>>? = null
    private var activity: Activity? = null
    
    // For canceling location requests
    private var cancellationTokenSource = CancellationTokenSource()
    
    /**
     * Initialize with activity for permission requests
     */
    override fun initialize(activity: Activity, permissionLauncher: ActivityResultLauncher<Array<String>>) {
        this.activity = activity
        this.permissionLauncher = permissionLauncher
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    }
    
    override fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    override fun requestLocationPermission() {
        permissionLauncher?.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
    
    @SuppressLint("MissingPermission")
    override fun getCurrentLocation(onSuccess: (Location) -> Unit, onError: (Exception) -> Unit) {
        // Cancel any ongoing request
        cancellationTokenSource.cancel()
        cancellationTokenSource = CancellationTokenSource()
        
        // If no permission, return error
        if (!hasLocationPermission()) {
            onError(SecurityException("Location permission not granted"))
            return
        }
        
        fusedLocationClient?.let { client ->
            client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location ->
                if (location != null) {
                    onSuccess(location)
                } else {
                    onError(Exception("Location not available"))
                }
            }.addOnFailureListener { exception ->
                onError(exception)
            }
        } ?: run {
            onError(Exception("Location client not initialized"))
        }
    }
    
    override fun cleanup() {
        cancellationTokenSource.cancel()
        activity = null
        permissionLauncher = null
    }
} 