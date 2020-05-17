package com.example.mobileappdevelopment.utils

import com.google.android.gms.maps.model.LatLng
import kotlin.collections.ArrayList

object Coordinates {
    var coordinates: MutableList<LatLng> = ArrayList()
    fun addCoordinates(latLng: LatLng?) {
        this.coordinates.add(latLng!!)
    }

//    val coordinates: MutableList<LatLng>
//        get() = this.coordinates
}