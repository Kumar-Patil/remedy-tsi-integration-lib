package com.bmc.truesight.saas.remedy.integration.adapter;

import java.lang.reflect.Field;
import java.util.Currency;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmc.arsys.api.AttachmentField;
import com.bmc.arsys.api.CurrencyField;
import com.bmc.arsys.api.DateTimeField;
import com.bmc.arsys.api.DecimalField;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.EnumItem;
import com.bmc.arsys.api.IntegerField;
import com.bmc.arsys.api.SelectionField;
import com.bmc.arsys.api.SelectionFieldLimit;
import com.bmc.arsys.api.TimeOnlyField;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.bmc.truesight.saas.remedy.integration.beans.EventSource;
import com.bmc.truesight.saas.remedy.integration.beans.FieldItem;
import com.bmc.truesight.saas.remedy.integration.beans.TSIEvent;
import com.bmc.truesight.saas.remedy.integration.beans.Template;
import com.bmc.truesight.saas.remedy.integration.util.Constants;

/**
 * This is an adapter which converts the remedy {@link Entry} items into
 * {@link TSIEvent} (TSI Events)
 *
 * @author vitiwari
 */
public class RemedyEntryEventAdapter {

    private static final Logger log = LoggerFactory.getLogger(RemedyEntryEventAdapter.class);

    private Map<Integer, com.bmc.arsys.api.Field> fieldIdFieldMap;

    public RemedyEntryEventAdapter(Map<Integer, com.bmc.arsys.api.Field> fieldIdFieldMap) {
        this.fieldIdFieldMap = fieldIdFieldMap;
    }

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
            properties.put(key, getValueFromEntry(template, entry, properties.get(key)));
        }
        event.setSeverity(getValueFromEntry(template, entry, event.getSeverity()));
        event.setStatus(getValueFromEntry(template, entry, event.getStatus()));
        event.setCreatedAt(getValueFromEntry(template, entry, event.getCreatedAt()));
        event.setEventClass(getValueFromEntry(template, entry, event.getEventClass()));

        // valiadting source
        EventSource source = event.getSource();
        source.setName(getValueFromEntry(template, entry, source.getName()));
        source.setType(getValueFromEntry(template, entry, source.getType()));
        source.setRef(getValueFromEntry(template, entry, source.getRef()));

        /*EventSource sender = event.getSender();
        sender.setName(getValueFromEntry(template, entry, sender.getName()));
        sender.setType(getValueFromEntry(template, entry, sender.getType()));
        sender.setRef(getValueFromEntry(template, entry, sender.getRef()));*/
        return event;

    }

    private String getValueFromEntry(Template template, Entry entry, String placeholder) {
        if (placeholder.startsWith("@")) {
            FieldItem fieldItem = template.getFieldDefinitionMap().get(placeholder);
            com.bmc.arsys.api.Field field = fieldIdFieldMap.get(fieldItem.getFieldId());

            if (field != null) {
                Value value = entry.get(fieldItem.getFieldId());
                if (field instanceof SelectionField) {
                    SelectionFieldLimit sFieldLimit = (SelectionFieldLimit) field.getFieldLimit();
                    String returnVal = Constants.NONE_VALUE;
                    if (value.getValue() != null) {
                        if (sFieldLimit != null) {
                            List<EnumItem> eItemList = sFieldLimit.getValues();
                            for (EnumItem eItem : eItemList) {
                                if (eItem.getEnumItemNumber() == Integer.parseInt(value.getValue().toString())) {
                                    returnVal = eItem.getEnumItemName();
                                    break;
                                }
                            }
                        }
                        if (fieldItem.getValueMap() != null && fieldItem.getValueMap().get(value.getValue().toString()) != null) {
                            return fieldItem.getValueMap().get(value.getValue().toString());
                        } else {
                            return returnVal;
                        }
                    } else {
                        return Constants.NONE_VALUE;
                    }
                } else if (field instanceof DateTimeField || field instanceof TimeOnlyField) {
                    Timestamp dateTimeTS = (Timestamp) value.getValue();
                    if (dateTimeTS != null) {
                        return Long.toString(dateTimeTS.getValue());
                    } else {
                        return Constants.NONE_VALUE;
                    }
                } else if (field instanceof AttachmentField) {
                    log.debug("FieldId,FieldName ({},{}) is an attachment field which is not expected in the mapping, ignoring the attachment field.", field.getFieldID(), field.getName());
                    return Constants.NONE_VALUE;
                } else if (field instanceof IntegerField || field instanceof CurrencyField || field instanceof DecimalField) {
                    String val = "";
                    if (value != null && value.getValue() != null) {
                        val = value.getValue().toString();
                    }
                    return val;

                } else {
                    String val = Constants.NONE_VALUE;
                    if (value != null && value.getValue() != null) {
                        val = value.getValue().toString();
                        if (fieldItem.getValueMap() != null && fieldItem.getValueMap().get(val) != null) {
                            return fieldItem.getValueMap().get(val);
                        } else {
                            return val;
                        }
                    }
                    return val;
                }
            } else {
                return Constants.NONE_VALUE;
            }
        } else if (placeholder.startsWith("#")) {
            Field fieldItem;
            String val = Constants.NONE_VALUE;
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

}
