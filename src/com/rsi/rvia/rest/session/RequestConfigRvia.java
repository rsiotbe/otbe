package com.rsi.rvia.rest.session;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants.CanalFront;
import com.rsi.Constants.CanalHost;
import com.rsi.rvia.rest.error.exceptions.SessionException;

public class RequestConfigRvia extends RequestConfig
{
    private static Logger pLog               = LoggerFactory.getLogger(RequestConfigRvia.class);
    private String        strNodeRvia;
    private String        strRviaSessionId   = "";
    private String        strRviaUserId      = "";
    private String        strIsumUserProfile = "";
    private String        strIsumServiceId   = "";
    private String        strIp              = "";
    /* tabla belts104 */
    private CanalHost     pCanalHost         = CanalHost.BANCA_INTERNET;
    /* tabla belts100 */
    private CanalFront    pCanalFront        = CanalFront.WEB;

    public static enum TokenKey
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

    public String getIp()
    {
        return strIp;
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
        super((String) pTokenvalues.get(TokenKey.LANG.value), (String) pTokenvalues.get(TokenKey.NRBE.value));
        try
        {
            this.strNodeRvia = pTokenvalues.get(TokenKey.NODE.value);
            this.strRviaSessionId = pTokenvalues.get(TokenKey.RVIASESION.value);
            this.strRviaUserId = pTokenvalues.get(TokenKey.RVIAUSERID.value);
            this.strIsumUserProfile = pTokenvalues.get(TokenKey.ISUMUSERPROFILE.value);
            this.strIsumServiceId = pTokenvalues.get(TokenKey.ISUMSERVICEID.value);
            this.strIp = pTokenvalues.get(TokenKey.IP.value);
            this.pCanalFront = obtainCanalWebFromStringValue(pTokenvalues.get(TokenKey.CANALFRONT.value));
            this.pCanalHost = obtainCanalHostFromStringValue(pTokenvalues.get(TokenKey.CANALHOST.value));
        }
        catch (Exception ex)
        {
            throw new SessionException(500, 999999, "Error al obtener datos de sesion que provienen de Ruralvia", null, ex);
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
        pSb.append("RviaSessionId         :" + strRviaSessionId + "\n");
        pSb.append("RviaUserId            :" + strRviaUserId + "\n");
        pSb.append("IsumUserProfile       :" + strIsumUserProfile + "\n");
        pSb.append("Language              :" + pLanguage.getJavaCode() + "\n");
        pSb.append("NRBE                  :" + strNRBE + "\n");
        pSb.append("Ip                    :" + strIp + "\n");
        pSb.append("CanalFront (CanalAix) :" + pCanalFront.name() + "\n");
        pSb.append("CanalHost (Canal)     :" + pCanalHost.name() + "\n");
        return pSb.toString();
    }
}
