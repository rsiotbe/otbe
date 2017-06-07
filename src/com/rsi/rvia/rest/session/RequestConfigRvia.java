package com.rsi.rvia.rest.session;

import java.net.URI;
import java.util.Map;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants.CanalFront;
import com.rsi.Constants.CanalHost;
import com.rsi.rvia.rest.error.exceptions.SessionException;
import com.rsi.rvia.rest.tool.RviaConnectCipher;
import com.rsi.rvia.rest.tool.Utils;

public class RequestConfigRvia extends RequestConfig
{
    private static Logger     pLog               = LoggerFactory.getLogger(RequestConfigRvia.class);
    private String            strNodeRvia;
    private static Properties pAddressRviaProp   = new Properties();
    private URI               pUriRvia           = null;
    private String            strRviaSessionId   = "";
    private String            strRviaUserId      = "";
    private String            strIsumUserProfile = "";
    private String            strIsumServiceId   = "";
    private String            strToken           = "";
    private String            strIp              = "";
    /* tabla belts104 */
    private CanalHost         pCanalHost         = CanalHost.BANCA_INTERNET;
    /* tabla belts100 */
    private CanalFront        pCanalFront        = CanalFront.WEB;

    private static enum TokenKey
    {
        NODE("node"), RVIASESION("RVIASESION"), RVIAUSERID("rviaUserId"), ISUMUSERPROFILE("isumUserProfile"), ISUMSERVICEID(
                "isumServiceId"), LANG("lang"), NRBE("NRBE"), CANALFRONT("canalAix"), CANALHOST("canal"), IP("ip");
        private String value;

        private TokenKey(String newValue)
        {
            value = newValue;
        }

        private static TokenKey getFromValue(String value)
        {
            for (TokenKey e : TokenKey.values())
            {
                if (e.value.equals(value))
                {
                    return e;
                }
            }
            return null;
        }

        public String getValue()
        {
            return value;
        }
    }

    public String getNodeRvia()
    {
        return strNodeRvia;
    }

    public URI getUriRvia()
    {
        return pUriRvia;
    }

    public String getRviaSessionId()
    {
        return strRviaSessionId;
    }

    public String getRviaUserId()
    {
        return strRviaUserId;
    }

    public String getIsumUserProfile()
    {
        return strIsumUserProfile;
    }

    public String getIsumServiceId()
    {
        return strIsumServiceId;
    }

    public CanalFront getCanalFront()
    {
        return pCanalFront;
    }

    public CanalHost getCanalHost()
    {
        return pCanalHost;
    }

    public String getToken()
    {
        return strToken;
    }

    public String getIp()
    {
        return strIp;
    }

    /**
     * Constructor de la clase
     * 
     * @param request
     *            Objeto request recibido
     * @throws Exception
     */
    public static RequestConfigRvia getInstance(HttpServletRequest request) throws Exception
    {
        RequestConfigRvia pReturn = null;
        ;
        try
        {
            String strTokenReaded;
            String strCleanData = "";
            pLog.debug("Se procede a cargar la configuración de la conexión con ruralvia");
            /* se comprueba si el contenido viene encriptado enel parámetro token */
            strTokenReaded = request.getParameter("token");
            if (strTokenReaded == null)
            {
                /* se comprueba si el token esta inicializado en la sesión de la aplicación */
                strTokenReaded = (String) request.getSession(false).getAttribute("token");
                pLog.info("Se lee el token de la sesión del usuario. Token: " + strTokenReaded);
            }
            /* se reemplazan los caracteres espacios por mases, por si al viahar como url se han transformado */
            strTokenReaded = strTokenReaded.replace(" ", "+");
            pLog.debug("La información viene cifrada, se procede a descifrarla");
            /* se desencipta la información */
            strCleanData = RviaConnectCipher.symmetricDecrypt(strTokenReaded, RviaConnectCipher.RVIA_CONNECT_KEY);
            /* si se recibe null se intenta descifrar con el método antiguo */
            pLog.warn("Al intentar descifrar el token con el metodo nuevo AES/CBC/PKCS5Padding no se consigue nada, se intenta con el método antiguo AES");
            strCleanData = RviaConnectCipher.symmetricDecryptOld(strTokenReaded, RviaConnectCipher.RVIA_CONNECT_KEY);
            pLog.debug("Contenido descifrado. Token: " + strCleanData);
            if (strCleanData == null)
            {
                throw new Exception("Error al recuperar la información del token");
            }
            /* se obtienen las variables recibidas */
            pReturn = new RequestConfigRvia(Utils.queryStringToMap(strCleanData));
            pReturn.strToken = strTokenReaded;
            pLog.debug("Objeto de configuación creado. RequestConfigRvia: " + pReturn);
        }
        catch (Exception ex)
        {
            throw new SessionException(500, 999999, "Error al obtener datos de sesion desde Ruralvia", null, ex);
        }
        return pReturn;
    }

