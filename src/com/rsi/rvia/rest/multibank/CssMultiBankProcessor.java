package com.rsi.rvia.rest.multibank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.session.RequestConfig;
import com.rsi.rvia.rest.tool.AppConfiguration;
import com.rsi.rvia.rest.tool.Utils;

/** Clase que gestiona el los CSS de multientidad para adaptar el estilo de la web. */
public class CssMultiBankProcessor
{
    private static Logger                   pLog        = LoggerFactory.getLogger(CssMultiBankProcessor.class);
    public static Hashtable<String, String> htCacheData = new Hashtable<String, String>();

    /**
     * Devuelve el tamaño de la cache
     * 
     * @return int con el tamaño de la cache
     */
    public static int getCacheSize()
    {
        int nReturn = 0;
        if (htCacheData != null)
        {
            nReturn = htCacheData.size();
        }
        return nReturn;
    }

    /** Reinicia la Cache */
    public static void resetCache()
    {
        if (htCacheData != null)
        {
            htCacheData = new Hashtable<String, String>();
        }
    }

    /**
     * Devuelve los datos de la cache en formato texto
     * 
     * @return Contenido de la caché
     * @throws Exception
     */
    public static String cacheToString() throws Exception
    {
        String strReturn;
        strReturn = Utils.hastablePrettyPrintHtml(htCacheData);
        return strReturn;
    }

    /**
     * Funcion que carga la cache desde base de datos
     * 
     * @throws Exception
     */
    private static void loadDDBBCache() throws Exception
    {
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        try
        {
            String strQuery = "SELECT * from " + AppConfiguration.getInstance().getProperty("BELScheme").trim()
                    + ".bdptb229_css_multibank";
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pResultSet = pPreparedStatement.executeQuery();
            while (pResultSet.next())
            {
                String strLinkRvia = (String) pResultSet.getString("RURALVIA");
                String strNRBE = (String) pResultSet.getString("NRBE");
                String strNewLink = (String) pResultSet.getString("VALUE");
                String strKey = strNRBE + "_" + strLinkRvia;
                if (!htCacheData.containsKey(strKey))
                    htCacheData.put(strKey, strNewLink);
            }
            pLog.debug("Se carga la cache de CssMultiBank con " + getCacheSize() + " elementos");
        }
        catch (Exception ex)
        {
            pLog.error("Error al realizar la consulta a la BBDD.");
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
        }
    }

    /**
     * Devuelve el valor de reemplazo del link css y si no lo encuentra devuelve el propio valor pasado
     * 
     * @param strNRBE
     *            Código de entidad
     * @param strCSSLink
     *            Link css a convertir
     * @return
     */
    private static String getLinkConversion(String strNRBE, String strCSSLink)
    {
        String strReturn;
        String strKey = strNRBE + "_" + strCSSLink;
        pLog.debug("Se solicita la key " + strKey);
        if (htCacheData.containsKey(strKey))
        {
            strReturn = htCacheData.get(strKey);
            pLog.debug("Se recupera el dato de la caché");
        }
        else
        {
            pLog.debug("No se encuentra el dato en caché");
            strReturn = null;
        }
        return strReturn;
    }

    /**
     * Función Principal, recibe el XHTML y la entidad y realiza las conversiones
     * 
     * @param pDocument
     *            Documento html en jsoup
     * @param pRequestConfig
     *            Datos de sesión de ruralvia para el usuario
     * @return String con el HTML con los reemplazos de css ya realizados.
     * @throws Exception
     */
    public static Document processXHTML(Document pDocument, RequestConfig pRequestConfig) throws Exception
    {
        pLog.debug("Se procede a modificar los enlaces css si es necesario");
        pDocument = adjustCSSLink(pDocument, pRequestConfig.getNRBE());
        return pDocument;
    }

    /**
     * Sincroniza el acceso a la carga de la cache de miqquest
     * 
     * @throws Exception
     */
    private static synchronized void synchronizeLoadCache() throws Exception
    {
        if (getCacheSize() == 0)
        {
            loadDDBBCache();
        }
    }

    /**
     * Función que modifica los links css de un documento JSOUP.
     * 
     * @param doc
     *            Document(Jsoup) a modificar.
     * @param htData
     *            Hashtable con los enlaces a convertir y sus conversiones.
     * @param strNRBE
     *            String con el idioma al que se quiere traducir.
     * @return Document(Jsoup) con la traducción ya puesta.
     * @throws Exception
     */
    private static Document adjustCSSLink(Document pDocument, String strNRBE) throws Exception
    {
        Elements pLinksCss = pDocument.select("link[href]");
        if (htCacheData == null || getCacheSize() < 1)
        {
            pLog.debug("La caché no está inicializada se procede a inicializarla");
            synchronizeLoadCache();
        }
        for (Element pItem : pLinksCss)
        {
            if (pItem != null)
            {
                String strOldLink = pItem.attr("href");
                /* se comprueba si el link es de tipo css */
                if ("stylesheet".equals(pItem.attr("rel")))
                {
                    /* se obtiene la traducción del link */
                    String strNewLink = getLinkConversion(strNRBE, strOldLink);
                    /* se comprueba si el link tiene traducción */
                    if (strNewLink != null)
                    {
                        /* se modifica la propiedad href del link */
                        pItem.attr("href", strNewLink);
                        pLog.debug("Se modifica el enlace \"" + strOldLink + "\" por \"" + strNewLink + "\"");
                    }
                }
            }
        }
        /* se retorna el documetno modificado */
        return pDocument;
    }
}
