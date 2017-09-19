package com.pola.app.beans;

/**
 * Created by Ajay on 12-Sep-17.
 */
public class TripDetailsBean {


    private int tripId;
    private int tripUserId;
    private String startDateTime;
    private String dropDateTime;
    private int remainingPassengers;

    private String date;
    private String time;
    private int passengers;
    private int fare;
    private String start;
    private double startLat;
    private double startLong;
    private String drop;
    private double dropLat;
    private double dropLong;
    private int distance;
    private int duration;
    private String overviewPolylines;
    private String walkPolylinesStart;
    private String walkPolylinesDrop;
    private int walkDistanceStart;
    private int walkDistanceDrop;
    private int walkDurationStart;
    private int walkDurationDrop;

    //user details
    private String fullName;
    private String dp;
    private String mobile;


    public int getWalkDistanceStart() {
        return walkDistanceStart;
    }

    public void setWalkDistanceStart(int walkDistanceStart) {
        this.walkDistanceStart = walkDistanceStart;
    }

    public int getWalkDistanceDrop() {
        return walkDistanceDrop;
    }

    public void setWalkDistanceDrop(int walkDistanceDrop) {
        this.walkDistanceDrop = walkDistanceDrop;
    }

    public int getWalkDurationStart() {
        return walkDurationStart;
    }

    public void setWalkDurationStart(int walkDurationStart) {
        this.walkDurationStart = walkDurationStart;
    }

    public int getWalkDurationDrop() {
        return walkDurationDrop;
    }

    public void setWalkDurationDrop(int walkDurationDrop) {
        this.walkDurationDrop = walkDurationDrop;
    }

    public String getWalkPolylinesStart() {
        return walkPolylinesStart;
    }

    public void setWalkPolylinesStart(String walkPolylinesStart) {
        this.walkPolylinesStart = walkPolylinesStart;
    }

    public String getWalkPolylinesDrop() {
        return walkPolylinesDrop;
    }

    public void setWalkPolylinesDrop(String walkPolylinesDrop) {
        this.walkPolylinesDrop = walkPolylinesDrop;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getRemainingPassengers() {
        return remainingPassengers;
    }

    public void setRemainingPassengers(int remainingPassengers) {
        this.remainingPassengers = remainingPassengers;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        this.tripId = tripId;
    }

    public int getTripUserId() {
        return tripUserId;
    }

    public void setTripUserId(int tripUserId) {
        this.tripUserId = tripUserId;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getDropDateTime() {
        return dropDateTime;
    }

    public void setDropDateTime(String dropDateTime) {
        this.dropDateTime = dropDateTime;
    }

    public String getOverviewPolylines() {
        return overviewPolylines;
    }

    public void setOverviewPolylines(String overviewPolylines) {
        this.overviewPolylines = overviewPolylines;
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLong() {
        return startLong;
    }

    public void setStartLong(double startLong) {
        this.startLong = startLong;
    }

    public double getDropLat() {
        return dropLat;
    }

    public void setDropLat(double dropLat) {
        this.dropLat = dropLat;
    }

    public double getDropLong() {
        return dropLong;
    }

    public void setDropLong(double dropLong) {
        this.dropLong = dropLong;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }

    public int getFare() {
        return fare;
    }

    public void setFare(int fare) {
        this.fare = fare;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getDrop() {
        return drop;
    }

    public void setDrop(String drop) {
        this.drop = drop;
    }


}

