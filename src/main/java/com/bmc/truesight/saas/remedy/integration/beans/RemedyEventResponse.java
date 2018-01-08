package com.bmc.truesight.saas.remedy.integration.beans;

import java.util.List;

public class RemedyEventResponse {

    private List<TSIEvent> validEventList;
    private List<InvalidEvent> invalidEventList;

    public List<InvalidEvent> getInvalidEventList() {
        return invalidEventList;
    }

    public void setInvalidEventList(List<InvalidEvent> invalidEventList) {
        this.invalidEventList = invalidEventList;
    }

    public List<TSIEvent> getValidEventList() {
        return validEventList;
    }

    public void setValidEventList(List<TSIEvent> validEventList) {
        this.validEventList = validEventList;
    }

}
