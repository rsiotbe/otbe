package com.rsi.rvia.rest.tool;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase que retornará la configuración según el entorno */
public class AppConfigurationFactory
{
    private static Logger           pLog        = LoggerFactory.getLogger(AppConfigurationFactory.class);
    private static AppConfiguration eAppConfig = null;

    public static Properties getConfiguration() throws Exception
    {
        if (eAppConfig == null)
        {
            pLog.info("Instanciando configuración de entorno...");
            eAppConfig = new AppConfiguration();
        }
        return eAppConfig.getEnvProperties();
    }
}
