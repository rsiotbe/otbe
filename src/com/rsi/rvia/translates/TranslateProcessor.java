/************************************************************************
 * CREACION: REFERENCIA: P000008956 FECHA: 02-08-2016 AUTOR: Victor Muñoz Descripción: Clase procesadora de traducciones
 * en HTML MODIFICACIONES:
 ************************************************************************/
package com.rsi.rvia.translates;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants;
import com.rsi.Constants.Language;

/** Clase que gestiona el cambio de idioma en el contenido HTML de la web. */
public class TranslateProcessor
{
    private static Logger pLog = LoggerFactory.getLogger(TranslateProcessor.class);

    public static Hashtable<String, String> getTranslationsById(String[] pStrIds, Language pLanguage) throws Exception
    {
        Hashtable<String, String> htReturn;
        htReturn = TranslateCache.getTranslationsByCode(pStrIds, pLanguage);
        return htReturn;
    }

    public static Hashtable<String, TranslateEntry> getTranslationsById(String[] pStrIds) throws Exception
    {
        Hashtable<String, TranslateEntry> htReturn;
        htReturn = TranslateCache.getTranslationsByCode(pStrIds);
        return htReturn;
    }

    public static Hashtable<String, String> getTranslationsByApp(String strAppName, Language pLanguage)
            throws Exception
    {
        Hashtable<String, String> htReturn;
        htReturn = TranslateCache.getTranslationsByApp(strAppName, pLanguage);
        return htReturn;
    }

    public static Hashtable<String, TranslateEntry> getTranslationsByApp(String strAppName) throws Exception
    {
        Hashtable<String, TranslateEntry> htReturn;
        htReturn = TranslateCache.getTranslationsByApp(strAppName);
        return htReturn;
    }

    /**
     * Función Principal, recibe el documento Jsoup y el idioma y lo traduce
     * 
     * @param pDocument
     *            Documento jsoup que contien el html
     * @param strLanguage
     *            idioma de la traducción
     * @return Documento jsoup con el HTML con la nueva traducción ya aplicada.
     */
    public static Document processHTML(Document pDocument, Language pLanguage)
    {
        ArrayList<String> alIdsTrans = null;
        Hashtable<String, TranslateEntry> htTransData = new Hashtable<String, TranslateEntry>();
        pLog.debug("String XHTML parseado a Documento correctamente.");
        if (pDocument != null)
        {
            alIdsTrans = extractIdsFromDocument(pDocument);
            pLog.debug("IDs de traducciones extraidos correctamente.");
            pLog.debug("alIdsTrans lenght: " + alIdsTrans.size());
        }
        if (alIdsTrans != null && !alIdsTrans.isEmpty())
        {
            try
            {
                String[] aIdsTrans = new String[alIdsTrans.size()];
                aIdsTrans = alIdsTrans.toArray(aIdsTrans);
                htTransData = getTranslationsById(aIdsTrans);
                pLog.debug("Traducciones recuperadas correctamente.");
            }
            catch (Exception ex)
            {
                pLog.error("Error al intentar recuperar las Traducciones de la BBDD", ex);
            }
        }
        if (htTransData != null && !htTransData.isEmpty())
        {
            if (pLanguage == null)
                pLanguage = Constants.DEFAULT_LANGUAGE;
            pLog.debug("Documento premodificación es nulo?: " + (pDocument == null));
            pDocument = modifyDocument(pDocument, htTransData, pLanguage);
            pLog.debug("Documento modificado Correctamente. Tamaño de htTransData: " + htTransData.size());
        }
        return pDocument;
    }

    /**
     * Función que extrae todos los IDs de data-translate dado un Document(Jsoup)
     * 
     * @param pDocument
     *            Document(Jsoup) con el HTML parseado.
     * @return ArrayList<String> con los IDs de data-translate
     */
    private static ArrayList<String> extractIdsFromDocument(Document pDocument)
    {
        ArrayList<String> alIdsTranslate = new ArrayList<String>();
        Elements pListDataTranslate = pDocument.getElementsByAttribute("data-translate");
        for (Element pItem : pListDataTranslate)
        {
            String strId = pItem.attr("data-translate");
            if (strId != null)
            {
                alIdsTranslate.add(strId);
            }
        }
        return alIdsTranslate;
    }

    /**
     * Función que modifica sus etiquetas data-translate con las nuevas traducciones.
     * 
     * @param doc
     *            Document(Jsoup) a modificar.
     * @param htData
     *            Hashtable con los IDs data.translate y las traducciones.
     * @param pLanguage
     *            Idioma al que se quiere traducir.
     * @return Document(Jsoup) con la traducción ya puesta.
     */
    private static Document modifyDocument(Document pDoc, Hashtable<String, TranslateEntry> htData, Language pLanguage)
    {
        Enumeration<String> pEnumHTData = htData.keys();
        while (pEnumHTData.hasMoreElements())
        {
            String strHtKey = (String) pEnumHTData.nextElement();
            Elements pListIDsTrans = pDoc.select("[data-translate=\"" + strHtKey + "\"]");
            for (Element pItem : pListIDsTrans)
            {
                if (pItem != null)
                {
                    TranslateEntry pTrans = (TranslateEntry) htData.get(strHtKey);
                    String strTraduccion = pTrans.getTranslation(pLanguage);
                    if (strTraduccion != null)
                    {
                        pItem.text(strTraduccion);
                    }
                }
            }
        }
        /* se añade el atributo lang a la etiqueta html para poder manejar el idioma dentro de la página */
        pDoc.getElementsByTag("html").attr("lang", pLanguage.getWindowsCode());
        return pDoc;
    }
}
