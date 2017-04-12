package com.rsi.rvia.rest.security;

import java.util.HashMap;

/**
 * @author zenhaust
 * @interface Interfaz de gestores de identidad
 */
public interface IdentityProvider
{
    // public String generateJWT(HashMap<String, String> claims, String strTokenId) throws Exception;
    // public HashMap<String, String> validateJWT(String jwt, String strTokenId) throws Exception;
    // public HashMap<String, String> doLogin(HttpServletRequest pRequest) throws ClientProtocolException, IOException;
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
