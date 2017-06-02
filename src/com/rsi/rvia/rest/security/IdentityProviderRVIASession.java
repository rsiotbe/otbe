package com.rsi.rvia.rest.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.client.ClientProtocolException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.ManageJWToken;
import com.rsi.rvia.rest.error.exceptions.LogicalErrorException;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.operation.info.InterrogateRvia;

/**
 * @class Gestor de validaciónd etoken desde ruralvia y tokens de autorización a partir de usuario obetnido en el token
 *        de Ruralvía
 */
public class IdentityProviderRVIASession implements IdentityProvider
{
    private static Logger           pLog            = LoggerFactory.getLogger(IdentityProviderRVIASession.class);
    private HttpServletRequest      _pRequest;
    private HashMap<String, String> _claims;
    private String                  _JWT;
    public static final String      RURALVIA_NODE   = "node";
    public static final String      RURALVIA_COOKIE = "RVIASESION";
    public static final String      TOKEN_ID        = "rviatk1";

    public IdentityProviderRVIASession(HttpServletRequest pRequest, MiqQuests pMiqQuests)
    {
        _pRequest = pRequest;
        _claims = null;
        _JWT = "";
    }

    /**
     * Generador de JWT
     * 
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
     * Validación de JWT y extraccíon de campos de payload
     * 
     * @param jwt
     * @param strTokenId
     * @return HashMap con los campos del payload
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
        _JWT = _pRequest.getHeader("Authorization");
        if (_JWT == null)
        {
            _claims = getUserInfo(_pRequest);
            if (_claims != null)
                _JWT = generateJWT(_claims, TOKEN_ID);
            else
            {
                // Login fallido
                pLog.error("Se lanza una excepción de fallo de obtención de los parámetros de identificación de Rvia");
                throw new LogicalErrorException(403, 9999, "Login failed", "Suministre credenciales válidas para iniciar sesión", new Exception());
            }
        }
        _claims = validateJWT(_JWT, TOKEN_ID);
        if (_claims == null)
        {
            pLog.error("Se genera un error de comprobación de JWT");
            throw new LogicalErrorException(401, 9999, "Unauthorized", "Sesión no válida", new Exception());
        }
    };

    /**
     * Proceso de obtenciónd e datos de ruralvia utilizando los datos que llegan en la request
     * 
     * @param pRequest
     * @return HashMap con los campos del payload, o null si falló login
     * @throws ClientProtocolException
     * @throws IOException
     */
    private HashMap<String, String> getUserInfo(HttpServletRequest pRequest) throws ClientProtocolException,
            IOException
    {
        String strNode = pRequest.getParameter(RURALVIA_NODE);
        String strRviaCookie = pRequest.getParameter(RURALVIA_COOKIE);
        Hashtable<String, String> pHtReturn;
        String strParameters = "USUARIO;ENTID;PERUSU;idioma;ENTID;canalAix;canal;IP";
        pHtReturn = InterrogateRvia.getParameterFromSession(strParameters, strRviaCookie, strNode);
        return new HashMap<String, String>(pHtReturn);
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

    public getRequestConfigRvia()
    {
        _claims
        
        pRequestConfigRvia
    }
}
