package com.rsi.rvia.rest.session;

import java.net.URI;
import java.util.Properties;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.error.exceptions.SessionException;
import com.rsi.rvia.rest.tool.RviaConnectCipher;

public class RequestConfigRvia extends RequestConfig
{
    private static Logger     pLog               = LoggerFactory.getLogger(RequestConfigRvia.class);
    private String            strNodeRvia;
    private Cookie[]          pCookiesRviaData;
    private static Properties pAddressRviaProp   = new Properties();
    private URI               pUriRvia           = null;
    private String            strRviaSessionId   = "";
    private String            strIsumUserProfile = "";
    private String            strIsumServiceId   = "";
    private String            strIp              = "";
    private String            strToken           = "";
    /* tabla belts104 */
    private CanalHost         pCanalHost         = CanalHost.BANCA_INTERNET;
    /* tabla belts100 */
    private CanalFront        pCanalFront        = CanalFront.WEB;

    private static enum TokenKey
    {
        NODE("node"), RVIASESION("RVIASESION"), ISUMUSERPROFILE("isumUserProfile"), ISUMSERVICEID("isumServiceId"), LANG(
                "lang"), NRBE("NRBE"), CANALAIX("canalAix"), CANAL("canal"), IP("ip");
        private String value;

        private TokenKey(String newValue)
        {
            value = newValue;
        }

        private static TokenKey getValue(String value)
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
    }

    /**
     * Enumeración de canal aix recibido desde la parte front de ruralvia, en ruralvia se denomina canalAix
     */
    public enum CanalFront
    {
        WEB(1), TABLET(6), MOVIL(7);
        private final int value;

        CanalFront(int newValue)
        {
            value = newValue;
        }

        public int getValue()
        {
            return value;
        }
    }

    /**
     * Enumeración de canal host recibido desde ruralvia, en ruralvia se denomina canal
     */
    public enum CanalHost
    {
        VALORES_BANCA_INTERNET(1), VALORES_BANCA_TELEFONICA(2), BANCA_INTERNET(3), BANCA_TELEFONICA(4), ABOGADOS(5), ABOGADOS_TELEFONICA(
                6), TPV_VIRTUAL(7), SEGUROS(8), OFICINA(9), TPV_VIRTUAL_TELEFONICA(10), BANCA_MOVIL(11), BANCA_TABLET(
                13), BANCA_TABLET_CAU(14);
        private final int value;

        CanalHost(int newValue)
        {
            value = newValue;
        }

        public int getValue()
        {
            return value;
        }
    }

    public String getNodeRvia()
    {
        return strNodeRvia;
    }

    public Cookie[] getCookiesRviaData()
    {
        return pCookiesRviaData;
    }

    public URI getUriRvia()
    {
        return pUriRvia;
    }

    public String getRviaSessionId()
    {
        return strRviaSessionId;
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
    public RequestConfigRvia(HttpServletRequest request) throws Exception
    {
        super(request);
        try
        {
            String[] strParameters;
            String strDesToken = "";
            pLog.debug("Se procede a cargar la configuración de la conexión con ruralvia");
            /* se comprueba si el contenido viene encriptado enel parámetro token */
            strToken = request.getParameter("token");
            if (strToken == null)
            {
                /* se comprueba si el token esta inicializado en la sesión de la aplicación */
                strToken = (String) request.getSession(false).getAttribute("token");
                pLog.info("Se lee el token de la sesión del usuario. Token: " + strToken);
            }
            if (strToken != null)
            {
                pLog.debug("La información viene cifrada, se procede a descifrarla");
                /* se desencipta la información */
                strDesToken = RviaConnectCipher.symmetricDecrypt(strToken, RviaConnectCipher.RVIA_CONNECT_KEY);
                pLog.debug("Contenido descifrado. Token: " + strDesToken);
                /* se obtienen las variables recibidas */
                strParameters = strDesToken.split("&");
                for (int i = 0; i < strParameters.length; i++)
                {
                    String[] strAux = strParameters[i].split("=");
                    TokenKey strName = TokenKey.getValue(strAux[0]);
                    String strValue = null;
                    if (strAux.length > 1)
                        strValue = strAux[1];
                    if (strValue != null)
                    {
                        switch (strName)
                        {
                            case NODE:
                                strNodeRvia = strValue;
                                break;
                            case RVIASESION:
                                strRviaSessionId = strValue;
                                break;
                            case ISUMUSERPROFILE:
                                strIsumUserProfile = new String(strValue);
                                break;
                            case ISUMSERVICEID:
                                strIsumServiceId = strValue;
                                break;
                            case LANG:
                                strLanguage = strValue;
                                break;
                            case NRBE:
                                strNRBE = strValue;
                                break;
                            case CANALAIX:
                                // Se buscan en todas los posibles valores de la enumeración
                                pCanalFront = obtainCanalWebFromStringValue(strValue);
                                break;
                            case CANAL:
                                // Se buscan en todas los posibles valores de la enumeración
                                pCanalHost = obtainCanalHostFromStringValue(strValue);
                                break;
                            case IP:
                                // Se buscan en todas los posibles valores de la enumeración
                                strIp = strValue;
                                break;
                            default:
                                // No hace nada.
                                break;
                        }
                    }
                }
            }
            else
            {
                /* se intenta leer la información sin cifrar */
                pLog.debug("La información no viene cifrada, se procede a leerla directamente de parámetros");
                strNodeRvia = request.getParameter("node");
                strRviaSessionId = request.getParameter("RVIASESION");
                strIsumUserProfile = request.getParameter("isumProfile");
                strIsumServiceId = request.getParameter("isumServiceId");
                strLanguage = request.getParameter("lang");
                strNRBE = request.getParameter("NRBE");
                strIp = request.getParameter("IP");
                pCanalFront = obtainCanalWebFromStringValue(request.getParameter("canalAix"));
            }
            pCookiesRviaData = request.getCookies();
            /* se precargan las propiedades de comunicación con RVIA */
            loadProperties();
        }
        catch (Exception ex)
        {
            throw new SessionException(500, 999999, "Error al obtener datos de sesion desde Ruralvia", strIsumServiceId, ex);
        }
    }

    /**
     * Carga las propiedades de ruralvia
     * 
     * @throws Exception
     */
    private void loadProperties() throws Exception
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
        pSb.append("IsumUserProfile       :" + strIsumUserProfile + "\n");
        pSb.append("Language              :" + strLanguage + "\n");
        pSb.append("NRBE                  :" + strNRBE + "\n");
        pSb.append("Token                 :" + strToken + "\n");
        pSb.append("CanalFront (CanalAix) :" + pCanalFront.name() + "\n");
        pSb.append("CanalHost (Canal)     :" + pCanalHost.name() + "\n");
        if (pCookiesRviaData != null)
        {
            pSb.append("Cookies         :\n");
            for (int i = 0; i < pCookiesRviaData.length; i++)
            {
                pSb.append("                 [" + (i) + "] " + pCookiesRviaData[i].getName() + " -> "
                        + pCookiesRviaData[i].getValue());
                if (i < pCookiesRviaData.length - 1)
                    pSb.append("\n");
            }
        }
        return pSb.toString();
    }
}
