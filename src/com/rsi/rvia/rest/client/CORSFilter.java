package com.rsi.rvia.rest.client;

import java.io.IOException;
import java.util.Properties;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import com.rsi.Constantes;
import com.rsi.rvia.rest.tool.Utils;

@Provider
public class CORSFilter implements ContainerResponseFilter
{
    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext)
            throws IOException
    {
        Properties properties = Utils.getApplicationProperties();
        if (!properties.getProperty(Constantes.ENVIRONMENT).equals(Constantes.Environment.PROD.name()))
        {
            MultivaluedMap<String, Object> headers = responseContext.getHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST, DELETE, PUT");
            headers.add("Access-Control-Allow-Headers", "origin, content-type, accept, authorization");
        }
    }
}
