package com.example.mapclustring.map

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint

abstract class BaseMapFragment : LiveLocationFragment(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null

    override val mViewModel: BaseMapViewModel by viewModels()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel.addressUpdates.observe(viewLifecycleOwner, {
            onAddressReceived(it?.getAddressLine(0))
        })
    }

    protected open fun onAddressReceived(addressLine: String?) {}

    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
//        mMap!!.setOnMarkerDragListener(object : GoogleMap.OnMarkerDragListener {
//            override fun onMarkerDragEnd(marker: Marker?) {
//                marker?.position?.apply {
//                    //update location
//                }
//            }
//
//            override fun onMarkerDragStart(p0: Marker?) {}
//
//            override fun onMarkerDrag(p0: Marker?) {}
//
//        })
        if (mCurrentLatLong.latitude != 0.0) {
            setMarker(mCurrentLatLong)
        }
    }

    override fun onLocationReceived(latLng: LatLng) {
        setMarker(latLng)
    }

    override fun onLocationPermissionDenied() {
        Toast.makeText(context, "permissions denied", Toast.LENGTH_SHORT).show()
    }

    private fun setMarker(latLng: LatLng) = mMap?.apply {
        clear()
        addMarker(MarkerOptions().position(latLng))
        animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
    }

}