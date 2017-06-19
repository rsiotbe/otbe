package com.rsi.rvia.rest.security;

import java.util.HashMap;

/**
 * @author zenhaust
 * @interface Interfaz de gestores de identidad
 */
public interface IdentityProvider
{
    /**
     * Recuperador de campos del JWT de un token válido.
     * 
     * @return campos de JWT
     */
    public HashMap<String, String> getClaims();

    /**
     * Recuperar el JWT
     * 
     * @return (String) JSON Web Token
     */
    public String getJWT();

    /**
     * Procesador de identidades
     * 
     * @throws Exception
     */
    public void process() throws Exception;
}
