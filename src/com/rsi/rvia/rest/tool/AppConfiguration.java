package com.rsi.rvia.rest.tool;

import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfiguration
{
    private static Logger pLog = LoggerFactory.getLogger(AppConfiguration.class);
    private Properties    pEnvProperties;

    public void AppConfiguration() throws IOException
    {
        pLog.info("Cargando configuración de entorno...");
        pEnvProperties = new Properties();
        pEnvProperties.load(AppConfiguration.class.getResourceAsStream("/ApplicationProperties.properties"));
    }

    public Properties getEnvProperties()
    {
        return pEnvProperties;
    }
}
