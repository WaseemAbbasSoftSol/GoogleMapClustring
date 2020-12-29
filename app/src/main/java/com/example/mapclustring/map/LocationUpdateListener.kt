package com.example.mapclustring.map

import com.google.android.gms.maps.model.LatLng

fun interface LocationUpdateListener {
    fun onLocationReceived(latLng: LatLng)
}