package com.megliosolutions.pobail.Objects;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Meglio on 6/13/16.
 */
public class TagObject {


    public String title;
    public String tag;
    public double lat;
    public double lng;
    public String key;
    public String permission;

    public TagObject() {
    }

    public TagObject(String title, double lat, double lng, String key, String permission) {
        this.title = title;
        this.lat = lat;
        this.lng = lng;
        this.key = key;
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    public String getTitle() {
        return title;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getKey() {
        return key;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }
}
