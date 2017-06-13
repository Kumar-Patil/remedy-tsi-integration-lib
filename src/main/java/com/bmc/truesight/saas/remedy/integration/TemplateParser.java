package com.bmc.truesight.saas.remedy.integration;

import com.bmc.truesight.saas.remedy.integration.beans.Template;
import com.bmc.truesight.saas.remedy.integration.exception.ParsingException;

/**
 * This interface defines the parsing of the incidentTemplate or ChangeTemplate
 * Json.
 *
 * @author vitiwari
 *
 */
public interface TemplateParser {

    /**
     * This method reads and parse from a JSON String. This function is used in
     * case template JSON is available in String
     *
     * @param configJson Template JSON String
     * @return {@link Template}
     * @throws ParsingException Throws this exception if JSON parsing is not
     * successful
     */
    Template readParseConfigJson(Template defaultTemplate,String configJson) throws ParsingException;

    /**
     * This method reads and parse from a JSON file. This function is used in
     * case template JSON is available in json file
     *
     * @param fileName Template JSON fileName
     * @return {@link Template}
     * @throws ParsingException Throws this exception if JSON parsing is not
     * successful
     */
    Template readParseConfigFile(Template defaultTemplate,String fileName) throws ParsingException;
}
