package com.example.mobileappdevelopment;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

class Coordinates {

    private static List<LatLng> coordinates = new ArrayList<>();

    static void addCoordinates(LatLng latLng) {
        coordinates.add(latLng);
    }

    static List<LatLng> getCoordinatesList() {
        return coordinates;
    }
}
