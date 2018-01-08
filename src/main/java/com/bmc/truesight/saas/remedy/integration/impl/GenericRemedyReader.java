package com.bmc.truesight.saas.remedy.integration.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmc.arsys.api.ARErrors;
import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.Field;
import com.bmc.arsys.api.OutputInteger;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.SortInfo;
import com.bmc.arsys.api.StatusInfo;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.bmc.truesight.saas.remedy.integration.ARServerForm;
import com.bmc.truesight.saas.remedy.integration.RemedyReader;
import com.bmc.truesight.saas.remedy.integration.adapter.RemedyEntryEventAdapter;
import com.bmc.truesight.saas.remedy.integration.beans.InvalidEvent;
import com.bmc.truesight.saas.remedy.integration.beans.RemedyEventResponse;
import com.bmc.truesight.saas.remedy.integration.beans.TSIEvent;
import com.bmc.truesight.saas.remedy.integration.beans.Template;
import com.bmc.truesight.saas.remedy.integration.exception.RemedyLoginFailedException;
import com.bmc.truesight.saas.remedy.integration.exception.RemedyReadFailedException;
import com.bmc.truesight.saas.remedy.integration.util.Constants;
import com.bmc.truesight.saas.remedy.integration.util.StringUtil;

/**
 * This class is a generic implementation of {@link RemedyReader}.
 *
 * @author vitiwari
 *
 */
public class GenericRemedyReader implements RemedyReader {

    private static final Logger log = LoggerFactory.getLogger(GenericRemedyReader.class);
    private static final Integer STATUS_FIELD_ID = 7;
    private static final Integer INCIDENT_STATUS_CLOSED = 5;
    private static final Integer CHANGE_STATUS_CLOSED = 11;

    @Override
    public ARServerUser createARServerContext(String hostName, Integer port, String userName, String password) {
        ARServerUser arServerContext = new ARServerUser();
        arServerContext.setServer(hostName);
        if (port != null) {
            arServerContext.setPort(port);
        }
        arServerContext.setUser(userName);
        arServerContext.setPassword(password);
        return arServerContext;
    }

    @Override
    public boolean login(ARServerUser arServerContext) throws RemedyLoginFailedException {
        try {
            arServerContext.login();
            log.info("Login successful to remedy server");
        } catch (ARException e1) {
            throw new RemedyLoginFailedException(StringUtil.format(Constants.REMEDY_LOGIN_FAILED, new Object[]{e1.getMessage()}));
        }
        return true;
    }

