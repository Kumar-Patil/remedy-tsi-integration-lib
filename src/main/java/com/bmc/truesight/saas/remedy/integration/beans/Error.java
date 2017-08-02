package com.bmc.truesight.saas.remedy.integration.beans;

public class Error {

    private int index;
    private String message;

    public Error() {

    }

    public Error(int index, String message) {
        this.index = index;
        this.message = message;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
