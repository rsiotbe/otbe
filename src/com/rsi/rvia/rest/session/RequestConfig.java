package com.rsi.rvia.rest.session;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constantes;

/**
 * Clase que contiene la información asoiada ala petición del usuario y que se utiliza para configurar el comportamiento
 * del resultado
 */
public class RequestConfig
{
    private static Logger pLog        = LoggerFactory.getLogger(RequestConfig.class);
    protected String      strLanguage = "";
    protected String      strNRBE     = "";

    public String getLanguage()
    {
        return strLanguage;
    }

    public String getNRBE()
    {
        return strNRBE;
    }

    /**
     * Constructor pasando parámetros
     * 
     * @param strLang
     *            String con el lenguaje, por defecto si viene vacio o a null se pondra es_ES
     * @param strNRBE
     *            String con el NRBE, por defecto si viene vacio o a null se pondre 0198
     */
    public RequestConfig(String strLang, String strNRBE)
    {
        pLog.debug("Se procede a cargar la configuración de la petición leyendo parámetros");
        if (strLang == null || strLang.trim().isEmpty())
        {
            this.strLanguage = Constantes.DEFAULT_LANGUAGE;
            pLog.warn("No se recibe parámetro de configuración de idioma, se coge por defecto español (es_ES)");
        }
        else
        {
            this.strLanguage = strLang;
        }
        if (strNRBE == null || strNRBE.trim().isEmpty())
        {
            this.strNRBE = Constantes.CODIGO_BANCO_COOPERATIVO_ESPANOL;
            pLog.warn("No se recibe parámetro de configuración de entidad, se coge por defecto Banco cooperativo (0198)");
        }
        else
        {
            this.strNRBE = strNRBE;
        }
    }

    /**
     * Constructor de la clase
     * 
     * @param request
     *            Objeto request recibido
     * @throws Exception
     */
    public RequestConfig(HttpServletRequest request) throws Exception
    {
        pLog.debug("Se procede a cargar la configuración de la petición leyendo objeto request");
        strLanguage = request.getParameter(Constantes.PARAM_LANG);
        if (strLanguage == null)
        {
            strLanguage = (String) request.getAttribute(Constantes.PARAM_LANG);
        }
        strNRBE = request.getParameter(Constantes.PARAM_NRBE);
        if (strNRBE == null)
        {
            strNRBE = (String) request.getAttribute(Constantes.PARAM_NRBE);
        }
        if (strLanguage == null || strLanguage.trim().isEmpty())
        {
            this.strLanguage = Constantes.DEFAULT_LANGUAGE;
            pLog.warn("No se recibe parámetro de configuración de idioma, se coge por defecto español (es_ES)");
        }
        if (strNRBE == null || strNRBE.trim().isEmpty())
        {
            this.strNRBE = Constantes.CODIGO_BANCO_COOPERATIVO_ESPANOL;
            pLog.warn("No se recibe parámetro de configuración de entidad, se coge por defecto Banco cooperativo (0198)");
        }
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuilder pSb = new StringBuilder();
        pSb.append("Language        :" + strLanguage + "\n");
        pSb.append("NRBE            :" + strNRBE + "\n");
        return pSb.toString();
    }
}
