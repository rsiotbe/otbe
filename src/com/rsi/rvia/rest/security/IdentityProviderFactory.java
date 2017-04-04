package com.rsi.rvia.rest.security;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.operation.MiqQuests;

public class IdentityProviderFactory
{
    private static Logger pLog = LoggerFactory.getLogger(DDBBProvider.class);

    public enum IdProvider
    {
        RSI, RVIA, TRUSTED;
    }

    public static IdentityProvider getIdentityProvider(HttpServletRequest pRequest, MiqQuests pMiqQuests,
            IdProvider pIdProvider) throws Exception
    {
        IdentityProvider rIdentityProvider;
        pLog.debug("Retornando proveedor de identidad " + pIdProvider.name());
        switch (pIdProvider)
        {
            case RSI:
                rIdentityProvider = new IdentityProviderRVIA(pRequest, pMiqQuests);
                break;
            case RVIA:
                rIdentityProvider = new IdentityProviderRVIA(pRequest, pMiqQuests);
                break;
            case TRUSTED:
                rIdentityProvider = new IdentityProviderRVIA(pRequest, pMiqQuests);
                break;
            default:
                pLog.error("Proveedor de identidad no encontrado, no existe configuración para este proveedor. Proveedor:"
                        + pIdProvider.name());
                rIdentityProvider = null;
                break;
        }
        return rIdentityProvider;
    }
}
