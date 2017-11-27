package com.pola.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pola.app.R;
import com.pola.app.Utils.API;
import com.pola.app.Utils.Constants;
import com.pola.app.Utils.DBHelper;
import com.pola.app.Utils.ErrorCodes;
import com.pola.app.beans.BaseRequestBean;
import com.pola.app.beans.CarDetailsResponseDataBean;
import com.pola.app.beans.GenericResponseBean;
import com.pola.app.beans.UserBean;
import com.pola.app.services.HttpService;

public class SettingsActivity extends AppCompatActivity {


    private static final String[] aryIam = {"Owner", "Passenger"};
    //beans
    BaseRequestBean requestBean;
    GenericResponseBean responseBean;
    int loadFlag = 0;
    //beans
    private ProgressDialog loader;
    private DBHelper dbHelper;
    private Spinner spinner;
    private LinearLayout nameMain;
    private TextView nameText;
    private UserBean userBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_settings);

            //Set up request bean
            requestBean = new BaseRequestBean(getApplicationContext());

            //setup DBHelper
            dbHelper = new DBHelper(this);

            userBean = dbHelper.getUserDetails();

            //set up xml elements
            nameMain = (LinearLayout) findViewById(R.id.name_main);
            nameText = (TextView) findViewById(R.id.name_text);

            loader = new ProgressDialog(SettingsActivity.this);
            loader.setMessage("Loading data...");
            loader.setIndeterminate(true);
            loader.setCancelable(false);

            nameText.setText(userBean.getFullName());

            spinner = (Spinner) findViewById(R.id.iam);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(SettingsActivity.this,
                    R.layout.snipper_item, aryIam);

            adapter.setDropDownViewResource(android.R.layout.select_dialog_item);

            spinner.setAdapter(adapter);
            //set spinner value
            if (userBean.getIam().equals("O"))
                spinner.setSelection(0);
            else
                spinner.setSelection(1);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    if (loadFlag++ != 0) {
                        if (position == 0) {
                            loader.setMessage("Checking you car details");
                            loader.show();
                            new CarSetUpTask().execute(null, null, null);
                        } else {
                            userBean.setIam("P");
                            dbHelper.clearUsersTable();
                            dbHelper.insertUser(userBean);
                            Toast.makeText(SettingsActivity.this, "Your are Passenger now", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    Toast.makeText(getApplicationContext(), "Nothing selected", Toast.LENGTH_SHORT).show();
                }

            });

            nameMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                    intent.putExtra("fromProfile", "Y");
                    startActivity(intent);
                }
            });


        } catch (Exception e) {
            Log.e(Constants.LOG_TAG, "Fucked+++++++++++++++++++++++");
            e.printStackTrace();
        }


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Call webservice and check for login
     */
    private class CarSetUpTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                responseBean = HttpService.getResponse(API.carDetailsUrl, requestBean, SettingsActivity.this);
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
                    Toast.makeText(SettingsActivity.this, "Car details not found", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), CarSetupActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    userBean.setIam("O");
                    dbHelper.clearUsersTable();
                    dbHelper.insertUser(userBean);
                    Toast.makeText(SettingsActivity.this, "You are Owner now", Toast.LENGTH_SHORT).show();
                }
            } else if (null != responseBean && responseBean.getStatus() == 0) {
                if (responseBean.getErrorCode().equals(ErrorCodes.CODE_888.toString())) {
                    Toast.makeText(SettingsActivity.this, ErrorCodes.CODE_888.getErrorMessage(), Toast.LENGTH_LONG).show();

                    //clear all database
                    dbHelper.clearUsersTable();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(SettingsActivity.this, responseBean.getErrorDescription(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(SettingsActivity.this, "Unable to process request!", Toast.LENGTH_LONG).show();
                Log.e(Constants.LOG_TAG, this.getClass() + " : " + "ResponseBean " + responseBean);
            }
        }

    }
}
