package com.pola.app.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.pola.app.R;
import com.pola.app.Utils.Singleton;
import com.pola.app.Utils.Utils;
import com.pola.app.beans.GenericResponseBean;
import com.pola.app.beans.GetTripsResponseBean;
import com.pola.app.beans.TripDetailsBean;

import java.util.Arrays;
import java.util.List;

public class TripsSearchActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, OnMapReadyCallback {

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private static final String LOG_TAG = MapActivity.class.toString();
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = null;
    private TextView ownerName;
    private ProgressDialog loader;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mMap;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(TripsSearchActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trips_list_map);

        loader = new ProgressDialog(TripsSearchActivity.this);
        loader.setMessage("Hang on...");
        loader.setIndeterminate(true);
        loader.setCancelable(false);

        //set link to view
        ownerName = (TextView) findViewById(R.id.ownerName);

        //check for permission
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //show maps code
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

        mMap.setPadding(50, 350, 50, 150);

        populateTrips();

    }

    private void populateTrips() {
        mMap.clear();
        GenericResponseBean beanMain = Singleton.responseBean;
        GetTripsResponseBean tripsBean = (GetTripsResponseBean) beanMain.getDataBean();
        for (TripDetailsBean bean : tripsBean.getTrips()) {

            loader.dismiss();

            LatLngBounds.Builder builder = new LatLngBounds.Builder();

            //add owner details
            ownerName.setText(bean.getFullName());

            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(bean.getStart())
                    .position(new LatLng(bean.getStartLat(), bean.getStartLong())));

            mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(bean.getStart())
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
                    new Dot(), new Gap(5));
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
                            .title("Your Origin")
                            .position(new LatLng(pointsPassengerStart.get(i).latitude, pointsPassengerStart.get(i).longitude)));
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
                            .title("Your Destination")
                            .position(new LatLng(pointsPassengerDrop.get(i).latitude, pointsPassengerDrop.get(i).longitude)));
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

        }
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


}
