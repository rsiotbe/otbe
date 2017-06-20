package com.rsi.rvia.rest.security;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.operation.MiqQuests;

/**
 * @author zenhaust
 * @class Factory proveedor de gestor de identidad
 */
public class IdentityProviderFactory
{
    private static Logger pLog = LoggerFactory.getLogger(DDBBProvider.class);

    public enum IdProvider
    {
        RSI, RVIA, RVIASESSION, TRUSTED;
    }

    /**
     * @param pRequest
     * @param pMiqQuests
     * @param pIdProvider
     * @return Instancia de proveedor de identidad
     * @throws Exception
     */
    public static IdentityProvider getIdentityProvider(HttpServletRequest pRequest, MiqQuests pMiqQuests)
            throws Exception
    {
        IdentityProvider pIdentityProvider;
        pLog.debug("Retornando proveedor de identidad: " + pMiqQuests.getIdProvider().name());
        switch (pMiqQuests.getIdProvider())
        {
            case RSI:
                pIdentityProvider = new IdentityProviderRSI(pRequest, pMiqQuests);
                break;
            case RVIA:
                pIdentityProvider = new IdentityProviderRVIALogin(pRequest, pMiqQuests);
                break;
            case RVIASESSION:
                pIdentityProvider = new IdentityProviderRVIASession(pRequest, pMiqQuests);
                break;
            case TRUSTED:
                pIdentityProvider = new IdentityProviderTRUSTED(pRequest, pMiqQuests);
                break;
            default:
                pLog.error("Proveedor de identidad no encontrado, no existe configuración para este proveedor. Proveedor:"
                        + pMiqQuests.getIdProvider().name());
                pIdentityProvider = null;
                break;
        }
        return pIdentityProvider;
    }
}
