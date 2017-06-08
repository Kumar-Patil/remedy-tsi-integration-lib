package com.bmc.truesight.saas.remedy.integration;

import com.bmc.truesight.saas.remedy.integration.beans.Template;
import com.bmc.truesight.saas.remedy.integration.exception.ValidationException;

/**
 * This interface defines the Validation of the incidentTemplate or
 * ChangeTemplate Json.
 *
 * @author vitiwari
 *
 */
public interface TemplateValidator {

    /**
     * This method validates the template.
     *
     * @param template Instance of {@link Template} recieved from
     * {@link TemplateParser}
     * @return true If validation is successful true is returned.
     * @throws ValidationException In case of unsuccessful validation exception
     * is thrown.
     */
    boolean validate(Template template) throws ValidationException;
}
