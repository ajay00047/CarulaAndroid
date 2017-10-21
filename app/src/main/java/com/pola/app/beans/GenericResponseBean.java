package com.pola.app.beans;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.pola.app.delegates.DataBean;

import java.io.Serializable;

/**
 * Created by Ajay on 05-Sep-17.
 */
public class GenericResponseBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private int status;
    private String errorCode;
    private String errorDescription;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.PROPERTY,
            property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = GenericErrorResponseBean.class, name = "login"),
            @JsonSubTypes.Type(value = VerifyOTPResponseDataBean.class, name = "verifyOTP"),
            @JsonSubTypes.Type(value = GenericErrorResponseBean.class, name = "signUp"),
            @JsonSubTypes.Type(value = GenericErrorResponseBean.class, name = "carSetUp"),
            @JsonSubTypes.Type(value = GenericErrorResponseBean.class, name = "tripSetUp"),
            @JsonSubTypes.Type(value = GenericErrorResponseBean.class, name = "requestTrip"),
            @JsonSubTypes.Type(value = GenericErrorResponseBean.class, name = "changeTripStatus"),
            @JsonSubTypes.Type(value = GetTripsResponseBean.class, name = "tripRequests"),
            @JsonSubTypes.Type(value = GetTripsResponseBean.class, name = "getTrip"),
            @JsonSubTypes.Type(value = GetTripsResponseBean.class, name = "myTrip"),
            @JsonSubTypes.Type(value = CarDetailsResponseDataBean.class, name = "carDetails")})
    private DataBean dataBean;

    public DataBean getDataBean() {
        return dataBean;
    }

    public void setDataBean(DataBean dataBean) {
        this.dataBean = dataBean;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int success) {
        this.status = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

}
