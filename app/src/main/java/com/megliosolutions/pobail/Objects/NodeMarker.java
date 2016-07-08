package com.megliosolutions.pobail.Objects;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Meglio on 6/27/16.
 */
public class NodeMarker {

    public Double mLat;
    public Double mLong;
    public String mTitle;
    public String mSnippet;
    public int mIcon;

    public NodeMarker(Double mLat, Double mLong, String mTitle, String mSnippet, int mIcon) {
        this.mLat = mLat;
        this.mLong = mLong;
        this.mTitle = mTitle;
        this.mSnippet = mSnippet;
        this.mIcon = mIcon;
    }

    public Double getmLat() {
        return mLat;
    }

    public void setmLat(Double mLat) {
        this.mLat = mLat;
    }

    public Double getmLong() {
        return mLong;
    }

    public void setmLong(Double mLong) {
        this.mLong = mLong;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmSnippet() {
        return mSnippet;
    }

    public void setmSnippet(String mSnippet) {
        this.mSnippet = mSnippet;
    }

    public int getmIcon() {
        return mIcon;
    }

    public void setmIcon(int mIcon) {
        this.mIcon = mIcon;
    }
}
