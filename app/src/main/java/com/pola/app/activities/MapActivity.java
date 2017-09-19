package com.pola.app.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.pola.app.R;
import com.pola.app.Utils.Constants;
import com.pola.app.Utils.DirectionFinder;
import com.pola.app.Utils.Singleton;
import com.pola.app.Utils.Utils;
import com.pola.app.beans.Route;
import com.pola.app.delegates.DirectionFinderListener;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class MapActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks,OnMapReadyCallback,LocationListener,DirectionFinderListener {
    private static final String LOG_TAG = MapActivity.class.toString();
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private AutoCompleteTextView mStartAuotComplete;
    private AutoCompleteTextView mDropAuotComplete;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> wayPointMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceStartArrayAdapter;
    private PlaceArrayAdapter mPlaceDropArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = null;
    private ProgressDialog progressDialog;
    private Button btnFindPath;
    private Button btnNext;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private LocationRequest mLocationRequest;
    //private FusedLocationProviderClient mFusedLocationClient;
    private LocationServices lservice;
    private MarkerOptions myLocation;

    private TextView tvDistance;
    private TextView tvDuration;
    private LinearLayout commandsText;
    private LinearLayout commandsButton;

    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().
            setTypeFilter(Place.TYPE_COUNTRY).setCountry("IN").build();


    private GoogleMap mMap;

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MapActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        //check for permission
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        //mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //show maps code
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        tvDuration = (TextView) findViewById(R.id.tvDuration);
        tvDistance = (TextView) findViewById(R.id.tvDistance);

        commandsText=(LinearLayout) findViewById(R.id.commandsText);
        commandsButton=(LinearLayout) findViewById(R.id.commandsButton);

        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        btnNext = (Button) findViewById(R.id.next);

        //Start Location Autocomplete
        mStartAuotComplete = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextViewStart);
        mStartAuotComplete.setThreshold(3);
        mStartAuotComplete.setOnItemClickListener(mAutocompleteStartClickListener);
        mPlaceStartArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, typeFilter);
        mStartAuotComplete.setAdapter(mPlaceStartArrayAdapter);

        //Drop Location Autocomplete
        mDropAuotComplete = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextViewDrop);
        mDropAuotComplete.setThreshold(3);
        mDropAuotComplete.setOnItemClickListener(mAutocompleteDropClickListener);
        mPlaceDropArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, typeFilter);
        mDropAuotComplete.setAdapter(mPlaceDropArrayAdapter);

        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),TripSummaryActivity.class);
                startActivity(intent);
            }
        });
    }

    private void sendRequest() {
        String origin = mStartAuotComplete.getText().toString();
        String destination = mDropAuotComplete.getText().toString();
        if (origin.isEmpty() ) {
            Toast.makeText(this, "Please enter start address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty() ) {
            Toast.makeText(this, "Please enter drop address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination,Singleton.tripDetailsBean.getDate()+" "+Singleton.tripDetailsBean.getTime(),this).execute();

            Singleton.tripDetailsBean.setStart(origin);
            Singleton.tripDetailsBean.setDrop(destination);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
                mMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


        LatLng loc = new LatLng(19.228825,72.854118);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 6));

        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());

        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11), 2000, null);

        mMap.setPadding(50,350,50,150);

    }



    private AdapterView.OnItemClickListener mAutocompleteStartClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Utils.hideSoftKeyboard(MapActivity.this);
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceStartArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdateStartDetailsCallback);
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteDropClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Utils.hideSoftKeyboard(MapActivity.this);
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceDropArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdateDropDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdateStartDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);

            LatLng location = place.getLatLng();

            //set singleton
            Singleton.tripDetailsBean.setStartLat(location.latitude);
            Singleton.tripDetailsBean.setStartLong(location.longitude);

            if (!originMarkers.isEmpty()) {
                for (Marker marker : originMarkers) {
                    marker.remove();
                }
            }

            if (!polylinePaths.isEmpty()) {
                for (Polyline polyline:polylinePaths ) {
                    polyline.remove();
                }
            }

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(place.getName().toString())
                    .position(location).draggable(true).snippet(place.getAddress().toString())));

            // Move the camera instantly to Sydney with a zoom of 15.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
            // Zoom in, animating the camera.
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 500, null);

            //hide next button
            btnFindPath.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            commandsText.setVisibility(View.GONE);

        }
    };

    private ResultCallback<PlaceBuffer> mUpdateDropDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);

            LatLng location = place.getLatLng();

            //set singleton
            Singleton.tripDetailsBean.setDropLat(location.latitude);
            Singleton.tripDetailsBean.setDropLong(location.longitude);

            if (!destinationMarkers.isEmpty()) {
                for (Marker marker : destinationMarkers) {
                    marker.remove();
                }
            }

            if (!polylinePaths.isEmpty()) {
                for (Polyline polyline:polylinePaths ) {
                    polyline.remove();
                }
            }

            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(place.getName().toString())
                    .position(location).draggable(true).snippet(place.getAddress().toString())));

            // Move the camera instantly to Sydney with a zoom of 15.
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
            // Zoom in, animating the camera.
            mMap.animateCamera(CameraUpdateFactory.zoomIn());
            // Zoom out to zoom level 10, animating with a duration of 2 seconds.
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 500, null);

            //hide next button
            btnFindPath.setVisibility(View.VISIBLE);
            btnNext.setVisibility(View.GONE);
            commandsText.setVisibility(View.GONE);

        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceStartArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        mPlaceDropArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
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
        mPlaceStartArrayAdapter.setGoogleApiClient(null);
        mPlaceDropArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);
        mMap.clear();
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();

        for (Route route : routes) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            //set singleton variables
            Singleton.tripDetailsBean.setOverviewPolylines(route.overviewPolylines);
            Singleton.route = route;
            Singleton.tripDetailsBean.setDuration(route.duration.value);
            Singleton.tripDetailsBean.setDistance(route.distance.value);

            tvDuration.setText(route.duration.text);
            tvDistance.setText(route.distance.text);

            commandsText.setVisibility(View.VISIBLE);

            btnFindPath.setVisibility(View.GONE);
            btnNext.setVisibility(View.VISIBLE);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .title(route.startAddress)
                    .position(route.startLocation)));

            for (int i=0;i<route.wayPointAddresses.size();i++){
                String wayPointName = route.wayPointAddresses.get(i);
                LatLng wayPointLatLng = route.wayPointLocations.get(i);

                wayPointMarkers.add(mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                        .title(wayPointName)
                        .position(wayPointLatLng)));

                builder.include(wayPointMarkers.get(i).getPosition());
            }

            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLACK).
                    width(13);

            builder.include(originMarkers.get(originMarkers.size()-1).getPosition());
            builder.include(destinationMarkers.get(destinationMarkers.size()-1).getPosition());

            LatLngBounds bounds = builder.build();

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds,0);

            mMap.animateCamera(cu);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));

            Log.e(Constants.LOG_TAG, String.valueOf(PolyUtil.isLocationOnPath(new LatLng(19.176968, 72.943289), route.points, true, 500.0)));
            Log.e(Constants.LOG_TAG,Singleton.getString());

        }
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission(){
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
                        mMap.setMyLocationEnabled(true);
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
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));


    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}