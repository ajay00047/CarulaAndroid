package com.pola.app.Utils;

/**
 * Created by Ajay on 04-Sep-17.
 */
public class API {

    public static final String HOSTNAME = "http://ec2-35-154-253-253.ap-south-1.compute.amazonaws.com:8080";
    public static final String HOSTNAME_LOCAL = "http://198.168.0.105:8080";
    public final static String loginUrl = "/login";
    public final static String signUpUrl = "/signUp";
    public final static String verifyOTPUrl = "/verifyOTP";
    public final static String dpUploadUrl = "/dpUpload";
    public final static String carSetUpUrl = "/carSetUp";
    public final static String tripSetUpUrl = "/tripSetUp";
    public final static String carDetailsUrl = "/carDetails";
    public final static String getTripUrl = "/getTrip";
    public final static String myTripUrl = "/myTrip";
    public final static String requestTripUrl = "/requestTrip";
    public final static String tripRequestsUrl = "/tripRequests";
    public final static String changeTripStatusUrl = "/changeTripStatus";

    //google APIs
    public static final String GOOGLE_DIRECTION_API = "https://maps.googleapis.com/maps/api/directions/json?";

}
