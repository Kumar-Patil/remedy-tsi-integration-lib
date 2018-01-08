package com.bmc.truesight.saas.remedy.integration.beans;

import java.util.Map;

/**
 * This is a pojo Class which represents the json configuration template (
 * incidentTemplate and changeTemplate)
 *
 * @author vitiwari
 *
 */
public class Template {

    private Configuration config;
    private TSIEvent eventDefinition;
    private Map<String, FieldItem> fieldDefinitionMap;

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public TSIEvent getEventDefinition() {
        return eventDefinition;
    }

    public void setEventDefinition(TSIEvent eventDefinition) {
        this.eventDefinition = eventDefinition;
    }

    public Map<String, FieldItem> getFieldDefinitionMap() {
        return fieldDefinitionMap;
    }

    public void setFieldDefinitionMap(Map<String, FieldItem> fieldDefinitionMap) {
        this.fieldDefinitionMap = fieldDefinitionMap;
    }

}