    /**
     * Constructor de la clase
     * 
     * @param request
     *            Objeto request recibido
     * @throws SessionException
     * @throws Exception
     */
    public RequestConfigRvia(Map<String, String> pTokenvalues) throws SessionException
    {
        super((String) pTokenvalues.get(TokenKey.LANG), (String) pTokenvalues.get(TokenKey.NRBE));
        try
        {
            this.strNodeRvia = (String) pTokenvalues.get(TokenKey.NODE);
            this.strRviaSessionId = (String) pTokenvalues.get(TokenKey.RVIASESION);
            this.strRviaUserId = (String) pTokenvalues.get(TokenKey.RVIAUSERID);
            this.strIsumUserProfile = (String) pTokenvalues.get(TokenKey.ISUMUSERPROFILE);
            this.strIsumServiceId = (String) pTokenvalues.get(TokenKey.ISUMSERVICEID);
            this.strIp = (String) pTokenvalues.get(TokenKey.IP);
            this.pCanalFront = obtainCanalWebFromStringValue((String) pTokenvalues.get(TokenKey.CANALFRONT));
            this.pCanalHost = obtainCanalHostFromStringValue((String) pTokenvalues.get(TokenKey.CANALHOST));
            loadRuralviaAddressProperties();
        }
        catch (Exception ex)
        {
            throw new SessionException(500, 999999, "Error al obtener datos de sesion que provienen de Ruralvia", null, ex);
        }
    }

    /**
     * Carga las propiedades de ruralvia
     * 
     * @throws Exception
     */
    private void loadRuralviaAddressProperties() throws Exception
    {
        try
        {
            if (pAddressRviaProp.isEmpty())
            {
                try
                {
                    pAddressRviaProp.load(this.getClass().getResourceAsStream("/RuralviaAddress.properties"));
                    pLog.debug("Se carga el fichero de resolución de direcciones");
                }
                catch (Exception ex)
                {
                    pLog.error("Fallo al cargar las propiedades de conexión con ruralvia", ex);
                    throw ex;
                }
            }
            /* se obtiene la maquina y puerto en la que existe la sesión del usuario */
            if (strNodeRvia == null)
            {
                pLog.error("No se ha podido leer el parámetro nodo de ruralvia, esto va a generar un error al obtener el nodo origen de la petición");
            }
            pUriRvia = new URI(pAddressRviaProp.getProperty(strNodeRvia));
        }
        catch (Exception ex)
        {
            pLog.error("Error al obtener los datos de configuración original de la sessión de ruralvia", ex);
            throw ex;
        }
    }

    /**
     * Obtiene el valor de enumeración correspondiente al valor pasado
     * 
     * @param strValue
     *            Valor a buscar su representación en la enumeración
     * @return Valor de la enumeración
     */
    private CanalFront obtainCanalWebFromStringValue(String strValue)
    {
        /* por defecto se considera canal WEB */
        CanalFront pReturn = CanalFront.WEB;
        if (strValue != null && !strValue.trim().isEmpty())
        {
            /* se buscan en todas los posibles valores de la enumeración */
            int nValue = Integer.parseInt(strValue);
            for (CanalFront pCanal : CanalFront.values())
            {
                /* si encuentra la enumaración que me indica el valor recibido */
                if (pCanal.getValue() == nValue)
                {
                    pReturn = pCanal;
                    break;
                }
            }
        }
        return pReturn;
    }

    /**
     * Obtiene el valor de enumeración correspondiente al valor pasado
     * 
     * @param strValue
     *            Valor a buscar su representación en la enumeración
     * @return Valor de la enumeración
     */
    private CanalHost obtainCanalHostFromStringValue(String strValue)
    {
        /* por defecto se considera canal WEB */
        CanalHost pReturn = CanalHost.BANCA_INTERNET;
        if (strValue != null && !strValue.trim().isEmpty())
        {
            /* se buscan en todas los posibles valores de la enumeración */
            int nValue = Integer.parseInt(strValue);
            for (CanalHost pCanal : CanalHost.values())
            {
                /* si encuentra la enumaración que me indica el valor recibido */
                if (pCanal.getValue() == nValue)
                {
                    pReturn = pCanal;
                    break;
                }
            }
        }
        return pReturn;
    }

    /*
     * (non-Javadoc)
     * @see com.rsi.rvia.rest.session.RequestConfig#toString()
     */
    public String toString()
    {
        StringBuilder pSb = new StringBuilder();
        pSb.append("NodeRvia              :" + strNodeRvia + "\n");
        pSb.append("URI                   :" + pUriRvia + "\n");
        pSb.append("RviaSessionId         :" + strRviaSessionId + "\n");
        pSb.append("RviaUserId            :" + strRviaUserId + "\n");
        pSb.append("IsumUserProfile       :" + strIsumUserProfile + "\n");
        pSb.append("Language              :" + pLanguage.getJavaCode() + "\n");
        pSb.append("NRBE                  :" + strNRBE + "\n");
        pSb.append("Token                 :" + strToken + "\n");
        pSb.append("Ip                    :" + strIp + "\n");
        pSb.append("CanalFront (CanalAix) :" + pCanalFront.name() + "\n");
        pSb.append("CanalHost (Canal)     :" + pCanalHost.name() + "\n");
        return pSb.toString();
    }
}
