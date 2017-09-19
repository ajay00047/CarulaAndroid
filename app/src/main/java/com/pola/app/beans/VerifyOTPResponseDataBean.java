package com.pola.app.beans;

import com.pola.app.delegates.DataBean;

/**
 * Created by Ajay on 05-Sep-17.
 */
public class VerifyOTPResponseDataBean extends GenericErrorResponseBean implements DataBean {


    private String firstName;
    private String lastName;
    private String mobile;
    private String dpPath;

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
    public String getDpPath() {
        return dpPath;
    }
    public void setDpPath(String dpPath) {
        this.dpPath = dpPath;
    }



}