    @Override
    public RemedyEventResponse readRemedyTickets(ARServerUser arServerContext, ARServerForm formName, Template template, int startFrom, int chunkSize, OutputInteger recordsCount, RemedyEntryEventAdapter adapter) throws RemedyReadFailedException {
        RemedyEventResponse response = new RemedyEventResponse();
        //keeping as set to avoid duplicates
        Set<Integer> fieldsList = new HashSet<>();
        log.debug("Reading Remedy tickets,total no of FieldDefinitionMap elements is =>{}", template.getFieldDefinitionMap().size());
        template.getEventDefinition().getProperties().entrySet().forEach(propEntry -> {
            if (propEntry.getValue().startsWith("@")) {
                fieldsList.add(template.getFieldDefinitionMap().get(propEntry.getValue()).getFieldId());
            }
        });

        int[] queryFieldsList = new int[fieldsList.size()];
        int index = 0;
        for (Integer i : fieldsList) {
            queryFieldsList[index++] = i;
        }
        log.debug("building parameters to make the call __________________________");
        log.debug("field ids  -> Size : {}, values: {}", queryFieldsList.length, fieldsList.size());
        //Qualifier Created for Date condition fields, for example if closed date is in startDate & endDate 
        log.debug("Bulding Qualifier to make the call __________________________ ");
        QualifierInfo qualInfoF = null;
        for (int fieldId : template.getConfig().getConditionFields()) {
            QualifierInfo qualInfo1 = buildFieldValueQualification(fieldId,
                    new Value(new Timestamp(template.getConfig().getStartDateTime()), DataType.TIME), RelationalOperationInfo.AR_REL_OP_GREATER_EQUAL);

            QualifierInfo qualInfo2 = buildFieldValueQualification(fieldId,
                    new Value(new Timestamp(template.getConfig().getEndDateTime()), DataType.TIME), RelationalOperationInfo.AR_REL_OP_LESS_EQUAL);

            QualifierInfo qualInfo = new QualifierInfo(QualifierInfo.AR_COND_OP_AND, qualInfo1, qualInfo2);

            if (qualInfoF != null) {
                qualInfoF = new QualifierInfo(QualifierInfo.AR_COND_OP_OR, qualInfoF, qualInfo);
            } else {
                qualInfoF = qualInfo;
            }
        }
        log.debug("Condition 1: Values of fields {} should fall in date {}, {}", template.getConfig().getConditionFields(), template.getConfig().getStartDateTime(), template.getConfig().getEndDateTime());
        //Status Query list
        List<Integer> queryStatusList = template.getConfig().getQueryStatusList();
        //If there is no statusQueryList Configured then create qualifier for Closed status
        if (queryStatusList == null || queryStatusList.isEmpty()) {
            QualifierInfo qualInfoStatus = null;
            if (formName == ARServerForm.INCIDENT_FORM) {
                qualInfoStatus = buildFieldValueQualification(STATUS_FIELD_ID,
                        new Value(INCIDENT_STATUS_CLOSED, DataType.INTEGER), RelationalOperationInfo.AR_REL_OP_EQUAL);
                log.debug("Condition 2: Status values -> {}", INCIDENT_STATUS_CLOSED);
            } else if (formName == ARServerForm.CHANGE_FORM) {
                qualInfoStatus = buildFieldValueQualification(STATUS_FIELD_ID,
                        new Value(CHANGE_STATUS_CLOSED, DataType.INTEGER), RelationalOperationInfo.AR_REL_OP_EQUAL);
                log.debug("Condition 2: Status values -> {}", CHANGE_STATUS_CLOSED);
            }
            qualInfoF = new QualifierInfo(QualifierInfo.AR_COND_OP_AND, qualInfoF, qualInfoStatus);

        } else {
            //else statusQueryList Configured, created Qualifier accordingly
            QualifierInfo qualInfoStatusF = null;
            QualifierInfo qualInfoStatus = null;
            for (int status : queryStatusList) {
                qualInfoStatus = buildFieldValueQualification(STATUS_FIELD_ID,
                        new Value(status, DataType.INTEGER), RelationalOperationInfo.AR_REL_OP_EQUAL);
                if (qualInfoStatusF != null) {
                    qualInfoStatusF = new QualifierInfo(QualifierInfo.AR_COND_OP_OR, qualInfoStatusF, qualInfoStatus);
                } else {
                    qualInfoStatusF = qualInfoStatus;
                }
            }
            qualInfoF = new QualifierInfo(QualifierInfo.AR_COND_OP_AND, qualInfoF, qualInfoStatusF);
            log.debug("Condition 2: Status values -> {}", queryStatusList);
        }
        log.debug("Qualifier making completed, about to start making the call");
        List<SortInfo> sortOrder = new ArrayList<SortInfo>();
        List<Entry> entryList = new ArrayList<>();
        boolean isSuccessful = false;
        int retryCount = 0;
        while (!isSuccessful && retryCount <= template.getConfig().getRetryConfig()) {
            try {
                entryList = arServerContext.getListEntryObjects(formName.toString(), qualInfoF,
                        startFrom, chunkSize, sortOrder, queryFieldsList, false, recordsCount);
                isSuccessful = true;
                log.debug("Recieved {} tickets  for starting index : {}, chunk size {}  ", new Object[]{entryList.size(), startFrom, chunkSize});
            } catch (ARException e) {
                if (retryCount < template.getConfig().getRetryConfig()) {
                    retryCount++;
                    log.debug("Reading  {} tickets from {} resulted into exception[{}], Re-trying for {} time", new Object[]{chunkSize, startFrom, e.getMessage(), retryCount});
                    try {
                        log.debug("Waiting for {} sec before trying again ......", (template.getConfig().getWaitMsBeforeRetry() / 1000));
                        Thread.sleep(template.getConfig().getWaitMsBeforeRetry());
                    } catch (InterruptedException e1) {
                    }

                    continue;
                } else {
                    log.debug("Skipping the read process, Reading tickets Failed for starting : {}, chunk size {} even after retrying for {} times", new Object[]{startFrom, chunkSize, retryCount});
                    throw new RemedyReadFailedException(e.getMessage()+", Skipping the read process, Reading tickets Failed for starting : " + startFrom + ", chunk size " + chunkSize + " even after retrying for " + retryCount + " times");

                }
            }
        }
        List<TSIEvent> payloadList = new ArrayList<TSIEvent>();
        List<InvalidEvent> invalidEventList = new ArrayList<InvalidEvent>();
        int largeEventCount = 0;
        if (adapter == null) {
            throw new RemedyReadFailedException("Adapter instance is null, it should not be null");
        }
        for (Entry entry : entryList) {
            TSIEvent event = adapter.convertEntryToEvent(template, entry);
            InvalidEvent invalidEvent = new InvalidEvent(entry.getEntryId());
            if (StringUtil.isObjectJsonSizeAllowed(event, invalidEvent)) {
                payloadList.add(event);
            } else {
                invalidEventList.add(invalidEvent);
                largeEventCount++;
            }
        }
        if (largeEventCount > 0) {
            log.debug("{} event(s) dropped before sending to TSI, size of event is greater than allowed limit({} Bytes). Please review the field mapping", new Object[]{largeEventCount, Constants.MAX_EVENT_SIZE_ALLOWED_BYTES});
        }
        response.setValidEventList(payloadList);
        response.setInvalidEventList(invalidEventList);

        return response;
    }

