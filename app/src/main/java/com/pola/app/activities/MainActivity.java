package com.pola.app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.pola.app.R;
import com.pola.app.Utils.Constants;
import com.pola.app.Utils.DBHelper;
import com.pola.app.Utils.StringUtil;
import com.pola.app.beans.UserBean;

public class MainActivity extends AppCompatActivity {

    private LinearLayout signUp,logIn;
    private DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create elements

        //setup DBHelper
        dbHelper = new DBHelper(this);

        //required beans
        UserBean bean;

        //check for valid user
        if ((bean = dbHelper.getUserDetails()) != null) {
            Log.e(Constants.LOG_TAG, this.getClass() + " : users table has user entry for : " + bean.getFullName());
            Intent intent = new Intent(getApplicationContext(), LandingPageActivity.class);
            startActivity(intent);
            finish();
        }else{
            Log.e(Constants.LOG_TAG, this.getClass() + " : users table cleared ");
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }


  }


}
