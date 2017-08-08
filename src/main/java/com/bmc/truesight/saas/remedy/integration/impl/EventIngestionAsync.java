package com.bmc.truesight.saas.remedy.integration.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmc.truesight.saas.remedy.integration.beans.Configuration;
import com.bmc.truesight.saas.remedy.integration.beans.Error;
import com.bmc.truesight.saas.remedy.integration.beans.Result;
import com.bmc.truesight.saas.remedy.integration.beans.Success;
import com.bmc.truesight.saas.remedy.integration.beans.TSIEvent;
import com.bmc.truesight.saas.remedy.integration.exception.BulkEventsIngestionFailedException;
import com.bmc.truesight.saas.remedy.integration.util.Constants;

public class EventIngestionAsync {

    private static final Logger LOG = LoggerFactory.getLogger(EventIngestionAsync.class);

    public static class EventIngestionTask extends RecursiveTask<Result> {

        private List<TSIEvent> eventList;
        private Configuration configuration;

        public EventIngestionTask(List<TSIEvent> eventList, Configuration configuration) {
            this.eventList = eventList;
            this.configuration = configuration;
        }

        @Override
        protected Result compute() {
            List<EventIngestionTask> tasks = new ArrayList<EventIngestionTask>();
            if (eventList.size() <= Constants.EVENTS_INGESTION_SIZE) {
                LOG.debug("Executing {} events ingestion", eventList.size());
                try {
                    return new GenericBulkEventHttpClient(this.configuration).pushBulkEventsToTSI(eventList);
                } catch (BulkEventsIngestionFailedException e) {
                    LOG.error(e.getMessage());
                    Result temp = new Result();
                    temp.setSuccess(Success.FALSE);
                    List<Error> errorList = new ArrayList<Error>();
                    errorList.add(new Error(0, e.getMessage()));
                    temp.setErrors(errorList);
                    return temp;
                }

            } else {
                int startIndex = 0;
                int remainingEventSize = eventList.size();
                while (remainingEventSize > 0) {
                    if (remainingEventSize > Constants.EVENTS_INGESTION_SIZE) {
                        LOG.debug("adding Event ingestion task from {} to {}", startIndex, (startIndex + Constants.EVENTS_INGESTION_SIZE));
                        tasks.add(new EventIngestionTask(eventList.subList(startIndex, (startIndex + Constants.EVENTS_INGESTION_SIZE)), configuration));
                        remainingEventSize -= Constants.EVENTS_INGESTION_SIZE;
                        startIndex += Constants.EVENTS_INGESTION_SIZE;
                    } else {
                        LOG.debug("adding Event ingestion task from {} to {}", startIndex, (startIndex + remainingEventSize));
                        tasks.add(new EventIngestionTask(eventList.subList(startIndex, (startIndex + remainingEventSize)), configuration));
                        startIndex += remainingEventSize;
                        remainingEventSize = 0;
                    }
                }

            }

            tasks.forEach(task -> {
                task.fork();
            });
            List<Result> resultList = new ArrayList<Result>();
            tasks.forEach(task -> {
                resultList.add(task.join());
            });
            Result resultFinal = new Result();
            int successCount = 0;
            int failureCount = 0;
            int partialCount = 0;
            for (Result resultitem : resultList) {
                try {
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
                } catch (Exception e) {
                    LOG.error(e.getMessage());
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
            return resultFinal;
        }

    }

    public static Result ingestEvents(List<TSIEvent> eventList, Configuration config) {
        LOG.debug("Fork and join creating");
        final ForkJoinPool pool = new ForkJoinPool(Constants.EVENTASYNC_FIXED_THREAD_POOL);
        try {
            LOG.debug("Async mode " + pool.getAsyncMode());
            return pool.invoke(new EventIngestionTask(eventList, config));
        } finally {
            pool.shutdown();
        }
    }
}
