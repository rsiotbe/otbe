package com.rsi.rvia.rest.session;

import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants;
import com.rsi.Constants.Language;

/**
 * Clase que contiene la información asoiada ala petición del usuario y que se utiliza para configurar el comportamiento
 * del resultado
 */
public class RequestConfig
{
    private static Logger pLog = LoggerFactory.getLogger(RequestConfig.class);
    protected Language    pLanguage;
    protected String      strNRBE;

    public Language getLanguage()
    {
        return pLanguage;
    }

    public String getNRBE()
    {
        return strNRBE;
    }

    /**
     * Constructor por defecto, no se debrái utilizar salvo para casos puntuales dodne no se ha recibido la información
     * del usuario
     */
    public static RequestConfig getDefaultRequestConfig()
    {
        pLog.debug("Se procede a cargar la configuración de la petición con los parámetros por defecto");
        return new RequestConfig(null, null);
    }

    /**
     * Constructor pasando objeto request
     * 
     * @param pRequest
     *            Objeto request recibido
     * @throws Exception
     */
    public static RequestConfig getRequestConfig(HttpServletRequest pRequest, String strJsonData) throws Exception
    {
        String strLangValue;
        String strNRBE;
        JSONObject pJSONObject = null;
        pLog.debug("Se procede a intanciar un objeto RequestConfig leyendo configuración de la petición leyendo objeto request o de datos json asociados");
        if (strJsonData != null && !strJsonData.isEmpty())
        {
            pJSONObject = new JSONObject(strJsonData);
        }
        strLangValue = pRequest.getParameter(Constants.PARAM_LANG);
        /* si el parámetro está vacio se intenta leer del contedio json */
        if (strLangValue == null && pJSONObject != null)
        {
            strLangValue = pJSONObject.optString(Constants.PARAM_LANG);
        }
        strNRBE = pRequest.getParameter(Constants.PARAM_NRBE);
        /* si el parámetro está vacio se intenta leer del contedio json */
        if (strNRBE == null && pJSONObject != null)
        {
            strNRBE = pJSONObject.optString(Constants.PARAM_NRBE);
        }
        return new RequestConfig(strLangValue, strNRBE);
    }

    /**
     * Constructor pasando parámetros
     * 
     * @param strLang
     *            String con el lenguaje, por defecto si viene vacio o a null se pondra es_ES
     * @param strNRBE
     *            String con el NRBE, por defecto si viene vacio o a null se pondre 0198
     * @param strAppName
     *            String con el nombre del aplicativo que responde a la petición
     */
    protected RequestConfig(String strLang, String strNRBE)
    {
        if (strLang == null || strLang.trim().isEmpty())
        {
            this.pLanguage = Constants.DEFAULT_LANGUAGE;
            pLog.warn("No se recibe parámetro de configuración de idioma, se coge por defecto español (es_ES)");
        }
        else
        {
            this.pLanguage = Language.getEnumValue(strLang);
        }
        if (strNRBE == null || strNRBE.trim().isEmpty())
        {
            this.strNRBE = Constants.CODIGO_ENTIDAD_FORMACION;
            pLog.warn("No se recibe parámetro de configuración de entidad, se coge por defecto etidad formación (9997)");
        }
        else
        {
            this.strNRBE = strNRBE;
        }
        pLog.info("Valores cargados. strLanguage: " + this.pLanguage.getJavaCode() + " - strNRBE: " + this.strNRBE);
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuilder pSb = new StringBuilder();
        pSb.append("Language        :" + pLanguage.getJavaCode() + "\n");
        pSb.append("NRBE            :" + strNRBE + "\n");
        return pSb.toString();
    }
}
