package com.rsi.rvia.rest.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;

public class IdentityProviderFactory
{
    private static Logger pLog = LoggerFactory.getLogger(DDBBProvider.class);

    public enum IdProvider
    {
        RSI, RVIA, TRUSTED;
    }

    public static IdentityProvider getIdentityProvider(IdProvider pIdProvider) throws Exception
    {
        IdentityProvider rIdentityProvider;
        pLog.debug("Retornando proveedor de identidad " + pIdProvider.name());
        switch (pIdProvider)
        {
            case RSI:
                rIdentityProvider = new IdentityProviderRVIA();
                break;
            case RVIA:
                rIdentityProvider = new IdentityProviderRVIA();
                break;
            case TRUSTED:
                rIdentityProvider = new IdentityProviderRVIA();
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
