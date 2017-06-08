package com.bmc.truesight.saas.remedy.integration.util;

import java.text.MessageFormat;

/**
 * String utilities
 *
 * @author vitiwari
 *
 */
public class StringUtil {

    public static String format(String template, Object[] args) {
        MessageFormat fmt = new MessageFormat(template);
        return fmt.format(args);
    }
}
