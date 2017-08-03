package com.bmc.truesight.saas.remedy.integration.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmc.truesight.saas.remedy.integration.beans.Configuration;
import com.bmc.truesight.saas.remedy.integration.beans.Result;
import com.bmc.truesight.saas.remedy.integration.beans.Success;
import com.bmc.truesight.saas.remedy.integration.beans.TSIEvent;
import com.bmc.truesight.saas.remedy.integration.exception.BulkEventsIngestionFailedException;
import com.bmc.truesight.saas.remedy.integration.util.Constants;

public class EventIngestionExecuterService {

    private static final int EVENTS_INGESTION_SIZE = 30;
    private final static Logger log = LoggerFactory.getLogger(EventIngestionExecuterService.class);

    public Result ingestEvents(List<TSIEvent> eventsList, Configuration configuration) throws BulkEventsIngestionFailedException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(Constants.EVENTASYNC_FIXED_THREAD_POOL);
        Result resultFinal = new Result();
        List<Future<Result>> resultList = new ArrayList<>();
        if (eventsList.size() > 0) {
            int totalSize = eventsList.size();
            int startIndex = 0;
            while (totalSize > 0) {
                int taskSize = 0;
                if (totalSize <= EVENTS_INGESTION_SIZE) {
                    taskSize = totalSize;
                    totalSize = 0;
                } else {
                    taskSize = EVENTS_INGESTION_SIZE;
                    totalSize = totalSize - taskSize;
                }
                log.debug("Adding events from {} to {} to a thread ", startIndex, (startIndex + taskSize));
                Future<Result> result = executor.submit(new CallableBulkEventHttpClient(eventsList.subList(startIndex, (startIndex + taskSize)), configuration));
                resultList.add(result);
                startIndex = startIndex + taskSize;
            }
            log.debug("Time to start {} threads in parrallel ", executor.getTaskCount());

            int successCount = 0;
            int failureCount = 0;
            int partialCount = 0;
            for (Future<Result> future : resultList) {
                Result resultitem;
                try {
                    resultitem = future.get();
                    if (resultitem.getSuccess() != null) {
                        if (resultitem.getSuccess() == Success.TRUE) {
                            successCount++;
                        } else if (resultitem.getSuccess() == Success.FALSE) {
                            failureCount++;
                        } else if (resultitem.getSuccess() == Success.PARTIAL) {
                            partialCount++;
                        }
                    }

                    if (resultitem.getAccepted() != null) {
                        if (resultFinal.getAccepted() == null) {
                            resultFinal.setAccepted(new ArrayList<>(resultitem.getAccepted()));
                        } else {
                            resultFinal.getAccepted().addAll(resultitem.getAccepted());
                        }
                    }

                    if (resultitem.getErrors() != null) {
                        if (resultFinal.getErrors() == null) {
                            resultFinal.setErrors(new ArrayList<>(resultitem.getErrors()));
                        } else {
                            resultFinal.getErrors().addAll(resultitem.getErrors());
                        }
                    }
                    if (resultitem.getSent() != 0) {
                        resultFinal.setSent(resultFinal.getSent() + resultitem.getSent());
                    }
                } catch (InterruptedException e) {
                    log.error(e.getMessage());
                } catch (ExecutionException e) {
                    executor.shutdownNow();
                    throw new BulkEventsIngestionFailedException(e.getMessage());
                }
            }
            if (partialCount > 0) {
                resultFinal.setSuccess(Success.PARTIAL);
            } else if (failureCount > 0) {
                resultFinal.setSuccess(Success.FALSE);
            } else if (successCount > 0) {
                resultFinal.setSuccess(Success.TRUE);
            } else {
                resultFinal.setSuccess(Success.FALSE);
            }
            // shut down the executor service now
            executor.shutdown();

        }
        return resultFinal;
    }
}
