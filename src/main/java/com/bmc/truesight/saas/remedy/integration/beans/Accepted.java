package com.bmc.truesight.saas.remedy.integration.beans;

public class Accepted {

    private int index;
    private String id;

    public Accepted() {

    }

    public Accepted(int index, String id) {
        this.index = index;
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