    @Override
    public RemedyEventResponse readRemedyTicketsWithId(ARServerUser arServerContext, ARServerForm formName, Template template, List<String> ids, RemedyEntryEventAdapter adapter) throws RemedyReadFailedException {

        RemedyEventResponse response = new RemedyEventResponse();
        //keeping as set to avoid duplicates
        Set<Integer> fieldsList = new HashSet<>();
        log.debug("template has {} FieldDefinitionMap", template.getFieldDefinitionMap().size());
        template.getEventDefinition().getProperties().entrySet().forEach(propEntry -> {
            if (propEntry.getValue().startsWith("@")) {
                fieldsList.add(template.getFieldDefinitionMap().get(propEntry.getValue()).getFieldId());
            }
        });

        log.debug("FieldsList populated with the field ids, count is {}", fieldsList.size());
        int[] queryFieldsList = new int[fieldsList.size()];
        int index = 0;
        for (Integer i : fieldsList) {
            queryFieldsList[index++] = i;
        }
        log.debug("Entry Ids -> {}", ids);

        List<Entry> entryList = new ArrayList<>();
        boolean isSuccessful = false;
        int retryCount = 0;
        while (!isSuccessful && retryCount <= template.getConfig().getRetryConfig()) {
            try {
                entryList = arServerContext.getListEntryObjects(formName.toString(), ids, queryFieldsList);
                isSuccessful = true;
                log.debug("Recieved {} tickets  for {} entry ids  ", new Object[]{entryList.size(), ids.size()});
            } catch (ARException e) {
                if (retryCount < template.getConfig().getRetryConfig()) {
                    retryCount++;
                    log.debug("Reading  {} tickets for {} entry ids  resulted into exception[{}], Re-trying for {} time", entryList.size(), ids.size(), retryCount);
                    try {
                        log.debug("Waiting for {} sec before trying again ......", (template.getConfig().getWaitMsBeforeRetry() / 1000));
                        Thread.sleep(template.getConfig().getWaitMsBeforeRetry());
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    continue;
                } else {
                    log.debug("Skipping the read process, Reading tickets Failed for {} entries even after retrying for {} times", ids.size(), retryCount);
                    throw new RemedyReadFailedException(e.getMessage()+", Skipping the read process, Reading tickets Failed for  " + ids.size() + " entries even after retrying for " + retryCount + " times");
                }
            }
        }
        List<TSIEvent> payloadList = new ArrayList<TSIEvent>();
        List<InvalidEvent> invalidEventList = new ArrayList<InvalidEvent>();
        int largeEventCount = 0;
        if (adapter == null) {
            throw new RemedyReadFailedException("Adapter instance is null, it should not be null");
        }
        for (Entry entry : entryList) {
            TSIEvent event = adapter.convertEntryToEvent(template, entry);
            InvalidEvent invalidEvent = new InvalidEvent(entry.getEntryId());
            if (StringUtil.isObjectJsonSizeAllowed(event, invalidEvent)) {
                payloadList.add(event);
            } else {
                invalidEventList.add(invalidEvent);
                largeEventCount++;
            }
        }
        if (largeEventCount > 0) {
            log.debug("{} event(s) dropped before sending to TSI, size of event is greater than allowed limit({} Bytes). Please review the field mapping", new Object[]{largeEventCount, Constants.MAX_EVENT_SIZE_ALLOWED_BYTES});
        }
        response.setValidEventList(payloadList);
        response.setInvalidEventList(invalidEventList);
        return response;
    }

    @Override
    public boolean exceededMaxServerEntries(ARServerUser arServerContext) {
        boolean returnVal = false;
        List<StatusInfo> messages = arServerContext.getLastStatus();
        if (messages != null && messages.size() > 0) {
            List<StatusInfo> updatedMessages = new ArrayList<StatusInfo>(messages.size());
            for (StatusInfo message : messages) {
                if (message.getMessageNum() == ARErrors.AR_WARN_MAX_ENTRIES_SERVER) {
                    returnVal = true;
                    // not breaking here to handle the case of having more than one message with
                } else {
                    // Adding all the warnings other than AR_WARN_MAX_ENTRIES_SERVER to the server context so that this warning wont be ignored in the further iterations.
                    updatedMessages.add(message);
                }
            }
            arServerContext.setLastStatus(updatedMessages);
        }
        return returnVal;
    }

    /**
     * Prepare qualification
     *
     * @return QualifierInfo
     */
    private QualifierInfo buildFieldValueQualification(int fieldId, Value value, int relationalOperation) {
        ArithmeticOrRelationalOperand leftOperand = new ArithmeticOrRelationalOperand(fieldId);
        ArithmeticOrRelationalOperand rightOperand = new ArithmeticOrRelationalOperand(value);
        RelationalOperationInfo relationalOperationInfo = new RelationalOperationInfo(relationalOperation, leftOperand,
                rightOperand);
        QualifierInfo qualification = new QualifierInfo(relationalOperationInfo);
        return qualification;
    }

    @Override
    public void logout(ARServerUser arServerContext) {
        arServerContext.logout();
        log.info("Logout successful from remedy server");
    }

    @Override
    public Map<Integer, Field> getFieldsMap(ARServerUser user, ARServerForm form) throws RemedyReadFailedException {
        Map<Integer, Field> fieldMap = new HashMap<>();
        log.debug("Getting field Item map ");
        List<Field> fieldList = null;
        try {
            fieldList = user.getListFieldObjects(form.toString());
        } catch (ARException e1) {
            throw new RemedyReadFailedException("List of fields could not be retrieved from AR Server." + e1.getMessage());
        }
        fieldList.forEach(fieldItem -> {
            try {
                fieldMap.put(fieldItem.getFieldID(), fieldItem);
            } catch (Exception e) {
                log.error("Creating field Map resulted into error, {}", e.getMessage());
            }
        });
        log.debug(" FieldMap has total {} entries", fieldMap.size());
        return fieldMap;
    }

}
