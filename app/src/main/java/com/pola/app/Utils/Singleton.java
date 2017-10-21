package com.pola.app.Utils;

import com.pola.app.beans.GenericErrorResponseBean;
import com.pola.app.beans.GenericResponseBean;
import com.pola.app.beans.Route;
import com.pola.app.beans.TripDetailsBean;
import com.pola.app.beans.UserBean;

/**
 * Created by Ajay on 05-Sep-17.
 */
public class Singleton {

    public static UserBean userBean = new UserBean();
    public static boolean isAvialable = false;
    public static boolean isOwner = false;
    public static boolean rideNow=false;
    public static TripDetailsBean tripDetailsBean = new TripDetailsBean();
    public static Route route = new Route();
    public static GenericResponseBean responseBean;
    public static GenericResponseBean responseBeanTrips;

    private Singleton() {
    }

    public static String getString() {
        return "SingleTone:[TripDetailsBean]"
                + "+tripDetailsBean.getDate() : " + tripDetailsBean.getDate()
                + "+tripDetailsBean.getTime() : " + tripDetailsBean.getTime()
                + "+tripDetailsBean.getFare() : " + tripDetailsBean.getFare()
                + "+tripDetailsBean.getPassengers() : " + tripDetailsBean.getPassengers()
                + "+tripDetailsBean.getStart() : " + tripDetailsBean.getStart()
                + "+tripDetailsBean.getDrop() : " + tripDetailsBean.getDrop()
                + "+tripDetailsBean.getOverview_polylines() : " + tripDetailsBean.getOverviewPolylines();
    }
}
