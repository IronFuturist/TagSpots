package com.megliosolutions.ipd.Objects;

import android.graphics.Bitmap;

/**
 * Created by Meglio on 6/13/16.
 */
public class NodeObject {


    public String staticAddress;
    public String latitude;
    public String longitude;

    public NodeObject(){
        //needed for firebase
    }

    public NodeObject(String staticAddress, String latitude, String longitude) {
        this.staticAddress = staticAddress;
        this.latitude = latitude;
        this.longitude = longitude;
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
