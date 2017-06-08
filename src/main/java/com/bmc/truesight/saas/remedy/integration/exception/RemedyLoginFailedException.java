package com.bmc.truesight.saas.remedy.integration.exception;

import com.bmc.truesight.saas.remedy.integration.RemedyReader;

/**
 * This exception is thrown when login attempt fails in {@link RemedyReader}
 *
 * @author vitiwari
 *
 */
public class RemedyLoginFailedException extends Exception {

    private static final long serialVersionUID = -4739634227509447336L;

    public RemedyLoginFailedException() {
        super();
    }

    public RemedyLoginFailedException(String message) {
        super(message);
    }

}
