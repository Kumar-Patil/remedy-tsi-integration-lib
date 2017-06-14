package com.bmc.truesight.saas.remedy.integration.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.bmc.truesight.saas.remedy.integration.ARServerForm;
import com.bmc.truesight.saas.remedy.integration.TemplatePreParser;
import com.bmc.truesight.saas.remedy.integration.beans.Configuration;
import com.bmc.truesight.saas.remedy.integration.beans.FieldItem;
import com.bmc.truesight.saas.remedy.integration.beans.TSIEvent;
import com.bmc.truesight.saas.remedy.integration.beans.Template;
import com.bmc.truesight.saas.remedy.integration.exception.ParsingException;
import com.bmc.truesight.saas.remedy.integration.util.Message;
import com.bmc.truesight.saas.remedy.integration.util.StringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class helps in preParsing the default master configurations and return
 * as a {@link Template} object.
 *
 * @author vitiwari
 *
 */
public class GenericTemplatePreParser implements TemplatePreParser {

    private static final String INCIDENT_CONFIG_FILE = "incidentDefaultTemplate.json";
    private static final String CHANGE_CONFIG_FILE = "incidentDefaultTemplate.json";

    @Override
    public Template loadDefaults(ARServerForm form) throws ParsingException {
        String fileName = "";
        switch (form) {
            case INCIDENT_FORM:
                fileName = INCIDENT_CONFIG_FILE;
                break;
            case CHANGE_FORM:
                fileName = CHANGE_CONFIG_FILE;
        }
        Template template = new Template();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            String configJson = getFile(fileName);
            rootNode = mapper.readTree(configJson);
        } catch (IOException e) {
            throw new ParsingException(StringUtil.format(Message.CONFIG_FILE_NOT_VALID, new Object[]{e.getMessage()}));
        }

        // Read the config details and map to pojo
        String configString;
        try {
            JsonNode configuration = rootNode.get("config");
            configString = mapper.writeValueAsString(configuration);
            Configuration config = mapper.readValue(configString, Configuration.class);
            template.setConfig(config);
        } catch (IOException e) {
            throw new ParsingException(StringUtil.format(Message.CONFIG_PROPERTY_NOT_FOUND, new Object[]{e.getMessage()}));
        }

        // Read the payload details and map to pojo
        try {
            JsonNode payloadNode = rootNode.get("eventDefinition");
            String payloadString = mapper.writeValueAsString(payloadNode);
            TSIEvent event = mapper.readValue(payloadString, TSIEvent.class);
            template.setEventDefinition(event);
        } catch (IOException e) {
            throw new ParsingException(StringUtil.format(Message.PAYLOAD_PROPERTY_NOT_FOUND, new Object[]{}));
        }

        // Iterate over the properties and if it starts with '@', put it to
        // itemValueMap
        Iterator<Entry<String, JsonNode>> nodes = rootNode.fields();
        Map<String, FieldItem> fieldItemMap = new HashMap<String, FieldItem>();
        while (nodes.hasNext()) {
            Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) nodes.next();
            if (entry.getKey().startsWith("@")) {
                try {
                    String placeholderNode = mapper.writeValueAsString(entry.getValue());
                    FieldItem placeholderDefinition = mapper.readValue(placeholderNode, FieldItem.class);
                    fieldItemMap.put(entry.getKey(), placeholderDefinition);
                } catch (IOException e) {
                    throw new ParsingException(StringUtil.format(Message.PAYLOAD_PROPERTY_NOT_FOUND, new Object[]{entry.getKey()}));
                }
            }
        }
        template.setFieldItemMap(fieldItemMap);
        return template;
    }

    private String getFile(String fileName) throws IOException {
		//Get file from resources folder
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(fileName);
        InputStreamReader streamReader = new InputStreamReader(inputStream, "UTF-8");
        BufferedReader in = new BufferedReader(streamReader);
        StringBuffer text= new StringBuffer("");
        for (String line; (line = in.readLine()) != null;) {
        	text = text.append(line);
        }
        return text.toString();
    }
    
}
