package com.bmc.truesight.saas.remedy.integration.exception;

import com.bmc.truesight.saas.remedy.integration.TemplateValidator;

/**
 * This Exception is thrown in case of validation failure by
 * {@link TemplateValidator}
 *
 * @author vitiwari
 *
 */
public class ValidationException extends Exception {

    private static final long serialVersionUID = -5950039647191513352L;

    public ValidationException() {
        super();
    }

    public ValidationException(String message) {
        super(message);
    }

}
