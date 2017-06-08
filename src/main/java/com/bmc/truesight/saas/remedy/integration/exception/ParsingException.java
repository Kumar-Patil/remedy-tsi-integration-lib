package com.bmc.truesight.saas.remedy.integration.exception;

import com.bmc.truesight.saas.remedy.integration.TemplateParser;

/**
 * This exception is thrown when there is some issue in parsing the Json
 * template by {@link TemplateParser}
 *
 * @author vitiwari
 *
 */
public class ParsingException extends Exception {

    private static final long serialVersionUID = -9153666788323684249L;

    public ParsingException() {
        super();
    }

    public ParsingException(String message) {
        super(message);
    }

}
