package com.pola.app.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.pola.app.beans.GenericErrorResponseBean;
import com.pola.app.beans.GenericResponseBean;
import com.pola.app.beans.LoginRequestBean;
import com.pola.app.services.HttpService;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    //beans
    LoginRequestBean requestBean;
    GenericResponseBean responseBean;
    //variables
    String mobile = "";
    // UI references
    private EditText xmlMobile;
    private Button xmlSubmit;
    private ProgressDialog loader;
    private DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Set up request bean
        requestBean = new LoginRequestBean(getApplicationContext());
        //setup DBHelper
        dbHelper = new DBHelper(this);

        // Set up the login form.
        xmlMobile = (EditText) findViewById(R.id.text_mobile);
        xmlSubmit = (Button) findViewById(R.id.button_submit);


        //Listeners
        xmlSubmit.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {

                                             loader = new ProgressDialog(LoginActivity.this);
                                             loader.setMessage("Loggin in...");
                                             loader.setIndeterminate(true);
                                             loader.setCancelable(false);

                                             mobile = xmlMobile.getText().toString();

                                             Log.e(Constants.LOG_TAG, this.getClass() + " : " + "Login attempt for " + mobile);

                                             if (!mobile.matches(Constants.REGEX_MOBILE)) {
                                                 Toast.makeText(LoginActivity.this, "Mobile number not valid", Toast.LENGTH_LONG).show();
                                             } else {
                                                 try {
                                                     loader.show();
                                                     requestBean.setMobile(mobile);
                                                     new LoginTask().execute(null, null, null);
                                                 } catch (Exception e) {
                                                     e.printStackTrace();
                                                 }
                                             }


                                         }

                                     }

        );
    }

    public void onBackPressed() {
    }

    /**
     * Call webservice and check for login
     */
    private class LoginTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            try {
                responseBean = HttpService.getResponse(API.loginUrl, requestBean, LoginActivity.this);
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

                if (((GenericErrorResponseBean) responseBean.getDataBean()).getErrorCode().equals(ErrorCodes.CODE_703)) {
                    Toast.makeText(LoginActivity.this, ((GenericErrorResponseBean) responseBean.getDataBean()).errorMessage, Toast.LENGTH_LONG).show();
                    Singleton.userBean.setMobile(mobile);
                    Singleton.isAvialable = true;
                    Intent intent = new Intent(getApplicationContext(), VerifyOTPActivity.class);
                    startActivity(intent);
                    finish();
                } else if (((GenericErrorResponseBean) responseBean.getDataBean()).getErrorCode().equals(ErrorCodes.CODE_701)) {
                    Toast.makeText(LoginActivity.this, ((GenericErrorResponseBean) responseBean.getDataBean()).errorMessage, Toast.LENGTH_LONG).show();
                    Singleton.userBean.setMobile(mobile);
                    Singleton.isAvialable = false;
                    Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(LoginActivity.this, ((GenericErrorResponseBean) responseBean.getDataBean()).errorMessage, Toast.LENGTH_LONG).show();
            } else if (null != responseBean && responseBean.getStatus() == 0) {
                if (responseBean.getErrorCode().equals(ErrorCodes.CODE_888.toString())) {
                    Toast.makeText(LoginActivity.this, ErrorCodes.CODE_888.getErrorMessage(), Toast.LENGTH_LONG).show();

                    //clear all database
                    dbHelper.clearUsersTable();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    Toast.makeText(LoginActivity.this, responseBean.getErrorDescription(), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(LoginActivity.this, "Unable to process request!", Toast.LENGTH_LONG).show();
                Log.e(Constants.LOG_TAG, this.getClass() + " : " + "ResponseBean " + responseBean);
            }
        }

    };
}


