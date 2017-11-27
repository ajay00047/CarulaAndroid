package com.pola.app.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pola.app.R;
import com.pola.app.Utils.API;
import com.pola.app.Utils.Constants;
import com.pola.app.Utils.DBHelper;
import com.pola.app.Utils.ErrorCodes;
import com.pola.app.Utils.Singleton;
import com.pola.app.Utils.StringUtil;
import com.pola.app.Utils.Utils;
import com.pola.app.beans.GenericErrorResponseBean;
import com.pola.app.beans.GenericResponseBean;
import com.pola.app.beans.GetTripsResponseBean;
import com.pola.app.beans.MyTripsRequestBean;
import com.pola.app.beans.TripDetailsBean;
import com.pola.app.beans.TripStatusChangeRequestBean;
import com.pola.app.services.HttpService;

import java.util.Arrays;
import java.util.List;

public class TripRequestsActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String LOG_TAG = MapActivity.class.toString();
    private static final int GOOGLE_API_CLIENT_ID = 0;
    GenericResponseBean responseBean;
    private ProgressDialog loader;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;
    private List<TripDetailsBean> trips;
    private TextView ownerName, tripNoOutOf, startTime, startFrom, drop;
    private ImageView next, prev;
    private LinearLayout reject, accept, call, accepted, cancel, rejected, noRequests;
    private int currentTrip = 0;
    private TripStatusChangeRequestBean requestBeanStatusChange;
    private MyTripsRequestBean requestBean;
    private DBHelper dbHelper;
    private CardView cardView;
    private String tripStatus, tripStart, tripDrop, tripPolylines;
    private double tripStartLat, tripStartLong, tripDropLat, tripDropLong;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(TripRequestsActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_trip_requests);

            Bundle extras = getIntent().getExtras();

            requestBeanStatusChange = new TripStatusChangeRequestBean(getApplicationContext());
            requestBean = new MyTripsRequestBean(getApplicationContext());

            requestBean.setTripId(Long.parseLong(extras.getString("tripId")));
            tripStatus = extras.getString("tripStatus");
            tripStart = extras.getString("tripStart");
            tripStartLat = Double.parseDouble(extras.getString("tripStartLat"));
            tripStartLong = Double.parseDouble(extras.getString("tripStartLong"));
            tripDrop = extras.getString("tripDrop");
            tripDropLat = Double.parseDouble(extras.getString("tripDropLat"));
            tripDropLong = Double.parseDouble(extras.getString("tripDropLong"));
            tripPolylines = extras.getString("tripPolylines");


            //setup DBHelper
            dbHelper = new DBHelper(this);

            ownerName = (TextView) findViewById(R.id.owner_name);
            tripNoOutOf = (TextView) findViewById(R.id.trip_no_out_of);
            drop = (TextView) findViewById(R.id.drop);
            startTime = (TextView) findViewById(R.id.start_time);
            startFrom = (TextView) findViewById(R.id.start_from);
            reject = (LinearLayout) findViewById(R.id.reject);
            accept = (LinearLayout) findViewById(R.id.accept);
            accepted = (LinearLayout) findViewById(R.id.accepted);
            rejected = (LinearLayout) findViewById(R.id.rejected);
            cancel = (LinearLayout) findViewById(R.id.cancel);
            call = (LinearLayout) findViewById(R.id.call);
            prev = (ImageView) findViewById(R.id.prev);
            next = (ImageView) findViewById(R.id.next);
            cardView = (CardView) findViewById(R.id.card_view);
            noRequests = (LinearLayout) findViewById(R.id.no_requests);

            loader = new ProgressDialog(TripRequestsActivity.this);
            loader.setMessage("Moving satellites in position ...");
            loader.setIndeterminate(true);
            loader.setCancelable(false);
            loader.show();
            //check for permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                checkLocationPermission();
            }

            //show maps code
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            if ("Completed".equals(tripStatus))
                cancel.setVisibility(View.GONE);

            //set listeners
            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (currentTrip == 0)
                        return;

                    --currentTrip;
                    ownerName.setText(trips.get(currentTrip).getFullName());
                    startTime.setText(StringUtil.convertSQLDate2Java(trips.get(currentTrip).getWalkStartDateTime()));
                    startFrom.setText(trips.get(currentTrip).getWalkStartLoc());
                    drop.setText(trips.get(currentTrip).getWalkDropLoc() + "");


                    tripNoOutOf.setText((currentTrip + 1) + "/" + trips.size());

                    populateTrip(currentTrip);

                    if (currentTrip == trips.size() - 1) {
                        next.setImageResource(R.mipmap.next_disabled);
                    } else
                        next.setImageResource(R.mipmap.next);

                    if (currentTrip == 0) {
                        prev.setImageResource(R.mipmap.prev_disabled);
                    } else
                        prev.setImageResource(R.mipmap.prev);

                    if ("ACP".equals(trips.get(currentTrip).getStatus())) {
                        accepted.setVisibility(View.VISIBLE);
                        rejected.setVisibility(View.GONE);
                        reject.setVisibility(View.GONE);
                        accept.setVisibility(View.GONE);
                        call.setVisibility(View.VISIBLE);
                    } else if ("REJ".equals(trips.get(currentTrip).getStatus())) {
                        accepted.setVisibility(View.GONE);
                        rejected.setVisibility(View.VISIBLE);
                        reject.setVisibility(View.GONE);
                        accept.setVisibility(View.GONE);
                        call.setVisibility(View.GONE);
                    } else {
                        accepted.setVisibility(View.GONE);
                        rejected.setVisibility(View.GONE);
                        reject.setVisibility(View.VISIBLE);
                        accept.setVisibility(View.VISIBLE);
                        call.setVisibility(View.VISIBLE);
                    }
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (currentTrip == trips.size() - 1)
                        return;


                    ++currentTrip;
                    ownerName.setText(trips.get(currentTrip).getFullName());
                    startTime.setText(StringUtil.convertSQLDate2Java(trips.get(currentTrip).getWalkStartDateTime()));
                    startFrom.setText(trips.get(currentTrip).getWalkStartLoc());
                    drop.setText(trips.get(currentTrip).getWalkDropLoc() + "");

                    tripNoOutOf.setText((currentTrip + 1) + "/" + trips.size());

                    populateTrip(currentTrip);

                    if (currentTrip == trips.size() - 1) {
                        next.setImageResource(R.mipmap.next_disabled);
                    } else
                        next.setImageResource(R.mipmap.next);

                    if (currentTrip == 0) {
                        prev.setImageResource(R.mipmap.prev_disabled);
                    } else
                        prev.setImageResource(R.mipmap.prev);

                    if ("ACP".equals(trips.get(currentTrip).getStatus())) {
                        accepted.setVisibility(View.VISIBLE);
                        rejected.setVisibility(View.GONE);
                        reject.setVisibility(View.GONE);
                        accept.setVisibility(View.GONE);
                        call.setVisibility(View.VISIBLE);
                    } else if ("REJ".equals(trips.get(currentTrip).getStatus())) {
                        accepted.setVisibility(View.GONE);
                        rejected.setVisibility(View.VISIBLE);
                        reject.setVisibility(View.GONE);
                        accept.setVisibility(View.GONE);
                        call.setVisibility(View.GONE);
                    } else {
                        accepted.setVisibility(View.GONE);
                        rejected.setVisibility(View.GONE);
                        reject.setVisibility(View.VISIBLE);
                        accept.setVisibility(View.VISIBLE);
                        call.setVisibility(View.VISIBLE);
                    }

                }
            });

            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    loader.setMessage("Processing request...");
                    loader.show();
                    requestBeanStatusChange.setAction("7");
                    requestBeanStatusChange.setTripId(trips.get(currentTrip).getTripId());
                    requestBeanStatusChange.setTripRequestId(trips.get(currentTrip).getTripRequestId());
                    requestBeanStatusChange.setStatus("REJ");
                    Log.e(Constants.LOG_TAG, "Current Trip : " + currentTrip);
                    new ChangeTripStatusTask().execute(null, null, null);
                }
            });

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    loader.setMessage("Processing request...");
                    loader.show();
                    requestBeanStatusChange.setAction("7");
                    requestBeanStatusChange.setTripId(trips.get(currentTrip).getTripId());
                    requestBeanStatusChange.setTripRequestId(trips.get(currentTrip).getTripRequestId());
                    requestBeanStatusChange.setStatus("ACP");
                    Log.e(Constants.LOG_TAG, "Current Trip : " + currentTrip);
                    new ChangeTripStatusTask().execute(null, null, null);
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    new AlertDialog.Builder(TripRequestsActivity.this)
                            .setTitle("Cancel Ride")
                            .setMessage("Do you really want to cancel this ride?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    loader.setMessage("Processing request...");
                                    loader.show();
                                    requestBeanStatusChange.setAction("77");
                                    requestBeanStatusChange.setTripId(requestBean.getTripId());
                                    requestBeanStatusChange.setStatus("CAN");
                                    Log.e(Constants.LOG_TAG, "Current Trip : " + currentTrip);
                                    new ChangeTripStatusTask().execute(null, null, null);
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();


                }
            });

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(TripRequestsActivity.this,
                                    "No Call Permission", Toast.LENGTH_SHORT).show();
                        }
                        String number = "tel:+91" + trips.get(currentTrip).getMobile();
                        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                        startActivity(callIntent);

                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(TripRequestsActivity.this,
                                "Call failed, please try again later!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //load trip requests
        new TripRequestsTask().execute(null, null, null);

        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();

        }


        LatLng loc = new LatLng(19.228825, 72.854118);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 6));

        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);

        mMap.setPadding(50, 160, 50, 700);

    }

    ;

    private void populateTrip(int index) {

        loader.setMessage("Loading trip ...");
        loader.show();


        mMap.clear();
        GenericResponseBean beanMain = Singleton.responseBeanTrips;
        GetTripsResponseBean tripsBean = (GetTripsResponseBean) beanMain.getDataBean();
        TripDetailsBean bean = tripsBean.getTrips().get(index);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_start_icon))
                .title(bean.getStart())
                .position(new LatLng(bean.getStartLat(), bean.getStartLong())));

        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_end_icon))
                .title(bean.getDrop())
                .position(new LatLng(bean.getDropLat(), bean.getDropLong())));
        PolylineOptions polylineOptionsOwner = new PolylineOptions().
                geodesic(true).
                color(Color.BLACK).
                width(13);
        List<LatLng> pointsOwner = Utils.decodePolyLine(bean.getOverviewPolylines());
        for (int i = 0; i < pointsOwner.size(); i++)
            polylineOptionsOwner.add(pointsOwner.get(i));
        mMap.addPolyline(polylineOptionsOwner);


        //add passenger polyline
        List<PatternItem> pattern = Arrays.<PatternItem>asList(
                new Dot(), new Gap(7));
        //start
        PolylineOptions polylineOptionsPassengerStart = new PolylineOptions().
                geodesic(true).
                color(Color.parseColor("#00B3FD")).
                pattern(pattern).
                width(20);
        List<LatLng> pointsPassengerStart = Utils.decodePolyLine(bean.getWalkPolylinesStart());
        for (int i = 0; i < pointsPassengerStart.size(); i++) {
            if (i == 0) {
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.walking_bag))
                        .title("Passenger Origin")
                        .position(new LatLng(pointsPassengerStart.get(i).latitude, pointsPassengerStart.get(i).longitude)));
                builder.include(new LatLng(pointsPassengerStart.get(i).latitude, pointsPassengerStart.get(i).longitude));
            }

               /* if (i == pointsPassengerStart.size() - 1) {
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                            .title("Your Start Location")
                            .position(new LatLng(pointsPassengerStart.get(i).latitude, pointsPassengerStart.get(i).longitude)));
                }
*/

            polylineOptionsPassengerStart.add(pointsPassengerStart.get(i));
            mMap.addPolyline(polylineOptionsPassengerStart);
        }

        //drop
        //start
        PolylineOptions polylineOptionsPassengerDrop = new PolylineOptions().
                geodesic(true).
                color(Color.parseColor("#00B3FD")).
                pattern(pattern).
                width(20);
        List<LatLng> pointsPassengerDrop = Utils.decodePolyLine(bean.getWalkPolylinesDrop());
        for (int i = 0; i < pointsPassengerDrop.size(); i++) {
                /*if (i == pointsPassengerDrop.size() - 1) {
                    mMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                            .title("Your Drop Location")
                            .position(new LatLng(pointsPassengerDrop.get(i).latitude, pointsPassengerDrop.get(i).longitude)));
                }*/

            if (i == 0) {
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.standing_bag))
                        .title("Passenger Destination")
                        .position(new LatLng(pointsPassengerDrop.get(i).latitude, pointsPassengerDrop.get(i).longitude)));
                builder.include(new LatLng(pointsPassengerDrop.get(i).latitude, pointsPassengerDrop.get(i).longitude));
            }


            polylineOptionsPassengerDrop.add(pointsPassengerDrop.get(i));
            mMap.addPolyline(polylineOptionsPassengerDrop);
        }

        //build the map
        builder.include(new LatLng(bean.getStartLat(), bean.getStartLong()));
        builder.include(new LatLng(bean.getDropLat(), bean.getDropLong()));
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);

        mMap.animateCamera(cu);

        trips = tripsBean.getTrips();

        Log.e(Constants.LOG_TAG, "responseBeanTrips : " + trips.size());

        //set up first trip details
        ownerName.setText(trips.get(0).getFullName());
        startTime.setText(StringUtil.convertSQLDate2Java(trips.get(0).getWalkStartDateTime()));
        startFrom.setText(trips.get(currentTrip).getWalkStartLoc());
        drop.setText(trips.get(currentTrip).getWalkDropLoc() + "");

        tripNoOutOf.setText((currentTrip + 1) + "/" + trips.size());

        if ("ACP".equals(trips.get(currentTrip).getStatus())) {
            accepted.setVisibility(View.VISIBLE);
            rejected.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
            call.setVisibility(View.VISIBLE);
        } else if ("REJ".equals(trips.get(currentTrip).getStatus())) {
            accepted.setVisibility(View.GONE);
            rejected.setVisibility(View.VISIBLE);
            reject.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
            call.setVisibility(View.GONE);
        } else {
            accepted.setVisibility(View.GONE);
            rejected.setVisibility(View.GONE);
            reject.setVisibility(View.VISIBLE);
            accept.setVisibility(View.VISIBLE);
            call.setVisibility(View.VISIBLE);
        }

        if (trips.size() > 1)
            next.setImageResource(R.mipmap.next);

        loader.dismiss();

    }

    private void populateOwnerTrip() {

        loader.setMessage("Loading trip ...");
        loader.show();


        mMap.clear();

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_start_icon))
                .title(tripStart)
                .position(new LatLng(tripStartLat, tripStartLong)));

        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.car_end_icon))
                .title(tripDrop)
                .position(new LatLng(tripDropLat, tripDropLong)));
        PolylineOptions polylineOptionsOwner = new PolylineOptions().
                geodesic(true).
                color(Color.BLACK).
                width(13);
        List<LatLng> pointsOwner = Utils.decodePolyLine(tripPolylines);
        for (int i = 0; i < pointsOwner.size(); i++)
            polylineOptionsOwner.add(pointsOwner.get(i));
        mMap.addPolyline(polylineOptionsOwner);

        mMap.setPadding(50, 250, 50, 250);

        //build the map
        builder.include(new LatLng(tripStartLat, tripStartLong));
        builder.include(new LatLng(tripDropLat, tripDropLong));
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);

        mMap.animateCamera(cu);

        loader.dismiss();

    }

    @Override
    public void onConnected(Bundle bundle) {

        Log.i(LOG_TAG, "Google Places API connected.");

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }

                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can add here other case statements according to your requirement.
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MyTripActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Call webservice and check for login
     */
    private class ChangeTripStatusTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                responseBean = HttpService.getResponse(API.changeTripStatusUrl, requestBeanStatusChange, TripRequestsActivity.this);
                loader.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                loader.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (null != responseBean && responseBean.getStatus() == 1) {

                Log.e(Constants.LOG_TAG, this.getClass() + " : " + "Error Code : " + ((GenericErrorResponseBean) responseBean.getDataBean()).getErrorCode());

                if (((GenericErrorResponseBean) responseBean.getDataBean()).getErrorCode().equals(ErrorCodes.CODE_007)) {

                    if (requestBeanStatusChange.getAction().equals("77") && requestBeanStatusChange.getStatus().equals("CAN")) {
                        Toast.makeText(TripRequestsActivity.this, "Ride Cancelled", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MyTripActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (requestBeanStatusChange.getAction().equals("7") && requestBeanStatusChange.getStatus().equals("REJ")) {
                        trips.get(currentTrip).setStatus("REJ");
                        reject.setVisibility(View.GONE);
                        accept.setVisibility(View.GONE);
                        call.setVisibility(View.GONE);
                        accepted.setVisibility(View.GONE);
                        rejected.setVisibility(View.VISIBLE);
                    } else if (requestBeanStatusChange.getAction().equals("7") && requestBeanStatusChange.getStatus().equals("ACP")) {
                        trips.get(currentTrip).setStatus("ACP");
                        reject.setVisibility(View.GONE);
                        accept.setVisibility(View.GONE);
                        call.setVisibility(View.VISIBLE);
                        accepted.setVisibility(View.VISIBLE);
                        rejected.setVisibility(View.GONE);
                    }

                } else {
                    //request_text.setVisibility(View.GONE);
                    Toast.makeText(TripRequestsActivity.this, ((GenericErrorResponseBean) responseBean.getDataBean()).errorMessage, Toast.LENGTH_LONG).show();
                }
            } else if (null != responseBean && responseBean.getStatus() == 0) {
                if (responseBean.getErrorCode().equals(ErrorCodes.CODE_888.toString())) {
                    Toast.makeText(TripRequestsActivity.this, ErrorCodes.CODE_888.getErrorMessage(), Toast.LENGTH_LONG).show();


                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(TripRequestsActivity.this, responseBean.getErrorDescription(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(TripRequestsActivity.this, "Unable to process request!", Toast.LENGTH_LONG).show();
                Log.e(Constants.LOG_TAG, this.getClass() + " : " + "ResponseBean " + responseBean);
            }
        }

    }

    /**
     * Call webservice and check for login
     */
    private class TripRequestsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                responseBean = HttpService.getResponse(API.tripRequestsUrl, requestBean, TripRequestsActivity.this);
                loader.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                loader.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (null != responseBean && responseBean.getStatus() == 1) {

                Log.e(Constants.LOG_TAG, this.getClass() + " : " + "Error Code : " + ((GenericErrorResponseBean) responseBean.getDataBean()).getErrorCode());

                if (((GenericErrorResponseBean) responseBean.getDataBean()).getErrorCode().equals(ErrorCodes.CODE_201)) {

                    Singleton.responseBeanTrips = responseBean;

                    populateTrip(0);
                    cardView.setVisibility(View.VISIBLE);
                    noRequests.setVisibility(View.GONE);
                    mMap.setPadding(50, 160, 50, 700);

                } else {
                    cardView.setVisibility(View.GONE);
                    noRequests.setVisibility(View.VISIBLE);
                    populateOwnerTrip();
                    mMap.setPadding(50, 250, 50, 200);
                }
            } else if (null != responseBean && responseBean.getStatus() == 0) {
                if (responseBean.getErrorCode().equals(ErrorCodes.CODE_888.toString())) {
                    Toast.makeText(TripRequestsActivity.this, ErrorCodes.CODE_888.getErrorMessage(), Toast.LENGTH_SHORT).show();

                    //clear all database
                    dbHelper.clearUsersTable();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(TripRequestsActivity.this, responseBean.getErrorDescription(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(TripRequestsActivity.this, "Unable to process request!", Toast.LENGTH_SHORT).show();
                Log.e(Constants.LOG_TAG, this.getClass() + " : " + "ResponseBean " + responseBean);
                finish();
            }
        }

    }

}


