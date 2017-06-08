package com.bmc.truesight.saas.remedy.integration;

public enum ARServerForm {

    INCIDENT_FORM("HPD:Help Desk"),
    CHANGE_FORM("CHG:Infrastructure Change");

    private final String text;

    private ARServerForm(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
