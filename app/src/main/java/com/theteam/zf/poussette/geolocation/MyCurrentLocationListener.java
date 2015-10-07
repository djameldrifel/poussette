package com.theteam.zf.poussette.geolocation;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;
import java.util.List;

/**
 * Created by DRIFELM on 02/09/2015.
 */
public class MyCurrentLocationListener implements LocationListener {

    public static Context context;
    @Override
    public void onLocationChanged(Location location) {

        Position position = new Position(location.getLongitude(), location.getLatitude(), location.getAltitude(),new Date());

        List<Position> list = MapsActivity.positionsHistory;


        if(list.isEmpty() || (!list.isEmpty() & !list.get(list.size() - 1).equals(position))){
            MapsActivity.positionsHistory.add(position);
        }

        MapsActivity.setLocation(location.getLongitude(), location.getLatitude(), location.getAltitude());

        Log.v("Log inside my current ", "***********************************************************************");
//        Log.v("test logique",""+(list.isEmpty() || (!list.isEmpty() & !list.get(list.size() - 1).equals(position))));
        Log.v(" positions history (" + MapsActivity.positionsHistory.size() + ") ", MapsActivity.positionsHistory.isEmpty() ? "empty" : MapsActivity.positionsHistory.get(MapsActivity.positionsHistory.size() - 1).toString());
//        Log.v("test equalse",list.get(list.size() - 1).equals(position)+" pos : "+position.toString()+ " other "+list.get(list.size() - 1).toString());
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
