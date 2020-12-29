package com.example.mapclustring.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.location.LocationRequest
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.PermissionListener
import com.patloew.colocation.CoGeocoder
import com.patloew.colocation.CoLocation
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class LiveLocationViewModel (private val context: Context) : ViewModel(), LifecycleObserver {

    //location updates objects
    private val coLocation = CoLocation.from(context)
    private val coGeoCoder = CoGeocoder.from(context)

    private val locationRequest: LocationRequest = LocationRequest.create()
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        .setInterval(900000)    //interval set to 15 min
        .setFastestInterval(2500)

    private val _locationUpdates: MutableLiveData<Location> = MutableLiveData()
    val locationUpdates: LiveData<Location> = _locationUpdates
    val addressUpdates: LiveData<Address?> = _locationUpdates.switchMap { location ->
        liveData { emit(coGeoCoder.getAddressFromLocation(location)) }
    }
    private val _resolveSettingsEvent: MutableLiveData<CoLocation.SettingsResult.Resolvable> = MutableLiveData()
    val resolveSettingsEvent: LiveData<CoLocation.SettingsResult.Resolvable> = _resolveSettingsEvent

    private var locationUpdatesJob: Job? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        startLocationUpdatesAfterCheck()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onStop() {
        locationUpdatesJob?.cancel()
        locationUpdatesJob = null
    }

    private fun startLocationUpdatesAfterCheck() {
        viewModelScope.launch {
            val settingsResult = coLocation.checkLocationSettings(locationRequest)
            when (settingsResult) {
                CoLocation.SettingsResult.Satisfied -> {
                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        askPermissions()
                        return@launch
                    }
                    coLocation.getLastLocation()?.run(_locationUpdates::postValue)
                    startLocationUpdates()
                }
                is CoLocation.SettingsResult.Resolvable -> _resolveSettingsEvent.postValue(settingsResult)
                else -> { /* Ignore for now, we can't resolve this anyway */
                }
            }
        }
    }

    fun startLocationUpdates() {
        locationUpdatesJob?.cancel()
        locationUpdatesJob = viewModelScope.launch {
            try {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    askPermissions()
                    return@launch
                }
                coLocation.getLocationUpdates(locationRequest).collect { location ->
                    Log.d("MainViewModel", "Location update received: $location")
                    _locationUpdates.postValue(location)
                }
            } catch (e: CancellationException) {
                Log.e("MainViewModel", "Location updates cancelled", e)
            }
        }
    }

    private fun askPermissions() {
        Dexter.withContext(context)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : PermissionListener {
                override fun onPermissionRationaleShouldBeShown(
                    permission: com.karumi.dexter.listener.PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

                override fun onPermissionGranted(response: PermissionGrantedResponse?) {
                    startLocationUpdates()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse?) {
                    Toast.makeText(
                        context,
                        "Permissions denied",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }).check()
    }

}