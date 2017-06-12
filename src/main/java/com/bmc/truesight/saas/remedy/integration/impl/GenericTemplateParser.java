package com.bmc.truesight.saas.remedy.integration.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import com.bmc.truesight.saas.remedy.integration.TemplateParser;
import com.bmc.truesight.saas.remedy.integration.beans.Configuration;
import com.bmc.truesight.saas.remedy.integration.beans.TSIEvent;
import com.bmc.truesight.saas.remedy.integration.beans.FieldItem;
import com.bmc.truesight.saas.remedy.integration.beans.Template;
import com.bmc.truesight.saas.remedy.integration.exception.ParsingException;
import com.bmc.truesight.saas.remedy.integration.util.Message;
import com.bmc.truesight.saas.remedy.integration.util.StringUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Generic Template Parser
 *
 * @author vitiwari
 *
 */
public class GenericTemplateParser implements TemplateParser {

    @Override
    public Template readParseConfigJson(String configJson) throws ParsingException {
        return parse(configJson);
    }

    /**
     * Used to parse the template in case of template available as json file
     *
     * @param fileName Name of the template json file
     * @throws ParsingException throws exception in case of unsuccessful parsing
     */
    @Override
    public Template readParseConfigFile(String fileName) throws ParsingException {
        // Read the file in String
        String configJson = null;
        try {
            configJson = FileUtils.readFileToString(new File(fileName), "UTF8");
        } catch (IOException e) {
            throw new ParsingException(StringUtil.format(Message.CONFIG_FILE_NOT_VALID, new Object[]{fileName}));
        }
        return parse(configJson);
    }

    private Template parse(String configJson) throws ParsingException {
        Template template = new Template();

        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = null;
        try {
            rootNode = mapper.readTree(configJson);
        } catch (IOException e) {
            throw new ParsingException(StringUtil.format(Message.CONFIG_FILE_NOT_VALID, new Object[]{}));
        }

        // Read the config details and map to pojo
        String configString;
        try {
            JsonNode configuration = rootNode.get("config");
            configString = mapper.writeValueAsString(configuration);
            Configuration config = mapper.readValue(configString, Configuration.class);
            template.setConfig(config);
        } catch (IOException e) {
            throw new ParsingException(StringUtil.format(Message.CONFIG_PROPERTY_NOT_FOUND, new Object[]{}));
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
                    throw new ParsingException(
                            StringUtil.format(Message.PAYLOAD_PROPERTY_NOT_FOUND, new Object[]{entry.getKey()}));
                }
            }
        }
        template.setFieldItemMap(fieldItemMap);
        return template;
    }

}
