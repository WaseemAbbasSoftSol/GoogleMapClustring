package com.example.mapclustring.model

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

data class Dealer(
    val id:Int=0,
    val name: String? = null,
    val profilePhoto: String = "",
    val mPosition: LatLng? = null,
    val address:String
):ClusterItem {
    override fun getPosition(): LatLng = mPosition!!

    override fun getTitle(): String? = name

    override fun getSnippet(): String = address
}