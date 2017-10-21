package com.pola.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pola.app.R;
import com.pola.app.Utils.Constants;
import com.pola.app.Utils.DBHelper;
import com.pola.app.Utils.Singleton;

public class HomePageActivity extends AppCompatActivity {

    private static final int TIME_DELAY = 2000;
    private static long back_pressed;
    //beans
    private ProgressDialog loader;
    private DBHelper dbHelper;
    private CardView rideNow, rideLater, myTrips, settings;
    private TextView headerName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home_page);

            //set up xml elements
            rideNow = (CardView) findViewById(R.id.ride_now);
            rideLater = (CardView) findViewById(R.id.ride_later);
            myTrips = (CardView) findViewById(R.id.my_trips);
            settings = (CardView) findViewById(R.id.settings);
            headerName = (TextView) findViewById(R.id.heading_text);

            //setup DBHelper
            dbHelper = new DBHelper(this);

            loader = new ProgressDialog(HomePageActivity.this);
            loader.setMessage("Loading data...");
            loader.setIndeterminate(true);
            loader.setCancelable(false);

            if (Singleton.userBean.getIam().equals("O"))
                headerName.setText("Owner Menu");
            else
                headerName.setText("Passenger Menu");

            //set on click listeners

            rideNow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Singleton.rideNow = true;
                    Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
                    startActivity(intent);
                }
            });

            rideLater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Singleton.rideNow = false;
                    Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
                    startActivity(intent);
                }
            });

            myTrips.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MyTripActivity.class);
                    startActivity(intent);
                }

            });

            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*if (Singleton.userBean.getIam().equals("O"))
                        Singleton.userBean.setIam("P");
                    else
                        Singleton.userBean.setIam("O");
                    dbHelper.clearUsersTable();
                    dbHelper.insertUser(Singleton.userBean);*/
                    Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                    startActivity(intent);
                    finish();
                }
            });


        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "Fucked+++++++++++++++++++++++");
            e.printStackTrace();
        }


    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (back_pressed + TIME_DELAY > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(HomePageActivity.this, "Press once again to exit!",
                    Toast.LENGTH_SHORT).show();
        }
        back_pressed = System.currentTimeMillis();
    }
}
