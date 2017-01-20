package com.rsi.rvia.rest.client;

import java.util.Properties;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants;
import com.rsi.rvia.rest.tool.AppConfigurationFactory;

@Provider
public class CORSFilter implements ContainerResponseFilter
{
    private static Logger pLog = LoggerFactory.getLogger(CORSFilter.class);

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
    {
        Properties properties;
        try
        {
            properties = AppConfigurationFactory.getConfiguration();
            if (!properties.getProperty(Constants.ENVIRONMENT).equals(Constants.Environment.PROD.name()))
            {
                MultivaluedMap<String, Object> headers = responseContext.getHeaders();
                headers.add("Access-Control-Allow-Origin", "*");
                headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
                headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
            }
        }
        catch (Exception e)
        {
            pLog.error("Error al convertir un hashtable a String", e);
        }
    }
}
