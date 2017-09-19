package com.pola.app.beans;

import android.content.Context;

import com.pola.app.Utils.Constants;
import com.pola.app.Utils.DBHelper;

import java.io.Serializable;

/**
 * Created by Ajay on 05-Sep-17.
 */
public class BaseRequestBean implements Serializable{

    private String passKey;
    private String appPassCode;
    DBHelper dbHelper;

    public BaseRequestBean(Context context){
        dbHelper = new DBHelper(context);
        appPassCode = Constants.APP_PASS_KEY;
        UserBean userBean = dbHelper.getUserDetails();
        if(null != userBean)
            passKey = userBean.getPassKey();
    }

    public String getAppPassCode() {
        return appPassCode;
    }

    public void setAppPassCode(String appPassCode) {
        this.appPassCode = appPassCode;
    }

    public String getPassKey() {
        return passKey;
    }

    public void setPassKey(String passKey) {
        this.passKey = passKey;
    }
}
