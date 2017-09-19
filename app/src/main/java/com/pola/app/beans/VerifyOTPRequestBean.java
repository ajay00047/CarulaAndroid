package com.pola.app.beans;

import android.content.Context;

/**
 * Created by Ajay on 05-Sep-17.
 */
public class VerifyOTPRequestBean extends BaseRequestBean {

    public VerifyOTPRequestBean(Context context){
        super(context);
    }

    private String otp;
    private String mobile;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
