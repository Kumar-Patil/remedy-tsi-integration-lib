package com.bmc.truesight.saas.remedy.integration;

import java.util.List;

import com.bmc.arsys.api.ARServerUser;
import com.bmc.arsys.api.Entry;
import com.bmc.arsys.api.OutputInteger;
import com.bmc.truesight.saas.remedy.integration.adapter.RemedyEntryEventAdapter;
import com.bmc.truesight.saas.remedy.integration.beans.Template;
import com.bmc.truesight.saas.remedy.integration.beans.TSIEvent;
import com.bmc.truesight.saas.remedy.integration.exception.RemedyLoginFailedException;
import com.bmc.truesight.saas.remedy.integration.exception.RemedyReadFailedException;

/**
 * This interface defines the methods required for reading Remedy Entries as TSI
 * events .
 *
 * @author vitiwari
 *
 */
public interface RemedyReader {

    /**
     * Creates an instance of {@link ARServerUser}, Which is required in all
     * other methods (ex login, Read Remedy Tickets etc) .
     *
     * @param hostName Remedy host name
     * @param port Remedy port ( Not required keep it as null if you don't want
     * to specifically assign)
     * @param userName Remedy Server userId
     * @param password Remedy Server password
     * @return {@link ARServerUser} {@link ARServerUser} Instance
     */
    ARServerUser createARServerContext(String hostName, Integer port, String userName, String password);

    /**
     * This function logins {@link ARServerUser}, it is required to login before
     * calling readRemedyTickets
     *
     * @param arServerContext Instance of {@link ARServerUser}
     * @return true Returns true in case of successful login
     * @throws RemedyLoginFailedException In case of unsuccessful login throws
     * exception
     */
    boolean login(ARServerUser arServerContext) throws RemedyLoginFailedException;

    /**
     * This method reads a no of Entries(defined by parameter startFrom and
     * chunkSize) from ARServer. Ensure that {@link ARServerUser} is logged in
     * before this call. After first call of this method {@link OutputInteger}
     * instance in parameter will hold the total no of records based on the
     * configuration provided in the template. This can be used to properly
     * batch the request.
     *
     * @param arServerContext An instance of ARServerUser
     * @param formName It can be {@link ARServerForm} enum value
     * @param template It is an instance of {@link Template}, recieved from
     * TemplateParser
     * @param startFrom This parameter defines the offset of the records reading
     * @param chunkSize This parameter defines the no of records returned in
     * this call.
     * @param recordsCount This is an Instance of {@link OutputInteger}, Which
     * will hold total the no of records matching.
     * @param adapter This is an instance of {@link RemedyEntryEventAdapter},
     * Which converts the {@link Entry} object received from ARserver to
     * {@link TSIEvent} Objects
     * @return {@link List} It returns the list of {@link TSIEvent} (from
     * startFrom parameter to startFrom+chunkSize index)
     * @throws RemedyReadFailedException 
     */
    List<TSIEvent> readRemedyTickets(ARServerUser arServerContext, ARServerForm formName, Template template, int startFrom,
            int chunkSize, OutputInteger recordsCount, RemedyEntryEventAdapter adapter) throws RemedyReadFailedException;

    /**
     * This method returns a boolean value suggesting if the messages contained
     * any warning about exceeding max record request limit.
     *
     * @param arServerContext
     * @return true/false
     */
    public boolean exceededMaxServerEntries(ARServerUser arServerContext);

    /**
     * This method logout the {@link ARServerUser} from the ARServer. Make sure
     * you call this method to logout the user, once you are done with reading
     * Remedy Tickets.
     *
     * @param arServerContext {@link ARServerUser} instance.
     */
    void logout(ARServerUser arServerContext);

}
