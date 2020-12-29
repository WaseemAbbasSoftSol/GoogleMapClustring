package com.example.mapclustring.map

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.mapclustring.data.REQUEST_LOCATION_SETTING
import com.google.android.gms.maps.model.LatLng

abstract class LiveLocationFragment: Fragment(), LocationUpdateListener {

    protected abstract val mViewModel: LiveLocationViewModel

    var mCurrentLatLong = LatLng(0.0, 0.0)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        lifecycle.addObserver(mViewModel)
        mViewModel.locationUpdates.observe(viewLifecycleOwner, {
            if (it == null ) return@observe
            mCurrentLatLong =  LatLng(it.latitude, it.longitude)
            onLocationReceived(mCurrentLatLong)
        })
        mViewModel.resolveSettingsEvent.observe(viewLifecycleOwner, {
            it.resolve(activity as Activity, REQUEST_LOCATION_SETTING)
        })
    }

    abstract fun onLocationPermissionDenied()

}