package com.bmc.truesight.saas.remedy.integration.beans;

import java.util.Map;

/**
 * This is a pojo class, which is used in the
 * {@link com.bmc.truesight.saas.remedy.integration.beans.Template Template}
 *
 * @author vitiwari
 *
 */
public class FieldItem {

    private Integer fieldId;
    Map<String, String> valueMap;

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
