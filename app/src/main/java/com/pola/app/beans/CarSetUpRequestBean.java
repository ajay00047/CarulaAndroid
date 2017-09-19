package com.pola.app.beans;

import android.content.Context;

/**
 * Created by Ajay on 08-Sep-17.
 */
public class CarSetUpRequestBean extends BaseRequestBean {

    public CarSetUpRequestBean(Context context){
        super(context);
    }

    private int carId;
    private String company;
    private String model;
    private String color;
    private String no;

    public int getCarId() {
        return carId;
    }

    public void setCarId(int carId) {
        this.carId = carId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }
}
