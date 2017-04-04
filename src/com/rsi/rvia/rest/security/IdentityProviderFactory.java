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
        RSI, RVIA, TRUSTED;
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
        IdentityProvider rIdentityProvider;
        pLog.debug("Retornando proveedor de identidad: " + pMiqQuests.getIdProvider().name());
        switch (pMiqQuests.getIdProvider())
        {
            case RSI:
                rIdentityProvider = new IdentityProviderRVIA(pRequest, pMiqQuests);
                break;
            case RVIA:
                rIdentityProvider = new IdentityProviderRVIA(pRequest, pMiqQuests);
                break;
            case TRUSTED:
                rIdentityProvider = new IdentityProviderTRUSTED(pRequest, pMiqQuests);
                break;
            default:
                pLog.error("Proveedor de identidad no encontrado, no existe configuración para este proveedor. Proveedor:"
                        + pMiqQuests.getIdProvider().name());
                rIdentityProvider = null;
                break;
        }
        return rIdentityProvider;
    }
}
