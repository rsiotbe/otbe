package com.rsi.rvia.rest.security;

import java.util.HashMap;

public interface IdentityProvider
{
    // public String generateJWT(HashMap<String, String> claims, String strTokenId) throws Exception;
    // public HashMap<String, String> validateJWT(String jwt, String strTokenId) throws Exception;
    // public HashMap<String, String> doLogin(HttpServletRequest pRequest) throws ClientProtocolException, IOException;
    public HashMap<String, String> getClaims();

    public String getJWT();

    public void process() throws Exception;
}
