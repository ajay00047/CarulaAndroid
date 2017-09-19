package com.pola.app.beans;

import com.google.android.gms.maps.model.LatLng;
import com.pola.app.Utils.Distance;
import com.pola.app.Utils.Duration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;
    public List<String> wayPointAddresses;
    public List<LatLng> wayPointLocations;
    public List<LatLng> points;
    public String overviewPolylines;

    public Route(){
        wayPointAddresses=new ArrayList<>();
        wayPointLocations=new ArrayList<>();
    }
}
