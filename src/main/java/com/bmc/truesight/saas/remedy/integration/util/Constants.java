package com.bmc.truesight.saas.remedy.integration.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface Constants {

    String CONFIG_NODE_NAME = "config";
    String CONFIG_HOSTNAME_NODE_NAME = "remedyHostName";
    String CONFIG_PORT_NODE_NAME = "remedyPort";
    String CONFIG_USERNAME_NODE_NAME = "remedyUserName";
    String CONFIG_PASSWORD_NODE_NAME = "remedyPassword";
    String CONFIG_TSIENDPOINT_NODE_NAME = "tsiEventEndpoint";
    String CONFIG_TSITOKEN_NODE_NAME = "tsiApiToken";
    String CONFIG_CHUNKSIZE_NODE_NAME = "chunkSize";
    String CONFIG_THREADS_NODE_NAME = "threadCount";
    String CONFIG_CONDFIELDS_NODE_NAME = "conditionFields";
    String CONFIG_CONDSTATUSFIELDS_NODE_NAME = "queryStatusList";
    String CONFIG_RETRY_NODE_NAME = "retryConfig";
    String CONFIG_WAITSMS_NODE_NAME = "waitMsBeforeRetry";
    String EVENTDEF_NODE_NAME = "eventDefinition";
    List<String> FINGERPRINT_EVENT_FIELDS = new ArrayList<String>() {
        {
            add("source.name");
            add("title");
            add("status");
            add("severity");
            add("message");
        }
    };

    String PLACEHOLDER_START_TOKEN = "@";
    Integer CONFIG_CHUNK_SIZE = 1000;
    Integer MAX_PROPERTY_FIELD_SUPPORTED = 128;
    Long MAX_EVENT_SIZE_ALLOWED_BYTES = 32000l;
    int EVENT_INGESTION_STATE_SUCCESS = 200;
    int EVENT_INGESTION_STATE_ACCEPTED = 202;
    int UNAUTHORIZED_STATUS = 401;
    int EVENTASYNC_FIXED_THREAD_POOL = 10;

    String SPECIAL_CHARACTOR = "!#$%&'()*+,./:;<=>?@[]^`{|}~";
    //Messages
    String CONFIG_FILE_NOT_FOUND = "Could not read the configuration file from location({0}) or it has different encoding than UTF8";
    String CONFIG_FILE_NOT_VALID = "The configuration json is not a valid JSON,{0})";
    String CONFIG_PROPERTY_NOT_FOUND = "There is an issue with 'config' field in default json file. {0}";
    String CONFIG_PROPERTY_NOT_VALID = "Either the configuration file does not contain proper 'config' property, or 'fields' are not correct. {0}";
    String PAYLOAD_PROPERTY_NOT_FOUND = "Either the configuration file does not contain proper 'eventDefinition' property, or 'fields' are not correct. {0}";
    String PLACEHOLDER_PROPERTY_NOT_CORRECT = "The fields  for property {0} are not correct";
    String CONFIG_VALIDATION_FAILED = "The fields for config elements are empty, it should be nonempty";
    String PAYLOAD_PLACEHOLDER_DEFINITION_MISSING = "The definition for payload placeholder {0} is missing in the configuration file";
    String REMEDY_LOGIN_FAILED = "Login failed to Remedy Server, ({0})";
    String FACTORY_INITIALIZATION_EXCEPTION = "Failed to create proper instance in the factory, Please ensure you are using right parameters";
    String PROPERTY_NAME_INVALID = "The property \"{0}\" is not a valid Field Name, Only AlphaNumeric and Underscore are allowed characters in the Field Names.";
    String PROPERTY_FIELD_COUNT_EXCEEDS = "The number of properties captured(ie {0}) in the Remedy field mapping exceeds the maximum count of {1}, please review the field mapping and reduce the count for successful ingestion..";
    String DATERANGE_VALIDATION_FAILED = "Start date & End date in config is not appropriate, start date should not be greater than end date and end date should not be greater than current date.";
    String EVENT_FIELD_MISSING = "FingerPrintFields values are invalid, field \"{0}\" does not exist in Event fields. Accepted fields are {1}";
    String EVENT_PROPERTY_FIELD_MISSING = "FingerPrintFields values are invalid, field \"{0}\" does not exist in user fields (in properties of eventDefinition)";
    String APPLICATION_NAME_INVALID = "The application \"{0}\" is not valid , Only AlphaNumeric, Hyphen  and Underscore are allowed characters in the application name.";
    String PAYLOAD_PLACEHOLDER_FIELD_MISSING = "The field \"{0}\" does not exist in Template Configuration, please review the mapping";
    String APPLICATION_NAME_NOT_FOUND = "app_id field is missing from properties, please include one app_id field in properties";

}
