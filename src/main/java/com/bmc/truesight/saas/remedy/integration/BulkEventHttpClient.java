package com.bmc.truesight.saas.remedy.integration;

import java.util.List;

import com.bmc.truesight.saas.remedy.integration.beans.Result;
import com.bmc.truesight.saas.remedy.integration.beans.TSIEvent;
import com.bmc.truesight.saas.remedy.integration.exception.BulkEventsIngestionFailedException;
import com.bmc.truesight.saas.remedy.integration.exception.TsiAuthenticationFailedException;

/**
 * This class sends the lists of events to TSI.
 *
 * @author vitiwari
 */
public interface BulkEventHttpClient {

    Result pushBulkEventsToTSI(List<TSIEvent> bulkEvents) throws BulkEventsIngestionFailedException, TsiAuthenticationFailedException;

}
