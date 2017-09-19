package com.pola.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.pola.app.R;
import com.pola.app.Utils.API;
import com.pola.app.Utils.Constants;
import com.pola.app.Utils.DBHelper;
import com.pola.app.Utils.ErrorCodes;
import com.pola.app.Utils.Singleton;
import com.pola.app.beans.BaseRequestBean;
import com.pola.app.beans.CarDetailsResponseDataBean;
import com.pola.app.beans.GenericResponseBean;
import com.pola.app.services.HttpService;

public class LandingPageActivity extends AppCompatActivity {

    //beans
    BaseRequestBean requestBean;
    GenericResponseBean responseBean;
    private LinearLayout owner, passenger;
    private DBHelper dbHelper;
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        //Set up request bean
        requestBean = new BaseRequestBean(getApplicationContext());

        //setup DBHelper
        dbHelper = new DBHelper(this);

        owner = (LinearLayout) findViewById(R.id.owner);
        passenger = (LinearLayout) findViewById(R.id.passenger);

        owner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loader = new ProgressDialog(LandingPageActivity.this);
                loader.setMessage("Hang on...");
                loader.setIndeterminate(true);
                loader.setCancelable(false);
                try {
                    loader.show();
                    new CarSetUpTask().execute(null, null, null);
                    Singleton.isOwner = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });

        passenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Singleton.isOwner = false;
                Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
                startActivity(intent);

            }
        });



    }

    /**
     * Call webservice and check for login
     */
    private class CarSetUpTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                responseBean = HttpService.getResponse(API.carDetailsUrl, requestBean, LandingPageActivity.this);
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

                Log.e(Constants.LOG_TAG, this.getClass() + " : " + "Error Code : " + ((CarDetailsResponseDataBean) responseBean.getDataBean()).getErrorCode());

                if (((CarDetailsResponseDataBean) responseBean.getDataBean()).getErrorCode().equals(ErrorCodes.CODE_202)) {

                    Intent intent = new Intent(getApplicationContext(), CarSetupActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
                    startActivity(intent);
                }
            } else if (null != responseBean && responseBean.getStatus() == 0) {
                if (responseBean.getErrorCode().equals(ErrorCodes.CODE_888.toString())) {
                    Toast.makeText(LandingPageActivity.this, ErrorCodes.CODE_888.getErrorMessage(), Toast.LENGTH_LONG).show();

                    //clear all database
                    dbHelper.clearUsersTable();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(LandingPageActivity.this, responseBean.getErrorDescription(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LandingPageActivity.this, "Unable to process request!", Toast.LENGTH_LONG).show();
                Log.e(Constants.LOG_TAG, this.getClass() + " : " + "ResponseBean " + responseBean);
            }
        }

    };



}
