package com.pl.reunite;


import android.content.Context;

/**
 * Created on 6/3/2016.
 */
public class ErrorMessage {
    private Context c;
    private String errorCode;
    private String[][] errorCollection;

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    private String errorMessage;

    ErrorMessage(Context c){
        this.c = c;
        errorCode = "";
        errorMessage = "";
        errorCollection = new String[][]{
                {"1", c.getResources().getString(R.string.plusError1)},
                {"2", c.getResources().getString(R.string.plusError2)},
                {"6", c.getResources().getString(R.string.plusError6)},
                {"7", c.getResources().getString(R.string.plusError7)},
                {"8", c.getResources().getString(R.string.plusError8)},
                {"9", c.getResources().getString(R.string.plusError9)},
                {"11", c.getResources().getString(R.string.plusError11)},
                {"14", c.getResources().getString(R.string.plusError14)},
                {"15", c.getResources().getString(R.string.plusError15)},
                {"16", c.getResources().getString(R.string.plusError16)},
                {"20", c.getResources().getString(R.string.plusError20)},
                {"21", c.getResources().getString(R.string.plusError21)},
                {"22", c.getResources().getString(R.string.plusError22)},
                {"23", c.getResources().getString(R.string.plusError23)},
                {"100", c.getResources().getString(R.string.plusError100)},
                {"600", c.getResources().getString(R.string.plusError600)},
                {"601", c.getResources().getString(R.string.plusError601)},
                {"602", c.getResources().getString(R.string.plusError602)},
                {"9999", c.getResources().getString(R.string.plusError9999)}
        };
    }

    public String searchErrorMessage(String errorCode) {
        if (errorCode.matches("[0-9]+") == false) {
            return c.getResources().getString(R.string.plusError9999);
        }
        this.errorCode = errorCode;
        if (this.errorCode.isEmpty()){
            errorMessage = c.getResources().getString(R.string.input_empty_error_code);
            return errorMessage;
        }
        errorMessage = "";
        for (int i = 0; i < errorCollection.length; i++){
            if (errorCode.compareToIgnoreCase(errorCollection[i][0]) == 0){
                errorMessage = errorCollection[i][1];
                break;
            }
        }
        if (errorMessage.isEmpty()){
            return c.getResources().getString(R.string.plusError9999);
        }
        return errorMessage;
    }
}
