package com.pola.app.Utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.pola.app.beans.Route;
import com.pola.app.delegates.DirectionFinderListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mai Thanh Hiep on 4/3/2016.
 */
public class DirectionFinder {
    private static final String DIRECTION_URL_API = API.GOOGLE_DIRECTION_API;
    private static final String GOOGLE_API_KEY = "AIzaSyCQ7kN27Jmap8-XUDvNKvsNgi76uKFnBkE";
    private DirectionFinderListener listener;
    private String origin;
    private String destination;
    private String dateTime;
    private Context context;

    public DirectionFinder(DirectionFinderListener listener, String origin, String destination, String dateTime, Context context) {
        this.listener = listener;
        this.origin = origin;
        this.destination = destination;
        this.dateTime = dateTime;
        this.context = context;
    }

    public void execute() throws UnsupportedEncodingException {
        listener.onDirectionFinderStart();
        new DownloadRawData().execute(createUrl());
    }

    private String createUrl() throws UnsupportedEncodingException {
        LatLng startPoint = new LatLng(Singleton.tripDetailsBean.getStartLat(),Singleton.tripDetailsBean.getStartLong());
        LatLng dropPoint = new LatLng(Singleton.tripDetailsBean.getDropLat(),Singleton.tripDetailsBean.getDropLong());
        String urlOrigin = URLEncoder.encode(startPoint.latitude + "," + startPoint.longitude, "utf-8");
        String urlDestination = URLEncoder.encode(dropPoint.latitude + "," + dropPoint.longitude, "utf-8");
        //String wayPoints = "&waypoints=Bandra,Mumbai|Marine+lines,Mumbai";
        String departureTime = "&departure_time=" + StringUtil.convertDate2Epoch(dateTime);
        String postURL = DIRECTION_URL_API + "origin=" + urlOrigin + "&destination=" + urlDestination + departureTime
                + "&mode=driving&alternatives=false&avoid=indoor&traffic_model=best_guess" + "&key=" + GOOGLE_API_KEY;
        Log.e(Constants.LOG_TAG, postURL);
        return postURL;
    }

    private void parseJSon(String data) throws JSONException {
        if (data == null)
            return;

        List<Route> routes = new ArrayList<Route>();
        JSONObject jsonData = new JSONObject(data);
        JSONArray jsonRoutes = jsonData.getJSONArray("routes");

        if (jsonRoutes.length() == 0)
            Toast.makeText(context,
                    "No Routes Found. Please try different address",
                    Toast.LENGTH_LONG).show();

        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
            Route route = new Route();

            JSONObject overview_polylineJson = jsonRoute.getJSONObject("overview_polyline");
            JSONArray jsonLegs = jsonRoute.getJSONArray("legs");

            int totalDistance = 0;
            int totalDuration = 0;

            for (int j = 0; j < jsonLegs.length(); j++) {

                JSONObject jsonLeg = jsonLegs.getJSONObject(j);

                JSONObject jsonDistance = jsonLeg.getJSONObject("distance");
                JSONObject jsonDuration = jsonLeg.getJSONObject("duration_in_traffic");
                JSONObject jsonEndLocation = jsonLeg.getJSONObject("end_location");
                JSONObject jsonStartLocation = jsonLeg.getJSONObject("start_location");

                totalDistance += jsonDistance.getInt("value");
                totalDuration += jsonDuration.getInt("value");

                if (j == 0) {
                    route.startAddress = jsonLeg.getString("start_address");
                    route.startLocation = new LatLng(jsonStartLocation.getDouble("lat"), jsonStartLocation.getDouble("lng"));
                }

                if (j == jsonLegs.length() - 1) {
                    route.endAddress = jsonLeg.getString("end_address");
                    route.endLocation = new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng"));
                    route.distance = new Distance(StringUtil.convertM2Km(totalDistance), totalDistance);
                    route.duration = new Duration(StringUtil.convertSec2H(totalDuration), totalDuration);
                }

                if (j != 0 && j != jsonLegs.length() - 1) {
                    route.wayPointAddresses.add(jsonLeg.getString("end_address"));
                    route.wayPointLocations.add(new LatLng(jsonEndLocation.getDouble("lat"), jsonEndLocation.getDouble("lng")));
                }
            }

            route.points = Utils.decodePolyLine(overview_polylineJson.getString("points"));
            route.overviewPolylines = overview_polylineJson.getString("points");
            routes.add(route);
        }

        listener.onDirectionFinderSuccess(routes);
    }

    private class DownloadRawData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String link = params[0];
            try {
                URL url = new URL(link);
                InputStream is = url.openConnection().getInputStream();
                StringBuffer buffer = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String res) {
            try {
                parseJSon(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


}
