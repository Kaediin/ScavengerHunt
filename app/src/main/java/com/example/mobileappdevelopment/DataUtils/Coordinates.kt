package com.example.mobileappdevelopment.DataUtils

import com.google.android.gms.maps.model.LatLng
import kotlin.collections.ArrayList

object Coordinates {
    var coordinates: MutableList<LatLng> = ArrayList()
    fun addCoordinates(latLng: LatLng?) {
        coordinates.add(latLng!!)
    }

    val coordinatesList: MutableList<LatLng>
        get() = coordinates
}