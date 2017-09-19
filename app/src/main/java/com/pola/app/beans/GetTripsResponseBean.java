package com.pola.app.beans;

import android.content.Context;

import com.pola.app.delegates.DataBean;

import java.util.List;

/**
 * Created by Ajay on 08-Sep-17.
 */
public class GetTripsResponseBean extends GenericErrorResponseBean implements DataBean {

    List<TripDetailsBean> trips;

    public List<TripDetailsBean> getTrips() {
        return trips;
    }

    public void setTrips(List<TripDetailsBean> trips) {
        this.trips = trips;
    }
}
