package com.bmc.truesight.saas.remedy.integration.exception;

import com.bmc.truesight.saas.remedy.integration.TemplateValidator;
import com.bmc.truesight.saas.remedy.integration.factory.TemplateValidatorFactory;

/**
 * This Exception is thrown while creating {@link TemplateValidator} in
 * {@link TemplateValidatorFactory}
 *
 * @author vitiwari
 *
 */
public class InitializationException extends Exception {

    private static final long serialVersionUID = -3288803001201992312L;

    public InitializationException() {
        super();
    }

    public InitializationException(String message) {
        super(message);
    }

}
