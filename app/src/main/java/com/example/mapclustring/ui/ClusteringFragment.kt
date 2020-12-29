package com.example.mapclustring.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mapclustring.databinding.FragmentClusteringBinding
import com.example.mapclustring.map.BaseMapFragment
import com.example.mapclustring.model.Dealer
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ClusteringFragment : BaseMapFragment() {

    private lateinit var binding: FragmentClusteringBinding
    private lateinit var clusterManger: ClusterManager<Dealer>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClusteringBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this

        binding.map.onCreate(savedInstanceState)
        binding.map.getMapAsync(this)

        return binding.root
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        super.onMapReady(googleMap)
        clusterManger = ClusterManager<Dealer>(context, googleMap)
//        addItems()
        val dealers = listOf(
            Dealer(1, "WASEEM ABBAS", "http://api.mateam.co/Images/WheelImages/6373743401445769490.png", LatLng(31.5240659, 74.3455807), "Main Market, Lahore"),
            Dealer(2, "Hussnain Liaqat", "http://api.mateam.co/Images/WheelImages/6373687040114428300.png", LatLng(31.4365388, 74.1899747), "Green Forts 2, Lahore"),
            Dealer(3, "Saif Khan", "http://api.mateam.co/Images/WheelImages/6373599860647326750.png", LatLng(34.5061286, 72.3967249), "Faisal Masjid, Islamabad"),
            Dealer(5, "Saif Khan", "http://api.mateam.co/Images/WheelImages/6373743408855393190.png", LatLng(30.1959736, 71.4217473), "Multan International Airport, Multan"),
            Dealer(6, "Naveed Majid", "http://api.mateam.co/Images/WheelImages/6373588969502312810.png", LatLng(34.0169613, 71.4878201), "Peshawar Zoo, Peshawar")
        )
        clusterManger.addItems(dealers)
        googleMap?.setOnCameraIdleListener(clusterManger)
        clusterManger.renderer = DealerRender(requireContext(), googleMap!!, clusterManger)
        clusterManger.cluster()
    }

//    private fun addItems() {
//
//        // Set some lat/lng coordinates to start with.
//        var lat = 51.5145160
//        var lng = -0.1270060
//
//        // Add ten cluster items in close proximity, for purposes of this example.
//        for (i in 0..9) {
//            val offset = i / 60.0
//            lat += offset
//            lng += offset
//            val offsetItem = Dealer(i+1, "Title $i", "", LatLng(lat, lng), "Snippet $i")
//            clusterManger.addItem(offsetItem)
//        }
//    }

    override fun onResume() {
        binding.map.onResume()
        super.onResume()
    }

    override fun onPause() {
        binding.map.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        binding.map.onDestroy()
        super.onDestroy()
    }

}