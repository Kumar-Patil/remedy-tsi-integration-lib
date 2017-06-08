package com.bmc.truesight.saas.remedy.integration.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Event {

    private String title;
    private List<String> fingerprintFields;
    private String severity;
    private String status;
    private Map<String, String> properties;
    private String createdAt;
    private String eventClass;
    private EventSource source;
    private EventSource sender;

    public Event(Event payload) {
        this.setTitle(payload.getTitle());
        this.setFingerprintFields(new ArrayList<String>(payload.fingerprintFields));
        this.setCreatedAt(payload.getCreatedAt());
        this.setEventClass(payload.getEventClass());
        this.setProperties(new HashMap<String, String>(payload.getProperties()));
        this.setSender(new EventSource(payload.getSender()));
        this.setSource(new EventSource(payload.getSource()));
        this.setSeverity(payload.getSeverity());
        this.setStatus(payload.getStatus());
    }

    public Event() {
        // Default Constructor
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getFingerprintFields() {
        return fingerprintFields;
    }

    public void setFingerprintFields(List<String> fingerprintFields) {
        this.fingerprintFields = fingerprintFields;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getEventClass() {
        return eventClass;
    }

    public void setEventClass(String eventClass) {
        this.eventClass = eventClass;
    }

    public EventSource getSource() {
        return source;
    }

    public void setSource(EventSource source) {
        this.source = source;
    }

    public EventSource getSender() {
        return sender;
    }

    public void setSender(EventSource sender) {
        this.sender = sender;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
