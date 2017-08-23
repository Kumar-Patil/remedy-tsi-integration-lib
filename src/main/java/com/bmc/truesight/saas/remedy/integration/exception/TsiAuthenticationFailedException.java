package com.bmc.truesight.saas.remedy.integration.exception;

public class TsiAuthenticationFailedException extends Exception {

    private static final long serialVersionUID = 8898745928831461930L;

    public TsiAuthenticationFailedException() {
        super();
    }

    public TsiAuthenticationFailedException(String message) {
        super(message);
    }

}
