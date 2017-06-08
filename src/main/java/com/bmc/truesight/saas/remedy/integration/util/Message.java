package com.bmc.truesight.saas.remedy.integration.util;

public interface Message {

    String CONFIG_FILE_NOT_FOUND = "Could not read the configuration file from location({0}) or it has different encoding than UTF8";
    String CONFIG_FILE_NOT_VALID = "The configuration file ({0}) is not a valid JSON";
    String CONFIG_PROPERTY_NOT_FOUND = "Either the configuration file does not contain 'config' property, or fields are not correct";
    String PAYLOAD_PROPERTY_NOT_FOUND = "Either the configuration file does not contain 'payload' property, or fields are not correct";
    String PLACEHOLDER_PROPERTY_NOT_CORRECT = "The fields  for property {0} are not correct";
    String CONFIG_VALIDATION_FAILED = "The fields for config elements are empty, it should be nonempty";
    String PAYLOAD_PLACEHOLDER_DEFINITION_MISSING = "The definition for payload placeholder {0} is missing in the configuration file";
    String REMEDY_LOGIN_FAILED = "Login failed to Remedy Server, ({0})";
    String FACTORY_INITIALIZATION_EXCEPTION = "Failed to create proper instance in the factory, Please ensure you are using right parameters";

}
