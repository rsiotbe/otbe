package com.rsi.rvia.rest.security;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.client.ClientProtocolException;

public interface IdentityProvider
{
    public String generateJWT(HashMap<String, String> claims, String strTokenId) throws Exception;

    public HashMap<String, String> validateJWT(String jwt, String strTokenId) throws Exception;

    public HashMap<String, String> doLogin(HttpServletRequest pRequest) throws ClientProtocolException, IOException;
}
