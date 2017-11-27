package com.pola.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pola.app.R;
import com.pola.app.Utils.API;
import com.pola.app.Utils.Constants;
import com.pola.app.Utils.DBHelper;
import com.pola.app.Utils.ErrorCodes;
import com.pola.app.Utils.Singleton;
import com.pola.app.beans.GenericErrorResponseBean;
import com.pola.app.beans.GenericResponseBean;
import com.pola.app.beans.TripSetUpRequestBean;
import com.pola.app.beans.UserBean;
import com.pola.app.services.HttpService;

public class TripSummaryActivity extends AppCompatActivity {

    //beans
    TripSetUpRequestBean requestBean;
    GenericResponseBean responseBean;
    private LinearLayout dateLayout, timeLayout, fareLayout, passengersLayout;
    private TextView date, from, time, to, fare, passengers;
    private Button submit;
    private ProgressDialog loader;
    private DBHelper dbHelper;
    private UserBean userBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_trip_summary);


            //Set up request bean
            requestBean = new TripSetUpRequestBean(getApplicationContext());
            //setup DBHelper
            dbHelper = new DBHelper(this);
            userBean = dbHelper.getUserDetails();

            loader = new ProgressDialog(TripSummaryActivity.this);
            loader.setMessage("Submitting data...");
            loader.setIndeterminate(true);
            loader.setCancelable(false);

            dateLayout = (LinearLayout) findViewById(R.id.date_label);
            timeLayout = (LinearLayout) findViewById(R.id.time_label);
            fareLayout = (LinearLayout) findViewById(R.id.fare_label);
            passengersLayout = (LinearLayout) findViewById(R.id.passengers_label);
            date = (TextView) findViewById(R.id.date);
            time = (TextView) findViewById(R.id.time);
            to = (TextView) findViewById(R.id.to);
            from = (TextView) findViewById(R.id.from);
            fare = (TextView) findViewById(R.id.fare);
            passengers = (TextView) findViewById(R.id.passengers);
            submit = (Button) findViewById(R.id.button_submit);

            if (userBean.getIam().equals("O")) {
                passengersLayout.setVisibility(View.VISIBLE);
                fareLayout.setVisibility(View.VISIBLE);
                submit.setText("Submit");
            } else {
                passengersLayout.setVisibility(View.GONE);
                fareLayout.setVisibility(View.GONE);
                submit.setText("Search Trips");
            }

            date.setText(Singleton.tripDetailsBean.getDate());
            time.setText(Singleton.tripDetailsBean.getTime());
            from.setText(Singleton.tripDetailsBean.getStart());
            to.setText(Singleton.tripDetailsBean.getDrop());
            fare.setText(Html.fromHtml("<b>" + Singleton.tripDetailsBean.getFare() + "</b>" + " (per passenger)"));
            passengers.setText(Html.fromHtml("<b>" + Singleton.tripDetailsBean.getPassengers() + "</b>" + " Passengers"));


            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Log.e(Constants.LOG_TAG, "Submit Trip Request : ");

                    try {
                        loader.show();
                        requestBean.setTripDetailsBean(Singleton.tripDetailsBean);
                        if (userBean.getIam().equals("O")) {
                            new TripSetUpUpTask().execute(null, null, null);
                        }else{
                            new GetTripListTask().execute(null, null, null);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            });

        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "Fucked+++++++++++++++++++++++");
            e.printStackTrace();
        }


    }

    /**
     * Call webservice and check for login
     */
    private class TripSetUpUpTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                responseBean = HttpService.getResponse(API.tripSetUpUrl, requestBean, TripSummaryActivity.this);
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
                    Toast.makeText(TripSummaryActivity.this, ((GenericErrorResponseBean) responseBean.getDataBean()).errorMessage, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), MyTripActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(TripSummaryActivity.this, ((GenericErrorResponseBean) responseBean.getDataBean()).errorMessage, Toast.LENGTH_LONG).show();
            } else if (null != responseBean && responseBean.getStatus() == 0) {
                if (responseBean.getErrorCode().equals(ErrorCodes.CODE_888.toString())) {
                    Toast.makeText(TripSummaryActivity.this, ErrorCodes.CODE_888.getErrorMessage(), Toast.LENGTH_LONG).show();

                    //clear all database
                    dbHelper.clearUsersTable();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(TripSummaryActivity.this, responseBean.getErrorDescription(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(TripSummaryActivity.this, "Unable to process request!", Toast.LENGTH_LONG).show();
                Log.e(Constants.LOG_TAG, this.getClass() + " : " + "ResponseBean " + responseBean);
            }
        }

    }

    /**
     * Call webservice and check for login
     */
    private class GetTripListTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                responseBean = HttpService.getResponse(API.getTripUrl, requestBean, TripSummaryActivity.this);
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
                    Toast.makeText(TripSummaryActivity.this, ((GenericErrorResponseBean) responseBean.getDataBean()).errorMessage, Toast.LENGTH_SHORT).show();

                    Singleton.responseBean = responseBean;

                    Intent intent = new Intent(getApplicationContext(), TripsSearchActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(TripSummaryActivity.this, ((GenericErrorResponseBean) responseBean.getDataBean()).errorMessage, Toast.LENGTH_LONG).show();
            } else if (null != responseBean && responseBean.getStatus() == 0) {
                if (responseBean.getErrorCode().equals(ErrorCodes.CODE_888.toString())) {
                    Toast.makeText(TripSummaryActivity.this, ErrorCodes.CODE_888.getErrorMessage(), Toast.LENGTH_LONG).show();

                    //clear all database
                    dbHelper.clearUsersTable();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(TripSummaryActivity.this, responseBean.getErrorDescription(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(TripSummaryActivity.this, "Unable to process request!", Toast.LENGTH_LONG).show();
                Log.e(Constants.LOG_TAG, this.getClass() + " : " + "ResponseBean " + responseBean);
            }
        }

    }


}
