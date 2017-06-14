package com.bmc.truesight.saas.remedy.integration.beans;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * This is a POJO class, which is mapped to the configuration field (ie config)
 * in incident/change json template. The fields contain the remedy access
 * details, TSI details and other configuration
 *
 * @author vitiwari
 */
public class Configuration {

    private String remedyHostName;
    private Integer remedyPort;
    private String remedyUserName;
    private String remedyPassword;
    private String tsiEventEndpoint;
    private String tsiApiToken;
    private Integer chunkSize;
    private List<Integer> conditionFields;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date startDateTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm a z")
    private Date endDateTime;
    private List<Integer> queryStatusList;
    private Integer retryConfig;
    private Integer waitMsBeforeRetry;

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getRemedyHostName() {
        return remedyHostName;
    }

    public void setRemedyHostName(String remedyHostName) {
        this.remedyHostName = remedyHostName;
    }

    public Integer getRemedyPort() {
        return remedyPort;
    }

    public void setRemedyPort(Integer remedyPort) {
        this.remedyPort = remedyPort;
    }

    public String getRemedyUserName() {
        return remedyUserName;
    }

    public void setRemedyUserName(String remedyUserName) {
        this.remedyUserName = remedyUserName;
    }

    public String getRemedyPassword() {
        return remedyPassword;
    }

    public void setRemedyPassword(String remedyPassword) {
        this.remedyPassword = remedyPassword;
    }

    public String getTsiApiToken() {
        return tsiApiToken;
    }

    public void setTsiApiToken(String tsiApiToken) {
        this.tsiApiToken = tsiApiToken;
    }

    public Integer getChunkSize() {
        return chunkSize;
    }

    public void setChunkSize(Integer chunkSize) {
        this.chunkSize = chunkSize;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Integer getRetryConfig() {
        return retryConfig;
    }

    public void setRetryConfig(Integer retryConfig) {
        this.retryConfig = retryConfig;
    }

    public List<Integer> getConditionFields() {
        return conditionFields;
    }

    public void setConditionFields(List<Integer> conditionFields) {
        this.conditionFields = conditionFields;
    }

    public String getTsiEventEndpoint() {
        return tsiEventEndpoint;
    }

    public void setTsiEventEndpoint(String tsiEventEndpoint) {
        this.tsiEventEndpoint = tsiEventEndpoint;
    }

    public Integer getWaitMsBeforeRetry() {
        return waitMsBeforeRetry;
    }

    public void setWaitMsBeforeRetry(Integer waitMsBeforeRetry) {
        this.waitMsBeforeRetry = waitMsBeforeRetry;
    }

    public List<Integer> getQueryStatusList() {
        return queryStatusList;
    }

    public void setQueryStatusList(List<Integer> queryStatusList) {
        this.queryStatusList = queryStatusList;
    }

}
