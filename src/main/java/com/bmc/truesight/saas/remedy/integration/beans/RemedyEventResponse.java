package com.bmc.truesight.saas.remedy.integration.beans;

import java.util.List;

public class RemedyEventResponse {

    private List<TSIEvent> validEventList;
    private List<TSIEvent> invalidEventList;
    private int largeInvalidEventCount;

    public List<TSIEvent> getValidEventList() {
        return validEventList;
    }

    public void setValidEventList(List<TSIEvent> validEventList) {
        this.validEventList = validEventList;
    }

    public int getLargeInvalidEventCount() {
        return largeInvalidEventCount;
    }

    public void setLargeInvalidEventCount(int largeInvalidEventCount) {
        this.largeInvalidEventCount = largeInvalidEventCount;
    }

	public List<TSIEvent> getInvalidEventList() {
		return invalidEventList;
	}

	public void setInvalidEventList(List<TSIEvent> invalidEventList) {
		this.invalidEventList = invalidEventList;
	}
}
