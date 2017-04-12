package com.rsi.rvia.rest.security;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.operation.MiqQuests;

/**
 * @author zenhaust
 * @class Clase hueca para casos sin gestión de identidad.
 */
public class IdentityProviderTRUSTED implements IdentityProvider
{
    private static Logger pLog = LoggerFactory.getLogger(IdentityProviderTRUSTED.class);

    /**
     * @param pRequest
     * @param pMiqQuests
     */
    public IdentityProviderTRUSTED(HttpServletRequest pRequest, MiqQuests pMiqQuests)
    {
    }

    /*
     * (non-Javadoc)
     * @see com.rsi.rvia.rest.security.IdentityProvider#process()
     */
    public void process() throws Exception
    {
    };

    /*
     * (non-Javadoc)
     * @see com.rsi.rvia.rest.security.IdentityProvider#getClaims()
     */
    public HashMap<String, String> getClaims()
    {
        return new HashMap<String, String>();
    };

    /*
     * (non-Javadoc)
     * @see com.rsi.rvia.rest.security.IdentityProvider#getJWT()
     */
    public String getJWT()
    {
        return new String();
    };
}
