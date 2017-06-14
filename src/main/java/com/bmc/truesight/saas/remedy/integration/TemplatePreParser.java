package com.bmc.truesight.saas.remedy.integration;

import com.bmc.truesight.saas.remedy.integration.beans.Template;
import com.bmc.truesight.saas.remedy.integration.exception.ParsingException;

public interface TemplatePreParser {

    /**
     * This method reads and parse a default JSON configuration file available
     * in the library resources and returns a template with default values. This
     * function should be called to have default configuration values already
     * available, The explicit configuration is passed in
     * {@link TemplateParser}, which overrides these values.
     *
     * @param form {@link ARServerForm} Enum to identify the default Json file
     * @return {@link Template}
     * @throws ParsingException Throws this exception if default JSON parsing is
     * not successful
     */
    Template loadDefaults(ARServerForm form) throws ParsingException;

}
