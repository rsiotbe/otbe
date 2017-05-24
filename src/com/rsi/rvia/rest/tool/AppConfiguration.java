package com.rsi.rvia.rest.tool;

import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppConfiguration
{
    private static Logger           pLog        = LoggerFactory.getLogger(AppConfiguration.class);
    private static AppConfiguration pInstance   = null;
    private Properties              pProperties = null;

    private AppConfiguration() throws IOException
    {
        pProperties = new Properties();
        pProperties.load(AppConfiguration.class.getClassLoader().getResourceAsStream("/application.properties"));
        pLog.info("Se cargan las propiedades generales de la aplaición");
    }

    public static AppConfiguration getInstance()
    {
        if (pInstance == null)
        {
            try
            {
                pInstance = new AppConfiguration();
                pLog.info("Se instancian la configuración general de la aplicación");
            }
            catch (IOException e)
            {
                pLog.error("Error instanciando la configuración general de la aplicación", e);
                pInstance = null;
            }
        }
        return pInstance;
    }

    public Properties getProperties()
    {
        return pProperties;
    }

    public String getProperty(String strKey)
    {
        String strValue;
        strValue = pProperties.getProperty(strKey);
        pLog.info("Se lee una propiedad la configuración general de la aplicación. " + strKey + ": " + strValue);
        return strValue;
    }
}
