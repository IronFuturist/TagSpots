package com.megliosolutions.ipd.Objects;

import android.graphics.Bitmap;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Meglio on 6/13/16.
 */
public class NodeObject {


    public String staticAddress;
    public String description;
    public String latitude;
    public String longitude;
    public String key;

    public NodeObject(){
        //needed for firebase
    }

    public NodeObject(String staticAddress, String description, String latitude, String longitude, String key) {
        this.staticAddress = staticAddress;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.key = key;
    }

    public String getDescription() {
        return description;
    }

    public String getStaticAddress() {
        return staticAddress;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStaticAddress(String staticAddress) {
        this.staticAddress = staticAddress;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


}
