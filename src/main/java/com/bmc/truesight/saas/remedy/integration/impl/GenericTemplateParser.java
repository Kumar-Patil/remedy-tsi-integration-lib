package com.bmc.truesight.saas.remedy.integration.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmc.truesight.saas.remedy.integration.TemplateParser;
import com.bmc.truesight.saas.remedy.integration.beans.Configuration;
import com.bmc.truesight.saas.remedy.integration.beans.FieldItem;
import com.bmc.truesight.saas.remedy.integration.beans.TSIEvent;
import com.bmc.truesight.saas.remedy.integration.beans.Template;
import com.bmc.truesight.saas.remedy.integration.exception.ParsingException;
import com.bmc.truesight.saas.remedy.integration.util.Constants;
import com.bmc.truesight.saas.remedy.integration.util.StringUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Generic Template Parser
 *
 * @author vitiwari
 *
 */
public class GenericTemplateParser implements TemplateParser {

    private static final Logger log = LoggerFactory.getLogger(GenericTemplateParser.class);

    @Override
    public Template readParseConfigJson(Template defaultTemplate, String configJson) throws ParsingException {
        return parse(defaultTemplate, configJson);
    }

    /**
     * Used to parse the template in case of template available as json file
     *
     * @param fileName Name of the template json file
     * @throws ParsingException throws exception in case of unsuccessful parsing
     */
    @Override
    public Template readParseConfigFile(Template defaultTemplate, String fileName) throws ParsingException {
        // Read the file in String
        String configJson = null;
        try {
            configJson = FileUtils.readFileToString(new File(fileName), "UTF8");
        } catch (IOException e) {
            log.debug("Template file {} not available, name should be incidentTemplate.json/changeTemplate.json & its location should be same as jar file", fileName);
            throw new ParsingException(StringUtil.format(Constants.CONFIG_FILE_NOT_VALID, new Object[]{fileName}));
        }
        return parse(defaultTemplate, configJson);
    }

    private Template parse(Template defaultTemplate, String configJson) throws ParsingException {
        //Template template = new Template();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(configJson);
        } catch (IOException e) {
            throw new ParsingException(StringUtil.format(Constants.CONFIG_FILE_NOT_VALID, new Object[]{configJson, e.getMessage()}));
        }

        // Read the config details and map to pojo
        String configString;
        JsonNode configuration = rootNode.get("config");
        Configuration config = null;
        if (configuration != null) {
            try {
                configString = mapper.writeValueAsString(configuration);
                config = mapper.readValue(configString, Configuration.class);
            } catch (IOException e) {
                throw new ParsingException(StringUtil.format(Constants.CONFIG_PROPERTY_NOT_VALID, new Object[]{e.getMessage()}));
            }
            Configuration defaultConfig = defaultTemplate.getConfig();
            updateConfig(defaultConfig, config);
            //defaultTemplate
        } else {
            log.info("config field is not found, falling back to default values while parsing");
        }

        // Read the payload details and map to pojo
        JsonNode payloadNode = rootNode.get("eventDefinition");
        TSIEvent event = null;
        if (payloadNode != null) {
            try {
                String payloadString = mapper.writeValueAsString(payloadNode);
                event = mapper.readValue(payloadString, TSIEvent.class);
            } catch (IOException e) {
                throw new ParsingException(StringUtil.format(Constants.PAYLOAD_PROPERTY_NOT_FOUND, new Object[]{e.getMessage()}));
            }
            TSIEvent defaultEvent = defaultTemplate.getEventDefinition();
            updateEventDefinition(defaultEvent, event);
            //defaultTemplate
        } else {
            log.info("eventDefinition field not found, falling back to default values while parsing");
        }

        // Mapping of fieldDefinitionMap
        Map<String, FieldItem> defaultFieldDefinitionMap = defaultTemplate.getFieldDefinitionMap();
        try {
            JsonNode fieldDefinitionNode = rootNode.get(Constants.FIELDDEFINITIONMAP_NODE_NAME);
            String fieldDefinitionString = mapper.writeValueAsString(fieldDefinitionNode);
            TypeReference<HashMap<String, FieldItem>> typeRef = new TypeReference<HashMap<String, FieldItem>>() {
            };
            Map<String, FieldItem> fieldDefinitionMap = mapper.readValue(fieldDefinitionString, typeRef);
            if (fieldDefinitionMap != null) {
                defaultFieldDefinitionMap.putAll(fieldDefinitionMap);
            }
            // defaultTemplate.setFieldDefinitionMap(defaultFieldDefinitionMap);
        } catch (IOException e) {
            throw new ParsingException(StringUtil.format(Constants.PAYLOAD_PROPERTY_NOT_FOUND, new Object[]{}));
        }

