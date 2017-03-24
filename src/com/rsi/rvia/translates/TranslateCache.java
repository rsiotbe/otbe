package com.rsi.rvia.translates;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.tool.Utils;

public class TranslateCache
{
    private static Logger                            pLog                 = LoggerFactory.getLogger(TranslateCache.class);
    private static Hashtable<String, TranslationApp> htTranslateCacheData = new Hashtable<String, TranslationApp>();
    private static ArrayList<String>                 alAppsFullLoaded     = new ArrayList<String>();

    /**
     * Devuelve el tamaño de la cache
     * 
     * @return int con el tamaño de la cache
     */
    public static int getCacheSize()
    {
        int nReturn = 0;
        if (htTranslateCacheData != null)
        {
            Enumeration<String> eApps = htTranslateCacheData.keys();
            while (eApps.hasMoreElements())
            {
                String strAppName = (String) eApps.nextElement();
                TranslationApp pTranslationApp = htTranslateCacheData.get(strAppName);
                nReturn += pTranslationApp.getCountTanslations();
            }
        }
        return nReturn;
    }

    /**
     * Reinicia la Cache
     */
    public static void resetCache()
    {
        if (htTranslateCacheData != null)
        {
            htTranslateCacheData = new Hashtable<String, TranslationApp>();
        }
        if (alAppsFullLoaded != null)
        {
            alAppsFullLoaded = new ArrayList<String>();
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
        String strReturn = "";
        strReturn += Utils.hastablePrettyPrintHtml(htTranslateCacheData);
        return strReturn;
    }

    private static void loadDataFromDDBBUsingCodes(String[] astrCodes) throws Exception
    {
        String strCodesToSearchInDDBB = "";
        for (String strCode : astrCodes)
        {
            boolean fCodeFound = false;
            Enumeration<String> e = htTranslateCacheData.keys();
            /* se busca si el codigo de traducción existe en la cache de alguna apliación */
            while (e.hasMoreElements() && fCodeFound == false)
            {
                String strAppName = (String) e.nextElement();
                TranslationApp pTranslationApp = htTranslateCacheData.get(strAppName);
                if (pTranslationApp.existTranslation(strCode))
                {
                    fCodeFound = true;
                    break;
                }
            }
            /* si no se ha encontrado se añade en la lista a buscar */
            if (!fCodeFound)
            {
                if (!strCodesToSearchInDDBB.equals(""))
                {
                    strCodesToSearchInDDBB += ",";
                }
                strCodesToSearchInDDBB += "'" + strCode + "'";
            }
        }
        if (!strCodesToSearchInDDBB.isEmpty())
        {
            Connection pConnection = null;
            PreparedStatement pPreparedStatement = null;
            ResultSet pResultSet = null;
            try
            {
                String strQuery = "SELECT A.codigo, A.idioma, A.traduccion, B.aplicativo"
                        + " FROM bel.bdptb079_idioma A, bel.bdptb079_idioma_aplicativo B"
                        + " where A.codigo=b.codigo and A.codigo in (?)";
                pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                pPreparedStatement = pConnection.prepareStatement(strQuery);
                pPreparedStatement.setString(1, strCodesToSearchInDDBB);
                pResultSet = pPreparedStatement.executeQuery();
                while (pResultSet.next())
                {
                    String strAppName = (String) pResultSet.getString("aplicativo");
                    String strCode = (String) pResultSet.getString("codigo");
                    String strLang = (String) pResultSet.getString("idioma");
                    String strTranslate = (String) pResultSet.getString("traduccion");
                    /* si el resultado pertenece a una aplicación aun no definida, se da de alta */
                    if (!htTranslateCacheData.containsKey(strAppName))
                    {
                        htTranslateCacheData.put(strAppName, new TranslationApp(strAppName));
                    }
                    TranslationApp pTranslationApp = htTranslateCacheData.get(strAppName);
                    /* si la aplicación traducida ya tiene el codigo, se añade la nueva traducción */
                    if (pTranslationApp.existTranslation(strCode))
                    {
                        TranslateEntry pTranslateEntry = pTranslationApp.getTranslation(strCode);
                        pTranslateEntry.addTranslation(strLang, strTranslate);
                    }
                    /*
                     * si la aplicación traducida no tiene todaviae el codigo, se añade el nuevo objeto Translate entry
                     * y la nueva traducción
                     */
                    else
                    {
                        TranslateEntry pTranslateEntry = new TranslateEntry(strCode, strAppName);
                        pTranslateEntry.addTranslation(strLang, strTranslate);
                        pTranslationApp.addTranslateEntry(strCode, pTranslateEntry);
                    }
                }
            }
            catch (Exception ex)
            {
                pLog.error("Error al realizar la consulta a la BBDD", ex);
            }
            finally
            {
                DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
            }
        }
    }

    private static void loadDataFromDDBBUsingApps(String strAppName) throws Exception
    {
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        try
        {
            String strQuery = "SELECT A.codigo, A.idioma, A.traduccion, B.aplicativo"
                    + " FROM bdptb079_idioma A, bdptb079_idioma_aplicativo B"
                    + " where A.codigo=b.codigo and b.aplicativo = ?";
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pPreparedStatement.setString(1, strAppName);
            pResultSet = pPreparedStatement.executeQuery();
            while (pResultSet.next())
            {
                String strCode = (String) pResultSet.getString("codigo");
                String strLang = (String) pResultSet.getString("idioma");
                String strTranslate = (String) pResultSet.getString("traduccion");
                /* si el resultado pertenece a una aplicación aun no definida, se da de alta */
                if (!htTranslateCacheData.containsKey(strAppName))
                {
                    htTranslateCacheData.put(strAppName, new TranslationApp(strAppName));
                }
                TranslationApp pTranslationApp = htTranslateCacheData.get(strAppName);
                /* si la aplicación traducida ya tiene el codigo, se añade la nueva traducción */
                if (pTranslationApp.existTranslation(strCode))
                {
                    if (!pTranslationApp.existTranslationInLanguaje(strCode, strLang))
                    {
                        TranslateEntry pTranslateEntry = pTranslationApp.getTranslation(strCode);
                        pTranslateEntry.addTranslation(strLang, strTranslate);
                    }
                }
                /*
                 * si la aplicación traducida no tiene todavia el codigo, se añade el nuevo objeto Translate entry y la
                 * nueva traducción
                 */
                else
                {
                    TranslateEntry pTranslateEntry = new TranslateEntry(strCode, strAppName);
                    pTranslateEntry.addTranslation(strLang, strTranslate);
                    pTranslationApp.addTranslateEntry(strCode, pTranslateEntry);
                }
            }
            /* se marca la aplicacion como totalmente cargada */
            if (!alAppsFullLoaded.contains(strAppName))
                alAppsFullLoaded.add(strAppName);
        }
        catch (Exception ex)
        {
            pLog.error("Error al realizar la consulta a la BBDD", ex);
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
        }
    }

    public static Hashtable<String, String> getTranslationsByCode(String[] astrCodes, String strLanguage)
            throws Exception
    {
        Hashtable<String, String> htResult = new Hashtable<String, String>();
        loadDataFromDDBBUsingCodes(astrCodes);
        for (String strCode : astrCodes)
        {
            Enumeration<String> e = htTranslateCacheData.keys();
            /* se busca el codigo de traducción en la cache de alguna apliación */
            while (e.hasMoreElements())
            {
                String strAppName = (String) e.nextElement();
                TranslationApp pTranslationApp = htTranslateCacheData.get(strAppName);
                if (pTranslationApp.existTranslation(strCode))
                {
                    htResult.put(strCode, pTranslationApp.getTranslation(strCode, strLanguage));
                }
            }
        }
        return htResult;
    }

    public static Hashtable<String, TranslateEntry> getTranslationsByCode(String[] astrCodes) throws Exception
    {
        Hashtable<String, TranslateEntry> htResult = new Hashtable<String, TranslateEntry>();
        loadDataFromDDBBUsingCodes(astrCodes);
        for (String strCode : astrCodes)
        {
            Enumeration<String> e = htTranslateCacheData.keys();
            /* se busca el codigo de traducción en la cache de alguna apliación */
            while (e.hasMoreElements())
            {
                String strAppName = (String) e.nextElement();
                TranslationApp pTranslationApp = htTranslateCacheData.get(strAppName);
                if (pTranslationApp.existTranslation(strCode))
                {
                    htResult.put(strCode, pTranslationApp.getTranslation(strCode));
                }
            }
        }
        return htResult;
    }

    public static Hashtable<String, String> getTranslationsByApp(String strAppName, String strLanguage)
            throws Exception
    {
        Hashtable<String, String> htResult = new Hashtable<String, String>();
        if (!alAppsFullLoaded.contains(strAppName))
        {
            loadDataFromDDBBUsingApps(strAppName);
        }
        TranslationApp pTranslationApp = htTranslateCacheData.get(strAppName);
        Enumeration<String> e = pTranslationApp.getAllTranslations().keys();
        /* se busca el codigo de traducción en la cache de alguna apliación */
        while (e.hasMoreElements())
        {
            String strCode = (String) e.nextElement();
            htResult.put(strCode, pTranslationApp.getTranslation(strCode, strLanguage));
        }
        return htResult;
    }

    public static Hashtable<String, TranslateEntry> getTranslationsByApp(String strAppName) throws Exception
    {
        Hashtable<String, TranslateEntry> htResult = new Hashtable<String, TranslateEntry>();
        if (!alAppsFullLoaded.contains(strAppName))
        {
            loadDataFromDDBBUsingApps(strAppName);
        }
        TranslationApp pTranslationApp = htTranslateCacheData.get(strAppName);
        Enumeration<String> e = pTranslationApp.getAllTranslations().keys();
        /* se busca el codigo de traducción en la cache de alguna apliación */
        while (e.hasMoreElements())
        {
            String strCode = (String) e.nextElement();
            htResult.put(strCode, pTranslationApp.getTranslation(strCode));
        }
        return htResult;
    }
}
