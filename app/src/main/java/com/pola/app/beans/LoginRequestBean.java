package com.pola.app.beans;

import android.content.Context;

/**
 * Created by Ajay on 05-Sep-17.
 */
public class LoginRequestBean extends BaseRequestBean {

    public LoginRequestBean(Context context){
        super(context);
    }

    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
