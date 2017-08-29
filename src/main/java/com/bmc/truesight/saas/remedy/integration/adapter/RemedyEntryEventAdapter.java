package com.bmc.truesight.saas.remedy.integration.adapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.Value;
import com.bmc.truesight.saas.remedy.integration.beans.EventSource;
import com.bmc.truesight.saas.remedy.integration.beans.FieldItem;
import com.bmc.truesight.saas.remedy.integration.beans.Template;
import com.bmc.truesight.saas.remedy.integration.beans.TSIEvent;

/**
 * This is an adapter which converts the remedy {@link Entry} items into
 * {@link TSIEvent} (TSI Events)
 *
 * @author vitiwari
 */
public class RemedyEntryEventAdapter {

    private static final Logger log = LoggerFactory.getLogger(RemedyEntryEventAdapter.class);

    /**
     * This method is an adapter which converts a Remedy Entry into Event object
     *
     * @param template A {@link Template} instance which contains the field
     * mapping and event Definition
     * @param entry {@link Entry} Object representing ARServer Record
     * @return TsiEvent {@link TSIEvent} object compatible to TSI event
     * ingestion API
     */
    public TSIEvent convertEntryToEvent(Template template, Entry entry) {

        TSIEvent event = new TSIEvent(template.getEventDefinition());

        event.setTitle(getValueFromEntry(template, entry, event.getTitle()));
        Map<String, String> properties = event.getProperties();
        for (String key : properties.keySet()) {
            String val = getValueFromEntry(template, entry, properties.get(key));
            if (isItRemedyDate(val)) {
                properties.put(key, resolveDate(getValueFromEntry(template, entry, properties.get(key))));
            } else {
                properties.put(key, getValueFromEntry(template, entry, properties.get(key)));
            }
        }
        event.setSeverity(getValueFromEntry(template, entry, event.getSeverity()));
        event.setStatus(getValueFromEntry(template, entry, event.getStatus()));
        event.setCreatedAt(resolveDate(getValueFromEntry(template, entry, event.getCreatedAt())));
        event.setEventClass(getValueFromEntry(template, entry, event.getEventClass()));

        // valiadting source
        EventSource source = event.getSource();
        source.setName(getValueFromEntry(template, entry, source.getName()));
        source.setType(getValueFromEntry(template, entry, source.getType()));
        source.setRef(getValueFromEntry(template, entry, source.getRef()));

        EventSource sender = event.getSender();
        sender.setName(getValueFromEntry(template, entry, sender.getName()));
        sender.setType(getValueFromEntry(template, entry, sender.getType()));
        sender.setRef(getValueFromEntry(template, entry, sender.getRef()));
        return event;

    }

    private String getValueFromEntry(Template template, Entry entry, String placeholder) {
        if (placeholder.startsWith("@")) {
            FieldItem fieldItem = template.getFieldItemMap().get(placeholder);
            Value value = entry.get(fieldItem.getFieldId());
            String val = "";
            if (value != null && value.getValue() != null) {
                val = value.getValue().toString();
            }
            if (fieldItem.getValueMap() != null && fieldItem.getValueMap().get(val) != null) {
                return fieldItem.getValueMap().get(val);
            } else {
                return val;
            }
        } else if (placeholder.startsWith("#")) {
            Field fieldItem;
            String val = "";
            try {
                fieldItem = template.getConfig().getClass().getDeclaredField(placeholder.substring(1));
                if (fieldItem != null) {
                    fieldItem.setAccessible(true);
                    val = fieldItem.get(template.getConfig()).toString();
                }
            } catch (NoSuchFieldException e) {
                log.error("There is no field \"{}\" in config. please review the mapping", placeholder.substring(1));
            } catch (SecurityException e) {
                log.error("Cannot acceess field \"{}\". {}", placeholder.substring(1), e.getMessage());
            } catch (IllegalArgumentException e) {
                log.error("Cannot get value for the field \"{}\". {}", placeholder.substring(1), e.getMessage());
            } catch (IllegalAccessException e) {
                log.error("Cannot get value for the field \"{}\". {}", placeholder.substring(1), e.getMessage());
            }
            return val;
        } else {
            return placeholder;
        }
    }

    private String resolveDate(String dateString) {
        Long longDate = null;
        try {
            if (dateString == null || dateString.trim().length() == 0) {
                return "";
            } else {
                longDate = Long.parseLong(dateString.substring(11, 21));
            }
        } catch (NumberFormatException nfe) {
            log.error("Remedy created date parsing failed, returning as blank value");
            return "";
        }
        return longDate.toString();
    }

    private boolean isItRemedyDate(String val) {
        boolean case1 = val.length() >= 22;
        if (case1) {
            boolean case2 = val.startsWith("[") && val.endsWith("]");
            boolean case3 = val.substring(1, 10).equalsIgnoreCase("Timestamp");
            boolean case4 = false;
            try {
                Long.parseLong(val.substring(11, 21));
                case4 = true;
            } catch (NumberFormatException nfe) {
                case4 = false;
            }
            return case1 && case2 && case3 && case4;
        }
        return case1;
    }
}
