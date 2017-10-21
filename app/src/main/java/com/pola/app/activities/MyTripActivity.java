package com.pola.app.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.pola.app.R;
import com.pola.app.Utils.API;
import com.pola.app.Utils.Constants;
import com.pola.app.Utils.DBHelper;
import com.pola.app.Utils.ErrorCodes;
import com.pola.app.Utils.Singleton;
import com.pola.app.adapters.MyTripAdapter;
import com.pola.app.beans.GenericErrorResponseBean;
import com.pola.app.beans.GenericResponseBean;
import com.pola.app.beans.GetTripsResponseBean;
import com.pola.app.beans.MyTripsRequestBean;
import com.pola.app.beans.TripDetailsBean;
import com.pola.app.delegates.CustomItemClickListener;
import com.pola.app.services.HttpService;

import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class MyTripActivity extends AppCompatActivity {

    //beans
    MyTripsRequestBean requestBean;
    GenericResponseBean responseBean;
    private List<TripDetailsBean> lstMyTrips;
    private RecyclerView lstTripsView;
    private RecyclerView.Adapter mAdapter;
    //beans
    private ProgressDialog loader;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_trips);

        //set up xml elements
        lstTripsView = (RecyclerView) findViewById(R.id.my_trips);

        requestBean = new MyTripsRequestBean(getApplicationContext());

        //setup DBHelper
        dbHelper = new DBHelper(this);

        loader = new ProgressDialog(MyTripActivity.this);
        loader.setMessage("Loading data...");
        loader.setIndeterminate(true);
        loader.setCancelable(false);

        try {
            loader.show();
            requestBean.setIam(Singleton.userBean.getIam());
            new MyTripsTask().execute(null, null, null);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void populateTrips(){
        GenericResponseBean beanMain = Singleton.responseBean;
        GetTripsResponseBean tripsBean = (GetTripsResponseBean) beanMain.getDataBean();

        lstMyTrips = tripsBean.getTrips();

        Log.e(Constants.LOG_TAG, "TRIPS COUNT : " + lstMyTrips.size());


        lstTripsView.setLayoutManager(new LinearLayoutManager(this));

        if (null != lstMyTrips && !lstMyTrips.isEmpty()) {
            mAdapter = new MyTripAdapter(lstMyTrips, getApplicationContext(), new CustomItemClickListener() {
                @Override
                public void onItemClick(View v, int position) {

                    if (!lstMyTrips.get(position).getStatus().equals("Cancelled") && Singleton.userBean.getIam().equals("O")) {
                        Intent intent = new Intent(getApplicationContext(), TripRequestsActivity.class);
                        intent.putExtra("tripId",String.valueOf(lstMyTrips.get(position).getTripId()));
                        startActivity(intent);
                        finish();
                    }

                    if (lstMyTrips.get(position).getStatus().equals("Requested") && Singleton.userBean.getIam().equals("P")) {
                        try {
                            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(MyTripActivity.this,
                                        "No Call Permission", Toast.LENGTH_SHORT).show();
                            }
                            String number = "tel:+91" + lstMyTrips.get(position).getMobile();
                            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));
                            startActivity(callIntent);

                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(MyTripActivity.this,
                                    "Call failed, please try again later!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            lstTripsView.setAdapter(mAdapter);
        }
    }


    /**
     * Call webservice and check for login
     */
    private class MyTripsTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                responseBean = HttpService.getResponse(API.myTripUrl, requestBean, MyTripActivity.this);
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
                    Singleton.responseBean = responseBean;
                    populateTrips();
                } else {
                    Toast.makeText(MyTripActivity.this, ((GenericErrorResponseBean) responseBean.getDataBean()).errorMessage, Toast.LENGTH_LONG).show();
                    finish();
                }
            } else if (null != responseBean && responseBean.getStatus() == 0) {
                if (responseBean.getErrorCode().equals(ErrorCodes.CODE_888.toString())) {
                    Toast.makeText(MyTripActivity.this, ErrorCodes.CODE_888.getErrorMessage(), Toast.LENGTH_LONG).show();

                    //clear all database
                    dbHelper.clearUsersTable();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(MyTripActivity.this, responseBean.getErrorDescription(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MyTripActivity.this, "Unable to process request!", Toast.LENGTH_LONG).show();
                Log.e(Constants.LOG_TAG, this.getClass() + " : " + "ResponseBean " + responseBean);
            }
        }

    }

}

