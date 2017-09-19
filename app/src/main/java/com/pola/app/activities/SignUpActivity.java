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
import com.pola.app.beans.SignUpRequestBean;
import com.pola.app.services.HttpService;

/**
 * A login screen that offers login via email/password.
 */
public class SignUpActivity extends AppCompatActivity {

    // UI references
    private EditText xmlFirstName;
    private EditText xmlLastName;
    private EditText xmlMobile;
    private Button xmlNext;
    private ProgressDialog loader;
    AsyncTask<Void, Void, Void> signUpTask;

    //beans
    SignUpRequestBean requestBean;
    GenericResponseBean responseBean;

    private DBHelper dbHelper;

    //variables
    String firstName = "";
    String lastName = "";
    String mobile = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        try{
        //Set up request bean
        requestBean = new SignUpRequestBean(getApplicationContext());
            //setup DBHelper
            dbHelper = new DBHelper(this);

        // Set up the login form.
        xmlFirstName = (EditText) findViewById(R.id.text_firstName);
        xmlLastName = (EditText) findViewById(R.id.text_lastName);
        xmlMobile = (EditText) findViewById(R.id.text_mobile);
        xmlNext = (Button) findViewById(R.id.button_next);

        //set up view
        xmlMobile.setText(Singleton.userBean.getMobile());

        //Listeners
        xmlNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loader = new ProgressDialog(SignUpActivity.this);
                loader.setMessage("Submitting data...");
                loader.setIndeterminate(true);
                loader.setCancelable(false);

                firstName = xmlFirstName.getText().toString();
                lastName = xmlLastName.getText().toString();
                mobile = xmlMobile.getText().toString();

                Log.e(Constants.LOG_TAG, this.getClass() + " : " + "Sign Up attempt for " + mobile);

                if (!firstName.matches(Constants.REGEX_ALPHA)) {
                    Toast.makeText(SignUpActivity.this, "Only characters are allowed [A-Z]", Toast.LENGTH_LONG).show();
                } else if (!lastName.matches(Constants.REGEX_ALPHA)) {
                    Toast.makeText(SignUpActivity.this, "Only characters are allowed [A-Z]", Toast.LENGTH_LONG).show();
                } else if (!mobile.matches(Constants.REGEX_MOBILE)) {
                    Toast.makeText(SignUpActivity.this, "Mobile number not valid", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        loader.show();
                        requestBean.setMobile(mobile);
                        requestBean.setFirstName(firstName);
                        requestBean.setLastName(lastName);
                        signUpTask.execute(null, null, null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });


    /**
     * Call webservice and check for login
     */
    signUpTask = new AsyncTask<Void, Void, Void>() {

        @Override
        protected Void doInBackground(Void... params) {

            try {
                responseBean = HttpService.getResponse(API.signUpUrl,requestBean,SignUpActivity.this);
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

               Log.e(Constants.LOG_TAG, this.getClass() + " : "+"Error Code : "+((GenericErrorResponseBean) responseBean.getDataBean()).getErrorCode());

               if (((GenericErrorResponseBean) responseBean.getDataBean()).getErrorCode().equals(ErrorCodes.CODE_702)) {
                   Toast.makeText(SignUpActivity.this, ((GenericErrorResponseBean) responseBean.getDataBean()).errorMessage, Toast.LENGTH_LONG).show();
                   Singleton.userBean.setMobile(mobile);
                   Singleton.isAvialable = true;
                   Intent intent = new Intent(getApplicationContext(), VerifyOTPActivity.class);
                   startActivity(intent);
                   finish();
               } 
               else
                   Toast.makeText(SignUpActivity.this, ((GenericErrorResponseBean) responseBean.getDataBean()).errorMessage, Toast.LENGTH_LONG).show();
           }else if(null != responseBean && responseBean.getStatus() ==0) {
               if (responseBean.getErrorCode().equals(ErrorCodes.CODE_888.toString())) {
                   Toast.makeText(SignUpActivity.this,ErrorCodes.CODE_888.getErrorMessage(), Toast.LENGTH_LONG).show();

                   //clear all database
                   dbHelper.clearUsersTable();

                   Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                   startActivity(intent);
                   finish();
               }else
                   Toast.makeText(SignUpActivity.this, responseBean.getErrorDescription(), Toast.LENGTH_LONG).show();
           }else{
               Toast.makeText(SignUpActivity.this, "Unable to process request!", Toast.LENGTH_LONG).show();
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

