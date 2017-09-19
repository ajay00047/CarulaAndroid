package com.pola.app.beans;

import android.content.Context;

/**
 * Created by Ajay on 08-Sep-17.
 */
public class SignUpRequestBean extends BaseRequestBean {

    public SignUpRequestBean(Context context){
        super(context);
    }

    private String firstName;
    private String lastName;
    private String mobile;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
