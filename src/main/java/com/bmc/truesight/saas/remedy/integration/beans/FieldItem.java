package com.bmc.truesight.saas.remedy.integration.beans;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * This is a pojo class, which is used in the
 * {@link com.bmc.truesight.saas.remedy.integration.beans.Template Template}
 *
 * @author vitiwari
 *
 */
@JsonInclude(Include.NON_NULL)
public class FieldItem {

    private Integer fieldId;
    private String fieldName;
    private String fieldType;
    private String fieldInstance;
    Map<String, String> valueMap;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public String getFieldInstance() {
        return fieldInstance;
    }

    public void setFieldInstance(String fieldInstance) {
        this.fieldInstance = fieldInstance;
    }

    public Map<String, String> getValueMap() {
        return valueMap;
    }

    public void setValueMap(Map<String, String> valueMap) {
        this.valueMap = valueMap;
    }

    public Integer getFieldId() {
        return fieldId;
    }

    public void setFieldId(Integer fieldId) {
        this.fieldId = fieldId;
    }

}
