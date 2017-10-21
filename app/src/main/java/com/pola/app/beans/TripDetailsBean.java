package com.pola.app.beans;

/**
 * Created by Ajay on 12-Sep-17.
 */
public class TripDetailsBean {



    private int tripRequestId;
    private String tripSequenceId;
    private int tripId;
    private int tripUserId;
    private String startDateTime;
    private String dropDateTime;
    private String walkStartDateTime;
    private String meetingDateTime;
    private int remainingPassengers;
    private int remainingRequests;

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
    private String walkStartLoc;
    private String walkDropLoc;
    private int walkDistanceStart;
    private double walkStartLat;
    private double walkStartLong;
    private double walkDropLat;
    private double walkDropLong;
    private int walkDistanceDrop;
    private int walkDurationStart;
    private int walkDurationDrop;
    private String status;


    //user details
    private String fullName;
    private String dp;
    private String mobile;

    //car details
    private String company;
    private String model;
    private String color;
    private String no;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public int getRemainingRequests() {
        return remainingRequests;
    }

    public void setRemainingRequests(int remainingRequests) {
        this.remainingRequests = remainingRequests;
    }

    public String getWalkStartDateTime() {
        return walkStartDateTime;
    }

    public void setWalkStartDateTime(String walkStartDateTime) {
        this.walkStartDateTime = walkStartDateTime;
    }

    public int getTripRequestId() {
        return tripRequestId;
    }

    public void setTripRequestId(int tripRequestId) {
        this.tripRequestId = tripRequestId;
    }

    public String getTripSequenceId() {
        return tripSequenceId;
    }

    public void setTripSequenceId(String tripSequenceId) {
        this.tripSequenceId = tripSequenceId;
    }

    public String getMeetingDateTime() {
        return meetingDateTime;
    }

    public void setMeetingDateTime(String meetingDateTime) {
        this.meetingDateTime = meetingDateTime;
    }

    public String getWalkStartLoc() {
        return walkStartLoc;
    }

    public void setWalkStartLoc(String walkStartLoc) {
        this.walkStartLoc = walkStartLoc;
    }

    public String getWalkDropLoc() {
        return walkDropLoc;
    }

    public void setWalkDropLoc(String walkDropLoc) {
        this.walkDropLoc = walkDropLoc;
    }

    public double getWalkStartLat() {
        return walkStartLat;
    }

    public void setWalkStartLat(double walkStartLat) {
        this.walkStartLat = walkStartLat;
    }

    public double getWalkStartLong() {
        return walkStartLong;
    }

    public void setWalkStartLong(double walkStartLong) {
        this.walkStartLong = walkStartLong;
    }

    public double getWalkDropLat() {
        return walkDropLat;
    }

    public void setWalkDropLat(double walkDropLat) {
        this.walkDropLat = walkDropLat;
    }

    public double getWalkDropLong() {
        return walkDropLong;
    }

    public void setWalkDropLong(double walkDropLong) {
        this.walkDropLong = walkDropLong;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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



