package com.example.mobileappdevelopment;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

class Coordinates {

    private static List<LatLng> coordiantes = new ArrayList<>();

    static void addCoordinates(LatLng latLng){
        coordiantes.add(latLng);
    }

    static List<LatLng> getCoordinatesList(){
        return coordiantes;
    }
}
