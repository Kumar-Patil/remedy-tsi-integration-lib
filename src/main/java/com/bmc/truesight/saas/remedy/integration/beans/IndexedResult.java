package com.bmc.truesight.saas.remedy.integration.beans;

import java.util.concurrent.Future;

public class IndexedResult {

    private Future<Result> result;
    private int startIndex;
    private int taskSize;

    public Future<Result> getResult() {
        return result;
    }

    public void setResult(Future<Result> result) {
        this.result = result;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public int getTaskSize() {
        return taskSize;
    }

    public void setTaskSize(int taskSize) {
        this.taskSize = taskSize;
    }

}
