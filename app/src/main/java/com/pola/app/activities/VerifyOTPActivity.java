package com.pola.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pola.app.R;
import com.pola.app.Utils.API;
import com.pola.app.Utils.Constants;
import com.pola.app.Utils.DBHelper;
import com.pola.app.Utils.ErrorCodes;
import com.pola.app.Utils.Singleton;
import com.pola.app.beans.GenericErrorResponseBean;
import com.pola.app.beans.GenericResponseBean;
import com.pola.app.beans.VerifyOTPRequestBean;
import com.pola.app.beans.VerifyOTPResponseDataBean;
import com.pola.app.services.HttpService;

/**
 * A login screen that offers login via email/password.
 */
public class VerifyOTPActivity extends AppCompatActivity {

    // UI references
    private EditText xmlOTP;
    private Button xmlSubmit;
    private Button xmlResendOTP;
    private ProgressDialog loader;
    AsyncTask<Void, Void, Void> task;

    //beans
    VerifyOTPRequestBean requestBean;
    GenericResponseBean responseBean;
    private DBHelper dbHelper;

    //variables
    String otp = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        try{
        //Set up request bean
        requestBean = new VerifyOTPRequestBean(getApplicationContext());

        // Set up the login form.
        xmlOTP = (EditText) findViewById(R.id.otp);
        xmlSubmit = (Button) findViewById(R.id.button_submit_otp);
        xmlResendOTP = (Button) findViewById(R.id.button_resubmit_otp);

        //setup DBHelper
        dbHelper = new DBHelper(this);

        //Listeners
            xmlSubmit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                loader = new ProgressDialog(VerifyOTPActivity.this);
                loader.setMessage("verifying otp...");
                loader.setIndeterminate(true);
                loader.setCancelable(false);

                otp = xmlOTP.getText().toString();
                if(!otp.matches(Constants.REGEX_OTP)) {
                    Toast.makeText(VerifyOTPActivity.this, "Wrong OTP!", Toast.LENGTH_LONG).show();
                }else {
                    try{
                        loader.show();
                        requestBean.setOtp(otp);
                        requestBean.setMobile(Singleton.userBean.getMobile());
                        task.execute(null, null, null);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

            }
        });


    /**
     * Call webservice and check for login
     */
    task = new AsyncTask<Void, Void, Void>() {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                responseBean = HttpService.getResponse(API.verifyOTPUrl,requestBean,VerifyOTPActivity.this);
                loader.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
                loader.dismiss();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
           if(null != responseBean && responseBean.getStatus() ==1) {
               if (((VerifyOTPResponseDataBean) responseBean.getDataBean()).getErrorCode().equals(ErrorCodes.CODE_704)) {
                   Toast.makeText(VerifyOTPActivity.this, ((VerifyOTPResponseDataBean) responseBean.getDataBean()).errorMessage, Toast.LENGTH_LONG).show();

                    dbHelper.insertUser((VerifyOTPResponseDataBean) responseBean.getDataBean());

                   Intent intent = new Intent(getApplicationContext(), LandingPageActivity.class);
                   startActivity(intent);
                   finish();
               } else {
                   Toast.makeText(VerifyOTPActivity.this, ((VerifyOTPResponseDataBean) responseBean.getDataBean()).errorMessage, Toast.LENGTH_LONG).show();
               }
           }else if(null != responseBean && responseBean.getStatus() ==0) {
               if (responseBean.getErrorCode().equals(ErrorCodes.CODE_888.toString())) {
                   Toast.makeText(VerifyOTPActivity.this,ErrorCodes.CODE_888.getErrorMessage(), Toast.LENGTH_LONG).show();

                   //clear all database
                   dbHelper.clearUsersTable();

                   Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                   startActivity(intent);
                   finish();
               }else
                   Toast.makeText(VerifyOTPActivity.this, responseBean.getErrorDescription(), Toast.LENGTH_LONG).show();
           }else{
               Toast.makeText(VerifyOTPActivity.this, "Unable to process request!", Toast.LENGTH_LONG).show();
               Log.e(Constants.LOG_TAG, this.getClass() + " : " + "ResponseBean " + responseBean);
           }
        }

    };

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public void onBackPressed() {

    }
}

