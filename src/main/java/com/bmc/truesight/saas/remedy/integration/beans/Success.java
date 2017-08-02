package com.bmc.truesight.saas.remedy.integration.beans;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Success {
    TRUE("true"),
    FALSE("false"),
    PARTIAL("partial");
    private String value;

    private Success(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
