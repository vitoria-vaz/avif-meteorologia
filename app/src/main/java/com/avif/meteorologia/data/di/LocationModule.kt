package com.avif.meteorologia.data.di

import com.avif.meteorologia.data.location.AndroidLocationService
import com.avif.meteorologia.data.location.LocationService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    @Singleton
    abstract fun bindLocationService(
        locationService: AndroidLocationService
    ): LocationService
} 