package com.bmc.truesight.saas.remedy.integration.beans;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {

    private int sent;
    private Success success;
    private List<Error> errors;
    private List<Accepted> accepted;

    public Result() {

    }

    public Result(int sent, Success success, List<Error> errors, List<Accepted> accepted) {
        this.sent = sent;
        this.success = success;
        this.errors = errors;
        this.accepted = accepted;
    }

    public int getSent() {
        return sent;
    }

    public void setSent(int sent) {
        this.sent = sent;
    }

    public Success getSuccess() {
        return success;
    }

    public void setSuccess(Success success) {
        this.success = success;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    public List<Accepted> getAccepted() {
        return accepted;
    }

    public void setAccepted(List<Accepted> accepted) {
        this.accepted = accepted;
    }
}
