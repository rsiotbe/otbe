package com.rsi.rvia.rest.security;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.ManageJWToken;
import com.rsi.rvia.rest.error.exceptions.LogicalErrorException;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.operation.info.InterrogateRvia;
import com.rsi.rvia.rest.session.RequestConfigRvia;
import com.rsi.rvia.rest.tool.RviaConnectCipher;
import com.rsi.rvia.rest.tool.Utils;

/**
 * @class Gestor de validaciónd etoken desde ruralvia y tokens de autorización a partir de usuario obetnido en el token
 *        de Ruralvía
 */
public class IdentityProviderRVIASession implements IdentityProvider
{
    private static Logger           pLog            = LoggerFactory.getLogger(IdentityProviderRVIASession.class);
    private HttpServletRequest      pRequest;
    private HashMap<String, String> pClaims;
    private String                  strJWT;
    private RequestConfigRvia       pRequestConfigRvia;
    public static final String      RURALVIA_NODE   = "node";
    public static final String      RURALVIA_COOKIE = "RVIASESION";
    public static final String      TOKEN_ID        = "rviatk1";

    public IdentityProviderRVIASession(HttpServletRequest pRequest, MiqQuests pMiqQuests)
    {
        this.pRequest = pRequest;
        pClaims = null;
        strJWT = "";
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
        pClaims = null;
        strJWT = pRequest.getHeader("Authorization");
        /* si el JWT no viene en la cabecera, se intenta buscar en la sesión del usuario */
        if (strJWT == null)
        {
            strJWT = pRequest.getHeader("Authorization");
            HttpSession pSession = pRequest.getSession(false);
            if (pSession != null)
            {
                strJWT = (String) pSession.getAttribute("JWT");
            }
        }
        if (strJWT == null)
        {
            pClaims = getUserInfo(pRequest);
            if (pClaims != null)
            {
                strJWT = generateJWT(pClaims, TOKEN_ID);
                HttpSession pSession = pRequest.getSession(true);
                pSession.setAttribute("JWT", strJWT);
            }
            else
            {
                // Login fallido
                pLog.error("Se lanza una excepción de fallo de obtención de los parámetros de identificación de Rvia");
                throw new LogicalErrorException(403, 9999, "Login failed", "Suministre credenciales válidas para iniciar sesión", new Exception());
            }
        }
        pClaims = validateJWT(strJWT, TOKEN_ID);
        if (pClaims == null)
        {
            pLog.error("Se genera un error de comprobación de JWT");
            throw new LogicalErrorException(401, 9999, "Unauthorized", "Sesión no válida", new Exception());
        }
        pRequestConfigRvia = new RequestConfigRvia(pClaims);
    };

    /**
     * Proceso de obtención de datos de ruralvia utilizando los datos que llegan en la request
     * 
     * @param pRequest
     * @return HashMap con los campos del payload, o null si falló login
     * @throws Exception
     */
    private HashMap<String, String> getUserInfo(HttpServletRequest pRequest) throws Exception
    {
        String strNode = pRequest.getParameter(RURALVIA_NODE);
        String strRviaCookie = pRequest.getParameter(RURALVIA_COOKIE);
        HashMap<String, String> pHtReturn;
        String strParameters = "USUARIO;ENTID;PERUSU;idioma;ENTID;canalAix;canal;IP";
        /*
         * si no se reciben los parameñtros para interrogar a rvia, se intenta obtener el token de sesión de la fomra
         * antigua
         */
        if (strNode != null && strRviaCookie != null)
        {
            pHtReturn = InterrogateRvia.getParameterFromSession(strNode, strRviaCookie, strParameters);
        }
        else
        {
            // TODO: Esta parte del código ses necesario eliminarla cuando ruralvia ya no gnere token, si no que consuma
            // la generaicón de JWT
            pLog.info("Se accede a leer el token de forma antigua");
            pRequestConfigRvia = RequestConfigRvia.getInstance(pRequest);
            String strTokenReaded = pRequest.getParameter("token");
            if (strTokenReaded == null)
            {
                /* se comprueba si el token esta inicializado en la sesión de la aplicación */
                strTokenReaded = (String) pRequest.getSession(false).getAttribute("token");
                pLog.info("Se lee el token de la sesión del usuario. Token: " + strTokenReaded);
            }
            /* se reemplazan los caracteres espacios por mases, por si al viahar como url se han transformado */
            strTokenReaded = strTokenReaded.replace(" ", "+");
            pLog.debug("La información viene cifrada, se procede a descifrarla");
            /* se desencipta la información */
            String strCleanData = RviaConnectCipher.symmetricDecrypt(strTokenReaded, RviaConnectCipher.RVIA_CONNECT_KEY);
            /* si se recibe null se intenta descifrar con el método antiguo */
            pLog.warn("Al intentar descifrar el token con el metodo nuevo AES/CBC/PKCS5Padding no se consigue nada, se intenta con el método antiguo AES");
            strCleanData = RviaConnectCipher.symmetricDecryptOld(strTokenReaded, RviaConnectCipher.RVIA_CONNECT_KEY);
            pLog.debug("Contenido descifrado. Token: " + strCleanData);
            if (strCleanData == null)
            {
                throw new Exception("Error al recuperar la información del token antiguo de rvia");
            }
            pHtReturn = (HashMap<String, String>) Utils.queryStringToMap(strCleanData);
            // Se establece el token de datos recibido desde ruralvia como dato de sesión.
            HttpSession pSession = pRequest.getSession(true);
            pSession.setAttribute("token", pRequestConfigRvia.getToken());
        }
        return pHtReturn;
    }

    /*
     * (non-Javadoc)
     * @see com.rsi.rvia.rest.security.IdentityProvider#getClaims()
     */
    public HashMap<String, String> getClaims()
    {
        return pClaims;
    };

    /*
     * (non-Javadoc)
     * @see com.rsi.rvia.rest.security.IdentityProvider#getJWT()
     */
    public String getJWT()
    {
        return strJWT;
    };

    public RequestConfigRvia getRequestConfigRvia()
    {
        return pRequestConfigRvia;
    }
}
