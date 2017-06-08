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
    private Event eventDefinition;
    private Map<String, FieldItem> FieldItemMap;

    public Configuration getConfig() {
        return config;
    }

    public void setConfig(Configuration config) {
        this.config = config;
    }

    public Event getEventDefinition() {
        return eventDefinition;
    }

    public void setEventDefinition(Event eventDefinition) {
        this.eventDefinition = eventDefinition;
    }

    public Map<String, FieldItem> getFieldItemMap() {
        return FieldItemMap;
    }

    public void setFieldItemMap(Map<String, FieldItem> fieldItemMap) {
        FieldItemMap = fieldItemMap;
    }
}
