package com.pola.app.beans;

/**
 * Created by Ajay on 04-Sep-17.
 */
public class UserBean {
    private int userId;
    private String firstName;
    private String lastName;
    private String mobile;
    private String dpPath;
    private String passKey;
    private String iam;

    public String getIam() {
        return iam;
    }

    public void setIam(String iam) {
        this.iam = iam;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

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

    public String getPassKey() {
        return passKey;
    }

    public void setPassKey(String passKey) {
        this.passKey = passKey;
    }

    public String getFullName(){
        return firstName+" "+lastName;
    }
}