        return defaultTemplate;
    }

    private void updateConfig(Configuration defaultConfig, Configuration config) {
        if (config.getRemedyHostName() != null && !config.getRemedyHostName().equals("")) {
            defaultConfig.setRemedyHostName(config.getRemedyHostName());
        }
        if (config.getRemedyPassword() != null && !config.getRemedyPassword().equals("")) {
            defaultConfig.setRemedyPassword(config.getRemedyPassword());
        }
        if (config.getRemedyPort() != null) {
            defaultConfig.setRemedyPort(config.getRemedyPort());
        }

        if (config.getRemedyUserName() != null && !config.getRemedyUserName().equals("")) {
            defaultConfig.setRemedyUserName(config.getRemedyUserName());
        }
        if (config.getTsiEventEndpoint() != null && !config.getTsiEventEndpoint().equals("")) {
            defaultConfig.setTsiEventEndpoint(config.getTsiEventEndpoint());
        }
        if (config.getTsiApiToken() != null && !config.getTsiApiToken().equals("")) {
            defaultConfig.setTsiApiToken(config.getTsiApiToken());
        }
        if (config.getStartDateTime() != null) {
            defaultConfig.setStartDateTime(config.getStartDateTime());
        }
        if (config.getEndDateTime() != null) {
            defaultConfig.setEndDateTime(config.getEndDateTime());
        }
        //ability to override from the user 
        if (config.getChunkSize() != null) {
            defaultConfig.setChunkSize(config.getChunkSize());
        }
        if (config.getRetryChunkSize() != null) {
            defaultConfig.setRetryChunkSize(config.getRetryChunkSize());
        }
        if (config.getThreadCount() != null) {
            defaultConfig.setThreadCount(config.getThreadCount());
        }
        if (config.getRetryConfig() != null) {
            defaultConfig.setRetryConfig(config.getRetryConfig());
        }
        if (config.getWaitMsBeforeRetry() != null) {
            defaultConfig.setWaitMsBeforeRetry(config.getRetryConfig());
        }
        if (config.getConditionFields() != null && config.getConditionFields().size() > 0) {
            defaultConfig.setConditionFields(config.getConditionFields());
        }
        if (config.getQueryStatusList() != null && config.getQueryStatusList().size() > 0) {
            defaultConfig.setQueryStatusList(config.getQueryStatusList());
        }

    }

    private void updateEventDefinition(TSIEvent defaultEvent, TSIEvent event) {
        if (event.getTitle() != null && !event.getTitle().equals("")) {
            defaultEvent.setTitle(event.getTitle());
        }
        if (event.getStatus() != null && !event.getStatus().equals("")) {
            defaultEvent.setStatus(event.getStatus());
        }
        if (event.getSeverity() != null && !event.getSeverity().equals("")) {
            defaultEvent.setSeverity(event.getSeverity());
        }
        if (event.getFingerprintFields() != null && event.getFingerprintFields().size() > 0) {
            defaultEvent.setFingerprintFields(event.getFingerprintFields());
        }
        if (event.getEventClass() != null && !event.getEventClass().equals("")) {
            defaultEvent.setEventClass(event.getEventClass());
        }
        if (event.getCreatedAt() != null && !event.getCreatedAt().equals("")) {
            defaultEvent.setCreatedAt(event.getCreatedAt());
        }
        if (event.getMessage() != null && !event.getMessage().equals("")) {
            defaultEvent.setMessage(event.getMessage());
        }

        //Overrides the default properties completely
        if (event.getProperties() != null && event.getProperties().size() > 0) {
            Map<String, String> defPropertyMap = defaultEvent.getProperties();
            if (event.getProperties().size() == 1 && event.getProperties().containsKey(Constants.PROPERTY_APP_ID)) {
                //it means there is no property mapping only app_id is mapped
                //So only App_id is overridden and not other properties
                defPropertyMap.put(Constants.PROPERTY_APP_ID, event.getProperties().get(Constants.PROPERTY_APP_ID));
            } else {
                defPropertyMap.clear();
                defPropertyMap.putAll(event.getProperties());
            }
        }
        if (event.getSource() != null) {
            defaultEvent.setSource(event.getSource());
        }
        /* if (event.getSender() != null) {
            defaultEvent.setSender(event.getSender());
        }*/
    }
}
