package com.rsi.rvia.translates;

import java.util.Enumeration;
import java.util.Hashtable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants.Language;
import com.rsi.rvia.rest.tool.Utils;

/** Servlet implementation class translateService */
public class TranslateAppService
{
    private static Logger pLog = LoggerFactory.getLogger(TranslateAppService.class);

    /**
     * Método que dado un nombre de aplaición y un idioma devuelve todas las traducciones en dicho idioma
     * 
     * @param astrApps
     * @param strLanguage
     * @return
     */
    public static String process(String strApps, Language pLanguage)
    {
        String strJSONReturn;
        Hashtable<?, ?> htReturn = null;
        try
        {
            if (strApps != null && !(strApps.isEmpty()))
            {
                if (pLanguage == null)
                {
                    pLog.info("Se recibe una petición para traducir la aplicación " + strApps + " a todos los idiomas");
                    htReturn = processOneAppAllLanguages(strApps);
                }
                else
                {
                    pLog.info("Se recibe una petición para traducir la aplicación " + strApps + " a "
                            + pLanguage.getJavaCode());
                    htReturn = processOneAppOneLanguages(strApps, pLanguage);
                }
            }
            else
            {
                throw new Exception("No existe apliación a traducir");
            }
            strJSONReturn = Utils.objectToJson(htReturn);
        }
        catch (Exception ex)
        {
            strJSONReturn = "{\"Error\":\"Fallo al obtener JSON\",\"desc\":\""
                    + Utils.replaceIlegalCharactersInJSON(Utils.getExceptionStackTrace(ex)) + "\"}";
        }
        return strJSONReturn;
    }

    private static Hashtable<Language, Hashtable<String, String>> processIdsAllLanguages(String[] astrIds)
            throws Exception
    {
        Hashtable<Language, Hashtable<String, String>> htReturn;
        Hashtable<String, TranslateEntry> htData;
        htData = TranslateProcessor.getTranslationsById(astrIds);
        htReturn = regroupHastableByLanguage(htData);
        return htReturn;
    }

    private static Hashtable<String, String> processIdsOneLanguages(String[] astrIds, Language pLanguage)
            throws Exception
    {
        return TranslateProcessor.getTranslationsById(astrIds, pLanguage);
    }

    private static Hashtable<Language, Hashtable<String, String>> processOneAppAllLanguages(String strAppName)
            throws Exception
    {
        Hashtable<Language, Hashtable<String, String>> htReturn;
        Hashtable<String, TranslateEntry> htData;
        htData = TranslateProcessor.getTranslationsByApp(strAppName);
        htReturn = regroupHastableByLanguage(htData);
        return htReturn;
    }

    private static Hashtable<String, String> processOneAppOneLanguages(String strAppName, Language pLanguage)
            throws Exception
    {
        Hashtable<String, String> htReturn;
        htReturn = TranslateProcessor.getTranslationsByApp(strAppName, pLanguage);
        return htReturn;
    }

    private static Hashtable<String, Hashtable<Language, Hashtable<String, String>>> processAppsAllLanguages(
            String[] astrAppName) throws Exception
    {
        Hashtable<String, Hashtable<Language, Hashtable<String, String>>> htReturn = new Hashtable<String, Hashtable<Language, Hashtable<String, String>>>();
        Hashtable<String, TranslateEntry> htData;
        Hashtable<Language, Hashtable<String, String>> htAux;
        for (String strAppName : astrAppName)
        {
            htData = TranslateProcessor.getTranslationsByApp(strAppName);
            htAux = regroupHastableByLanguage(htData);
            /* se añade la aplicación al resultado */
            htReturn.put(strAppName, htAux);
        }
        return htReturn;
    }

    private static Hashtable<String, Hashtable<String, String>> processAppsOneLanguages(String[] astrAppName,
            Language pLanguage) throws Exception
    {
        Hashtable<String, Hashtable<String, String>> htReturn = new Hashtable<String, Hashtable<String, String>>();
        Hashtable<String, String> htData;
        for (String strAppName : astrAppName)
        {
            htData = TranslateProcessor.getTranslationsByApp(strAppName, pLanguage);
            htReturn.put(strAppName, htData);
        }
        return htReturn;
    }

    private static Hashtable<Language, Hashtable<String, String>> regroupHastableByLanguage(
            Hashtable<String, TranslateEntry> htData)
    {
        Hashtable<Language, Hashtable<String, String>> htReturn = new Hashtable<Language, Hashtable<String, String>>();
        Enumeration<String> pEnumHtData = htData.keys();
        while (pEnumHtData.hasMoreElements())
        {
            String strHtCode = (String) pEnumHtData.nextElement();
            TranslateEntry pTranslations = htData.get(strHtCode);
            Enumeration<Language> pEnumHtTranslates = pTranslations.getAllTranslations().keys();
            while (pEnumHtTranslates.hasMoreElements())
            {
                Language pLanguage = (Language) pEnumHtTranslates.nextElement();
                /* si no existe todavia el idioma en el hashtable de salida se añade una nueva entrada */
                if (!htReturn.containsKey(pLanguage))
                {
                    htReturn.put(pLanguage, new Hashtable<String, String>());
                }
                Hashtable<String, String> htTranslationsLang = htReturn.get(pLanguage);
                /* se añade la traducción al resultado */
                htTranslationsLang.put(pTranslations.getCode(), pTranslations.getTranslation(pLanguage));
            }
        }
        return htReturn;
    }
}
