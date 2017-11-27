package com.pola.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pola.app.R;
import com.pola.app.Utils.API;
import com.pola.app.Utils.Constants;
import com.pola.app.Utils.DBHelper;
import com.pola.app.Utils.ErrorCodes;
import com.pola.app.Utils.Singleton;
import com.pola.app.beans.CarSetUpRequestBean;
import com.pola.app.beans.GenericErrorResponseBean;
import com.pola.app.beans.GenericResponseBean;
import com.pola.app.beans.UserBean;
import com.pola.app.services.HttpService;

/**
 * A login screen that offers login via email/password.
 */
public class CarSetupActivity extends AppCompatActivity {

    //beans
    CarSetUpRequestBean requestBean;
    GenericResponseBean responseBean;
    //variables
    String company = "";
    String model = "";
    String color = "";
    String no = "";
    // UI references
    private EditText xmlCompany;
    private EditText xmlModel;
    private EditText xmlColor;
    private EditText xmlNo;
    private Button xmlNext;
    private ProgressDialog loader;
    private UserBean userBean;
    //DB Helper
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_setup);
        try {
            //Set up request bean
            requestBean = new CarSetUpRequestBean(getApplicationContext());

            //DB helper
            dbHelper = new DBHelper(this);

            userBean = dbHelper.getUserDetails();

            // Set up the login form.
            xmlCompany = (EditText) findViewById(R.id.car_company);
            xmlModel = (EditText) findViewById(R.id.car_model);
            xmlColor = (EditText) findViewById(R.id.car_color);
            xmlNo = (EditText) findViewById(R.id.car_no);
            xmlNext = (Button) findViewById(R.id.button_next);

            xmlCompany.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            xmlModel.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            xmlColor.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            xmlNo.setFilters(new InputFilter[]{new InputFilter.AllCaps()});

            //Listeners
            xmlNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    loader = new ProgressDialog(CarSetupActivity.this);
                    loader.setMessage("Submitting data...");
                    loader.setIndeterminate(true);
                    loader.setCancelable(false);

                    company = xmlCompany.getText().toString();
                    model = xmlModel.getText().toString();
                    color = xmlColor.getText().toString();
                    no = xmlNo.getText().toString();

                    if (!company.matches(Constants.REGEX_ALPHA)) {
                        Toast.makeText(CarSetupActivity.this, "Only characters are allowed [A-Z] in Company field", Toast.LENGTH_LONG).show();
                    } else if (!model.matches(Constants.REGEX_ALPHANO)) {
                        Toast.makeText(CarSetupActivity.this, "Only characters/numbers are allowed [A-Z] and [0-9] in Model field", Toast.LENGTH_LONG).show();
                    } else if (!color.matches(Constants.REGEX_ALPHA)) {
                        Toast.makeText(CarSetupActivity.this, "Only characters are allowed [A-Z] in Color field", Toast.LENGTH_LONG).show();
                    } else if (!no.matches(Constants.REGEX_ALPHANO)) {
                        Toast.makeText(CarSetupActivity.this, "Only characters/numbers are allowed [A-Z] and [0-9] in No filed", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            loader.show();
                            requestBean.setCompany(company);
                            requestBean.setModel(model);
                            requestBean.setColor(color);
                            requestBean.setNo(no);

                            new CarSetUpTask().execute(null, null, null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Call webservice and check for login
     */
    private class CarSetUpTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                responseBean = HttpService.getResponse(API.carSetUpUrl, requestBean, CarSetupActivity.this);
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
                    userBean.setIam("O");
                    dbHelper.clearUsersTable();
                    dbHelper.insertUser(userBean);
                    Toast.makeText(CarSetupActivity.this, "You are Owner now", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(CarSetupActivity.this, ((GenericErrorResponseBean) responseBean.getDataBean()).errorMessage, Toast.LENGTH_LONG).show();
            } else if (null != responseBean && responseBean.getStatus() == 0) {
                if (responseBean.getErrorCode().equals(ErrorCodes.CODE_888.toString())) {
                    Toast.makeText(CarSetupActivity.this, ErrorCodes.CODE_888.getErrorMessage(), Toast.LENGTH_LONG).show();

                    //clear all database
                    dbHelper.clearUsersTable();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(CarSetupActivity.this, responseBean.getErrorDescription(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(CarSetupActivity.this, "Unable to process request!", Toast.LENGTH_LONG).show();
                Log.e(Constants.LOG_TAG, this.getClass() + " : " + "ResponseBean " + responseBean);
            }
        }

    }

    ;

}

