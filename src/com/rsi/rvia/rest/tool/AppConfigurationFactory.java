package com.rsi.rvia.rest.tool;

import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase que retornará la configuración según el entorno */
public class AppConfigurationFactory
{
    private static Logger           pLog        = LoggerFactory.getLogger(AppConfigurationFactory.class);
    private static AppConfiguration eEnviroment = null;

    public static Properties getEnv() throws Exception
    {
        if (eEnviroment == null)
        {
            pLog.info("Instanciando configuración de entorno...");
            eEnviroment = new AppConfiguration();
        }
        return eEnviroment.getEnvProperties();
    }
}
