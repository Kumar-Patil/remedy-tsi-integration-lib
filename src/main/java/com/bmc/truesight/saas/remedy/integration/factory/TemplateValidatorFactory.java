package com.bmc.truesight.saas.remedy.integration.factory;

import com.bmc.truesight.saas.remedy.integration.ApplicationType;
import com.bmc.truesight.saas.remedy.integration.TemplateValidator;
import com.bmc.truesight.saas.remedy.integration.exception.InitializationException;
import com.bmc.truesight.saas.remedy.integration.impl.PluginTemplateValidator;
import com.bmc.truesight.saas.remedy.integration.impl.ScriptTemplateValidator;
import com.bmc.truesight.saas.remedy.integration.util.Constants;
import com.bmc.truesight.saas.remedy.integration.util.StringUtil;

/**
 * This factory gives the instance of {@link TemplateValidator} based on the
 * {@link ApplicationType}.
 *
 * @author vitiwari
 */
public class TemplateValidatorFactory {

    /**
     * This is an static method to get an instance of {@link TemplateValidator}
     * based on {@link ApplicationType}
     *
     * @param type {@link ApplicationType} enum value
     * @return {@link TemplateValidator} Instance of {@link TemplateValidator}
     * @throws InitializationException In case of unsuccessful initialization
     * throws exception.
     */
    public static TemplateValidator getInstance(ApplicationType type) throws InitializationException {
        TemplateValidator validator = null;
        if (type == ApplicationType.PLUGIN) {
            validator = new PluginTemplateValidator();
        } else if (type == ApplicationType.SCRIPT) {
            validator = new ScriptTemplateValidator();
        } else {
            throw new InitializationException(StringUtil.format(Constants.FACTORY_INITIALIZATION_EXCEPTION, new Object[]{}));
        }
        return validator;
    }
}
