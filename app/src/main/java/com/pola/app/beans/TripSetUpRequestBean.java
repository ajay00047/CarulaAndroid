package com.pola.app.beans;

import android.content.Context;

/**
 * Created by Ajay on 08-Sep-17.
 */
public class TripSetUpRequestBean extends BaseRequestBean {

    public TripSetUpRequestBean(Context context){
        super(context);
    }

    TripDetailsBean tripDetailsBean;

    public TripDetailsBean getTripDetailsBean() {
        return tripDetailsBean;
    }

    public void setTripDetailsBean(TripDetailsBean tripDetailsBean) {
        this.tripDetailsBean = tripDetailsBean;
    }
}
