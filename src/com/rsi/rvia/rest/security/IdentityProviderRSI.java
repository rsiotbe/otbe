package com.rsi.rvia.rest.security;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.ManageJWToken;
import com.rsi.rvia.rest.error.exceptions.LogicalErrorException;
import com.rsi.rvia.rest.operation.MiqQuests;

/**
 * @author zenhaust
 * @class Gestor de login y tokens de autorización a partir de usuarios existentes en LDAP, y válidos para acceso a
 *        Intranet
 */
public class IdentityProviderRSI implements IdentityProvider
{
    private static Logger           pLog = LoggerFactory.getLogger(IdentityProviderRSI.class);
    private HttpServletRequest      _pRequest;
    private HashMap<String, String> _claims;
    private String                  _JWT;
    private MiqQuests               _pMiqQuests;
    private HashMap<String, String> _pParamsToInject;
    private String                  _tokenId;

    /**
     * @param pRequest
     * @param pMiqQuests
     */
    public IdentityProviderRSI(HttpServletRequest pRequest, MiqQuests pMiqQuests)
    {
        _pRequest = pRequest;
        _pMiqQuests = pMiqQuests;
        _tokenId = "RSItk1";
        _claims = null;
        _JWT = "";
    }

    /**
     * @param claims
     * @param strTokenId
     * @return
     * @throws Exception
     */
    private String generateJWT(HashMap<String, String> claims, String strTokenId) throws Exception
    {
        return ManageJWToken.generateJWT(claims, strTokenId);
    };

    /**
     * @param jwt
     * @param strTokenId
     * @return
     * @throws Exception
     */
    private HashMap<String, String> validateJWT(String jwt, String strTokenId) throws Exception
    {
        return ManageJWToken.validateJWT(jwt, strTokenId);
    };

    /*
     * (non-Javadoc)
     * @see com.rsi.rvia.rest.security.IdentityProvider#process()
     */
    public void process() throws Exception
    {
        _claims = null;
        _JWT = "";
        // String strPrimaryPath = _pRequest.
        if (_pMiqQuests.getPathRest().indexOf("/login") != -1)
        {
            // Si es login generamos JWT
            _claims = doLogin(_pRequest);
            if (_claims != null)
                _JWT = generateJWT(_claims, _tokenId);
            else
            {
                // Login fallido
                throw new LogicalErrorException(403, 9999, "Login failed", "Suministre credenciales válidas para iniciar sesión", new Exception());
            }
        }
        else
        {
            // Else verificamos JWT
            _JWT = _pRequest.getHeader("Authorization");
        }
        _pParamsToInject = validateJWT(_JWT, _tokenId);
        if (_pParamsToInject == null)
        {
            throw new LogicalErrorException(401, 9999, "Unauthorized", "Sesión no válida", new Exception());
        }
        Cookie cookie = new Cookie(_tokenId, _JWT);
        cookie.setMaxAge(60 * 60); // 1 hour
        cookie.setPath("/api");
    };

    /**
     * Proceso de login según campos del request
     * 
     * @param pRequest
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    private HashMap<String, String> doLogin(HttpServletRequest pRequest) throws ClientProtocolException, IOException
    {
        HashMap<String, String> claims = null;
        String usuario = pRequest.getParameter("usuario");
        String password = pRequest.getParameter("password");
        String inLogin = "http://intranetrsi.caja.rural/names.nsf?Login&Username=" + usuario + "&Password=" + password;
        HttpGet httpGet = new HttpGet(inLogin);
        // httpGet.setEntity(stringEntity);
        httpGet.addHeader("Accept", "text/html");
        // Execute and get the response.
        HttpClient httpClient = HttpClientBuilder.create().build();
        // HttpClient httpClient = new HttpClient();
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String strResponse = null;
        String JWT = "";
        if (entity != null)
        {
            strResponse = EntityUtils.toString(entity);
        }
        int loginok = strResponse.indexOf("/intranet.nsf/index.htm");
        if (loginok >= 0)
        {
            claims = new HashMap<String, String>();
            claims.put("usuario", usuario);
        }
        return claims;
    }

    /*
     * (non-Javadoc)
     * @see com.rsi.rvia.rest.security.IdentityProvider#getClaims()
     */
    public HashMap<String, String> getClaims()
    {
        return _claims;
    };

    /*
     * (non-Javadoc)
     * @see com.rsi.rvia.rest.security.IdentityProvider#getJWT()
     */
    public String getJWT()
    {
        return _JWT;
    };
}
