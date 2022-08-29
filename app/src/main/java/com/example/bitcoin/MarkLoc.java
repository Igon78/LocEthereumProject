package com.example.bitcoin;

import com.google.android.gms.maps.model.Marker;

import java.math.BigDecimal;

public class MarkLoc {
    private String creator;
    private String message;
    private String value;
    private double lat;
    private double longi;
    public MarkLoc(String creator, String message, String value, double lat, double longi) {
        this.creator = creator;
        this.message = message;
        this.value = value;
        this.lat = lat;
        this.longi = longi;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLongi() {
        return longi;
    }

    public void setLongi(double longi) {
        this.longi = longi;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
