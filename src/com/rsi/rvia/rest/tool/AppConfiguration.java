package com.rsi.rvia.rest.tool;

import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfiguration
{
    private static Logger pLog = LoggerFactory.getLogger(AppConfiguration.class);
    private Properties    pAppProperties;

    public AppConfiguration() throws IOException
    {
        pLog.info("Cargando configuración de entorno...");
        pAppProperties = new Properties();
        pAppProperties.load(AppConfiguration.class.getClassLoader().getResourceAsStream("/application.properties"));
    }

    public Properties getEnvProperties()
    {
        return pAppProperties;
    }
}
