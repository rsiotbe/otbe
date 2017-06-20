package com.rsi.rvia.rest.security;

import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.ManageJWToken;
import com.rsi.rvia.rest.conector.RestRviaConnector;
import com.rsi.rvia.rest.error.exceptions.LogicalErrorException;
import com.rsi.rvia.rest.error.exceptions.SessionException;
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
    private MiqQuests               pMiqQuests;
    private String                  strJWT;
    private RequestConfigRvia       pRequestConfigRvia;
    public static final String      RURALVIA_NODE   = "node";
    public static final String      RURALVIA_COOKIE = "RVIASESION";
    public static final String      ISUM_SERVICE_ID = "isumServiceId";
    public static final String      TOKEN_ID        = "rviatk1";

    public IdentityProviderRVIASession(HttpServletRequest pRequest, MiqQuests pMiqQuests)
    {
        this.pRequest = pRequest;
        this.pMiqQuests = pMiqQuests;
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
    public HashMap<String, String> validateJWT(String jwt, String strTokenId) throws Exception
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
        if (!isRequestToLogin(this.pMiqQuests))
        {
            strJWT = pRequest.getHeader("Authorization");
            /* si el JWT no viene en la cabecera, se intenta buscar en la sesión del usuario */
            pLog.trace("JWT recuperado de la cabecera: " + strJWT);
            if (strJWT == null)
            {
                HttpSession pSession = pRequest.getSession(false);
                if (pSession != null)
                {
                    strJWT = (String) pSession.getAttribute("JWT");
                    pLog.trace("JWT recuperado de la sesión: " + strJWT);
                    if (strJWT != null && isRequestToLogin(this.pMiqQuests))
                    {
                        /* si la pagina es el login se guarda el jwt como atributo para recuperalo en la pagina jsp */
                        pRequest.setAttribute("JWT", strJWT);
                        pLog.trace("Se establece el valor recuperado como atributo de la request");
                    }
                }
                else
                {
                    pLog.trace("No existe sesión del usuario");
                }
            }
        }
        else
        {
            /* si la petición es de login se fuerza a regenerar el tojen */
            strJWT = null;
        }
        if (strJWT == null)
        {
            pLog.trace("El valor recibido de JWT es nulo, se intenta obtener siempre y cuando el path de la petición sea /login");
            if (isRequestToLogin(this.pMiqQuests))
            {
                pClaims = getUserInfo(pRequest);
                if (pClaims != null)
                {
                    strJWT = generateJWT(pClaims, TOKEN_ID);
                    HttpSession pSession = pRequest.getSession(true);
                    pSession.setAttribute("JWT", strJWT);
                    pRequest.setAttribute("JWT", strJWT);
                    pLog.trace("Se genera un nuevo token JWT y se guarda en sesión y en la request");
                }
                else
                {
                    // Login fallido
                    pLog.error("Se lanza una excepción de fallo de obtención de los parámetros de identificación de Rvia");
                    throw new LogicalErrorException(403, 9999, "Login failed", "Es necesario suministrar credenciales válidos para iniciar sesión", null);
                }
            }
            else
            {
                pLog.trace("Se intenta recuperar los datos de sesión del token antiguo. Esto debería ser a extinguir");
                pClaims = getUserInfoOldToken(pRequest);
                strJWT = generateJWT(pClaims, TOKEN_ID);
                HttpSession pSession = pRequest.getSession(true);
                pSession.setAttribute("JWT", strJWT);
                pRequest.setAttribute("JWT", strJWT);
                // TODO: descomentar esta dos siguientes lineas cualdo se desactive el metodo de token antiguo y borrar
                // las anteriores
                // pLog.error("Se lanza una excepción de fallo debido a que no esxiste JWT para una petición que no es /login");
                // throw new LogicalErrorException(403, 9999, "Login failed",
                // "Es necesario acceder por el path /login para poder validar su sesión", null);
            }
        }
        pClaims = validateJWT(strJWT, TOKEN_ID);
        if (pClaims == null)
        {
            pLog.error("Se genera un error de comprobación de JWT");
            throw new LogicalErrorException(401, 9999, "Unauthorized", "Sesión no válida", new Exception());
        }
        pLog.trace("Se procede a generar el objeto RequestConfigRvia");
        this.pRequestConfigRvia = new RequestConfigRvia(pClaims);
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
        String strSessionId = pRequest.getParameter(RURALVIA_COOKIE);
        String strIsumServiceID = pRequest.getParameter(ISUM_SERVICE_ID);
        HashMap<String, String> pHtReturn;
        String strParameters = "USUARIO;ENTID;PERUSU;idioma;canalAix;canal;IP";
        HashMap<String, String> pHtAux = InterrogateRvia.getParameterFromSession(RestRviaConnector.getRuralviaAddress(strNode), strSessionId, strParameters);
        /* se genera el objeto completo necesario apra poder generar el JWT */
        String[] strParamNames = strParameters.split(";");
        if (strParamNames[0] == null)
        {
            /* si la respuesta de ruralvia es una sesión invalida se genera un error */
            pLog.error("Los datos recibidos de ruralvia no son validos. Datos: " + pHtAux);
            throw new SessionException(401, 777777, "Error al generar el token de sesión", "Los datos proporcionados no son correctos", null);
        }
        pHtReturn = new HashMap<String, String>();
        pHtReturn.put(RequestConfigRvia.TokenKey.NODE.getValue(), strNode);
        pHtReturn.put(RequestConfigRvia.TokenKey.RVIASESION.getValue(), strSessionId);
        pHtReturn.put(RequestConfigRvia.TokenKey.RVIAUSERID.getValue(), pHtAux.get(strParamNames[0]));
        pHtReturn.put(RequestConfigRvia.TokenKey.NRBE.getValue(), pHtAux.get(strParamNames[1]));
        pHtReturn.put(RequestConfigRvia.TokenKey.ISUMUSERPROFILE.getValue(), pHtAux.get(strParamNames[1])
                + pHtAux.get(strParamNames[2]));
        pHtReturn.put(RequestConfigRvia.TokenKey.LANG.getValue(), pHtAux.get(strParamNames[3]));
        pHtReturn.put(RequestConfigRvia.TokenKey.CANALFRONT.getValue(), pHtAux.get(strParamNames[4]));
        pHtReturn.put(RequestConfigRvia.TokenKey.CANALHOST.getValue(), pHtAux.get(strParamNames[5]));
        pHtReturn.put(RequestConfigRvia.TokenKey.IP.getValue(), pHtAux.get(strParamNames[6]));
        pHtReturn.put(RequestConfigRvia.TokenKey.ISUMSERVICEID.getValue(), strIsumServiceID);
        pLog.trace("Valore obtenidos: " + pHtReturn);
        return pHtReturn;
    }

    /**
     * Proceso de obtención de datos de ruralvia utilizando los datos que llegan en la request
     * 
     * @param pRequest
     * @return HashMap con los campos del payload, o null si falló login
     * @throws Exception
     */
    private HashMap<String, String> getUserInfoOldToken(HttpServletRequest pRequest) throws Exception
    {
        // TODO: Este metodo del código ses necesario eliminarla cuando ruralvia ya no genere token, si no que
        // consuma la generaicón de JWT
        HashMap<String, String> pHtReturn;
        /*
         * se intenta obtener el token de sesión de la forma antigua
         */
        pLog.info("Se accede a leer el token de forma antigua");
        String strTokenReaded = pRequest.getParameter("token");
        if (strTokenReaded == null)
        {
            /* se comprueba si el token esta inicializado en la sesión de la aplicación */
            if (pRequest.getSession(false) == null || pRequest.getSession(false).getAttribute("token") == null)
            {
                throw new SessionException(401, 777777, "Error al generar el token de sesión", "No se han proporcionado datos necesarios para poder generar el token", null);
            }
            strTokenReaded = (String) pRequest.getSession(false).getAttribute("token");
            pLog.info("Se lee el token de la sesión del usuario. Token: " + strTokenReaded);
        }
        /* se reemplazan los caracteres espacios por mases, por si al viahar como url se han transformado */
        strTokenReaded = strTokenReaded.replace(" ", "+");
        pLog.debug("La información viene cifrada, se procede a descifrarla");
        /* se desencipta la información */
        String strCleanData = RviaConnectCipher.symmetricDecrypt(strTokenReaded, RviaConnectCipher.RVIA_CONNECT_KEY);
        if (strCleanData == null)
        {/* si se recibe null se intenta descifrar con el método antiguo */
            pLog.warn("Al intentar descifrar el token con el metodo nuevo AES/CBC/PKCS5Padding no se consigue nada, se intenta con el método antiguo AES");
            strCleanData = RviaConnectCipher.symmetricDecryptOld(strTokenReaded, RviaConnectCipher.RVIA_CONNECT_KEY);
        }
        pLog.debug("Contenido descifrado. Token: " + strCleanData);
        if (strCleanData == null)
        {
            throw new Exception("Error al recuperar la información del token antiguo de rvia");
        }
        pHtReturn = (HashMap<String, String>) Utils.queryStringToMap(strCleanData);
        // Se establece el token de datos recibido desde ruralvia como dato de sesión.
        HttpSession pSession = pRequest.getSession(true);
        pSession.setAttribute("token", strTokenReaded);
        pLog.trace("Valore obtenidos: " + pHtReturn);
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

    private boolean isRequestToLogin(MiqQuests pMiqQuests)
    {
        return ((this.pMiqQuests != null) && (this.pMiqQuests.getPathRest().indexOf("/login") != -1));
    }
}
