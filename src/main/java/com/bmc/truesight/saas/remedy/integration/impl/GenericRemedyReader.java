package com.bmc.truesight.saas.remedy.integration.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bmc.arsys.api.ARException;
import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.ArithmeticOrRelationalOperand;
import com.bmc.arsys.api.DataType;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.OutputInteger;
import com.bmc.arsys.api.QualifierInfo;
import com.bmc.arsys.api.RelationalOperationInfo;
import com.bmc.arsys.api.SortInfo;
import com.bmc.arsys.api.Timestamp;
import com.bmc.arsys.api.Value;
import com.bmc.truesight.saas.remedy.integration.ARServerForm;
import com.bmc.truesight.saas.remedy.integration.RemedyReader;
import com.bmc.truesight.saas.remedy.integration.adapter.RemedyEntryEventAdapter;
import com.bmc.truesight.saas.remedy.integration.beans.Event;
import com.bmc.truesight.saas.remedy.integration.beans.Template;
import com.bmc.truesight.saas.remedy.integration.exception.RemedyLoginFailedException;
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
    public List<Event> readRemedyTickets(ARServerUser arServerContext, ARServerForm formName, Template template, int startFrom, int chunkSize, OutputInteger recordsCount, RemedyEntryEventAdapter adapter) {
        //keeping as set to avoid duplicates
        Set<Integer> fieldsList = new HashSet<>();
        template.getFieldItemMap().values().forEach(fieldItem -> {
            fieldsList.add(fieldItem.getFieldId());
        });

        int[] queryFieldsList = new int[fieldsList.size()];
        int index = 0;
        for (Integer i : fieldsList) {
            queryFieldsList[index++] = i;
        }

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

        List<SortInfo> sortOrder = new ArrayList<SortInfo>();
        List<Entry> entryList = new ArrayList<>();
        boolean isSuccessful = false;
        int retryCount = 0;
        while (!isSuccessful && retryCount <= template.getConfig().getRetryConfig()) {
            try {
                entryList = arServerContext.getListEntryObjects(formName.toString(), qualInfoF,
                        startFrom, chunkSize, sortOrder, queryFieldsList, false, recordsCount);
                isSuccessful = true;
                log.info("Recieved {} tickets  for starting index : {}, chunk size {}  ", new Object[]{entryList.size(), startFrom, chunkSize});
            } catch (ARException e) {
                if (retryCount < template.getConfig().getRetryConfig()) {
                    retryCount++;
                    log.error("Reading  {} tickets from {} resulted into exception[{}], Re-trying for {} time", new Object[]{chunkSize, startFrom, e.getMessage(), retryCount});
                    try {
                        log.error("Waiting for {} sec before trying again ......", (template.getConfig().getWaitMsBeforeRetry() / 1000));
                        Thread.sleep(template.getConfig().getWaitMsBeforeRetry());
                    } catch (InterruptedException e1) {
                    }

                    continue;
                } else {
                    log.error("Skipping the read process, Reading tickets Failed for starting : {}, chunk size {} even after retrying for {} times", new Object[]{startFrom, chunkSize, retryCount});
                    break;
                }
            }
        }
        List<Event> payloadList = new ArrayList<Event>();
        entryList.forEach(entry -> {
            payloadList.add(adapter.convertEntryToEvent(template, entry));
        });

        return payloadList;
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

}
