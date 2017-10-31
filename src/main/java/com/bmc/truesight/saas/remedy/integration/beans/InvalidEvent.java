package com.bmc.truesight.saas.remedy.integration.beans;

public class InvalidEvent {

    private String entryId;
    private TSIEvent invalidEvent;
    private long eventSize;
    private String maxSizePropertyName;
    private long propertySize;

    public InvalidEvent(String entryId) {
        this.entryId = entryId;
    }

    public TSIEvent getInvalidEvent() {
        return invalidEvent;
    }

    public void setInvalidEvent(TSIEvent invalidEvent) {
        this.invalidEvent = invalidEvent;
    }

    public long getEventSize() {
        return eventSize;
    }

    public void setEventSize(long eventSize) {
        this.eventSize = eventSize;
    }

    public String getMaxSizePropertyName() {
        return maxSizePropertyName;
    }

    public void setMaxSizePropertyName(String maxSizePropertyName) {
        this.maxSizePropertyName = maxSizePropertyName;
    }

    public long getPropertySize() {
        return propertySize;
    }

    public void setPropertySize(long propertySize) {
        this.propertySize = propertySize;
    }

    public String getEntryId() {
        return entryId;
    }

    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }
}
