package com.rsi.rvia.rest.tool;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import com.rsi.Constants;
import com.rsi.rvia.rest.session.RequestConfig;

/** Clase destinada getters de parametos en el request. Busca un request determinado por funcion. */
public class BUSHeader
{
    public static final String NRBE_EVO                         = "0239";
    public static final String BUS_HEADER_COD_SEC_ENT_DEFAULT   = " ";
    public static final String BUS_HEADER_COD_SEC_TRANS_DEFAULT = " ";
    public static final String BUS_HEADER_COD_SEC_USER_DEFAULT  = " ";
    public static final String BUS_HEADER_COD_APL_DEFAULT       = "BDP";
    public static final String BUS_HEADER_COD_TERMINAL_DEFAULT  = " ";
    public static final String BUS_HEADER_COD_CANAL_DEFAULT     = "10";
    public static final String BUS_HEADER_COD_SEC_IP_RURAL      = "10.1.246.12";
    public static final String BUS_HEADER_COD_SEC_IP_EVO        = "10.1.245.2";

    /**
     * Genera un objeto MultivaluedMap con los valores de cabecera necesarios para invocar al bus
     * 
     * @param pRequest
     * @param pRequestConfig
     * @return String con parametro o el parametro por defecto.
     */
    public static MultivaluedMap<String, Object> getHeaders(HttpServletRequest pRequest, RequestConfig pRequestConfig)
    {
        MultivaluedHashMap<String, Object> pReturn = new MultivaluedHashMap<String, Object>();
        pReturn.putSingle(Constants.BUS_HEADER_COD_SEC_USER, getValueFromRequest(pRequest, Constants.BUS_HEADER_COD_SEC_USER, BUS_HEADER_COD_SEC_USER_DEFAULT));
        pReturn.putSingle(Constants.BUS_HEADER_COD_SEC_TRANS, getValueFromRequest(pRequest, Constants.BUS_HEADER_COD_SEC_TRANS, BUS_HEADER_COD_SEC_TRANS_DEFAULT));
        pReturn.putSingle(Constants.BUS_HEADER_COD_TERMINAL, getValueFromRequest(pRequest, Constants.BUS_HEADER_COD_TERMINAL, BUS_HEADER_COD_TERMINAL_DEFAULT));
        pReturn.putSingle(Constants.BUS_HEADER_COD_APL, getValueFromRequest(pRequest, Constants.BUS_HEADER_COD_APL, BUS_HEADER_COD_APL_DEFAULT));
        pReturn.putSingle(Constants.BUS_HEADER_COD_CANAL, getValueFromRequest(pRequest, Constants.BUS_HEADER_COD_CANAL, BUS_HEADER_COD_CANAL_DEFAULT));
        if (pRequestConfig != null)
        {
            pReturn.putSingle(Constants.BUS_HEADER_COD_SEC_ENT, getValueFromRequest(pRequest, Constants.BUS_HEADER_COD_SEC_ENT, pRequestConfig.getNRBE()));
            if (NRBE_EVO.equals(pRequestConfig.getNRBE()))
            {
                pReturn.putSingle(Constants.BUS_HEADER_COD_SEC_IP, getValueFromRequest(pRequest, Constants.BUS_HEADER_COD_SEC_IP, BUS_HEADER_COD_SEC_IP_EVO));
            }
            else
            {
                pReturn.putSingle(Constants.BUS_HEADER_COD_SEC_IP, getValueFromRequest(pRequest, Constants.BUS_HEADER_COD_SEC_IP, BUS_HEADER_COD_SEC_IP_RURAL));
            }
        }
        else
        {
            pReturn.putSingle(Constants.BUS_HEADER_COD_SEC_ENT, getValueFromRequest(pRequest, Constants.BUS_HEADER_COD_SEC_ENT, BUS_HEADER_COD_SEC_ENT_DEFAULT));
            pReturn.putSingle(Constants.BUS_HEADER_COD_SEC_IP, getValueFromRequest(pRequest, Constants.BUS_HEADER_COD_SEC_IP, BUS_HEADER_COD_SEC_IP_RURAL));
        }
        return pReturn;
    }

    /**
     * Obtiene un valor de la request y si no encuentra valor devuelve el valor por defecto pasado
     * 
     * @param pRequest
     * @param strParamName
     * @param strDefaultValue
     * @return
     */
    private static String getValueFromRequest(HttpServletRequest pRequest, String strParamName, String strDefaultValue)
    {
        String strReturn;
        strReturn = pRequest.getParameter(strParamName);
        if (strReturn == null)
        {
            strReturn = strDefaultValue;
        }
        return strReturn;
    }
}
