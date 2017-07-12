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

    public final static boolean isValidJavaIdentifier(String s) {
        // an empty or null string cannot be a valid identifier
        if (s == null || s.length() == 0) {
            return false;
        }
        char[] c = s.toCharArray();
        if (!Character.isJavaIdentifierStart(c[0])) {
            return false;
        }
        for (int i = 1; i < c.length; i++) {
            if (!Character.isJavaIdentifierPart(c[i])) {
                return false;
            }
        }

        return true;
    }
}
