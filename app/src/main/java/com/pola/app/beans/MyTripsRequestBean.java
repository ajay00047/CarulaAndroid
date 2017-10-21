package com.pola.app.beans;

import android.content.Context;

/**
 * Created by Ajay on 05-Sep-17.
 */
public class MyTripsRequestBean extends BaseRequestBean {

    public MyTripsRequestBean(Context context){
        super(context);
    }

    private String iam;
    private int page;
    private long tripId;

    public long getTripId() {
        return tripId;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public String getIam() {
        return iam;
    }

    public void setIam(String iam) {
        this.iam = iam;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
