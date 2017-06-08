package com.bmc.truesight.saas.remedy.integration.beans;

/**
 * This is a pojo class, which is used in the {@link Event}
 *
 * @author vitiwari
 *
 */
public class EventSource {

    private String name;
    private String type;
    private String ref;

    public EventSource(EventSource source) {
        this.setName(source.getName());
        this.setRef(source.getRef());
        this.setType(source.getType());
    }

    public EventSource() {
        // default constructor
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}
