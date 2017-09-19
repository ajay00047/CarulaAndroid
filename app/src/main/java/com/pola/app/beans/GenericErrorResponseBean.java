package com.pola.app.beans;

import com.pola.app.Utils.ErrorCodes;
import com.pola.app.delegates.DataBean;

/**
 * Created by Ajay on 05-Sep-17.
 */
public class GenericErrorResponseBean implements DataBean{
    public ErrorCodes errorCode;
    public String errorMessage;
    private String passKey;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPassKey() {
        return passKey;
    }

    public void setPassKey(String passKey) {
        this.passKey = passKey;
    }

    public ErrorCodes getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCodes errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getErrorMessage();
    }
}
