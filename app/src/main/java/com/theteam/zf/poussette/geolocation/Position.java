package com.theteam.zf.poussette.geolocation;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by DRIFELM on 20/09/2015.
 */
public class Position {

    private double latitude;
    private double longitude;
    private double altitude;

    private Date date;

    public Position(){}

    public Position(double latitude,double longitude,double altitude,Date date){
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LatLng getLatLng(){
        return new LatLng(latitude,longitude);
    }

    @Override
    public String toString() {
        return latitude+" "+longitude+" "+altitude;
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof Position){
            if(latitude==((Position) o).latitude && longitude== ((Position) o).longitude){
                return true;
            }
        }
        return false;
    }
}
