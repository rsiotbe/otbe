package com.rsi.rvia.rest.operation;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.tool.Utils;

/**
 * Objeto que representa una operativa o operación definida en la aplicación
 */
public class MiqQuests
{
    private static Logger                          pLog                          = LoggerFactory.getLogger(MiqQuests.class);
    private int                                    nIdMiq;
    private String                                 strPathRest;
    private String                                 strComponentType;
    private String                                 strEndPoint;
    private String                                 strTemplate;
    private JSONObject                             jsonOpciones;
    public static Hashtable<String, MiqQuestParam> htParamsInput                 = new Hashtable<String, MiqQuestParam>();
    public static Hashtable<Integer, MiqQuests>    htCacheDataId                 = new Hashtable<Integer, MiqQuests>();
    public static Hashtable<String, MiqQuests>     htCacheDataPath               = new Hashtable<String, MiqQuests>();
    public static final String                     OPTION_PARAM_PROPAGATE_ID_MIQ = "propagateIdMiq";

    /**
     * Devuelve el tamaño de la cache
     * 
     * @return int con el tamaño de la cache
     */
    public static int getCacheSize()
    {
        int nReturn = 0;
        if (htCacheDataId != null)
        {
            nReturn = +htCacheDataId.size();
        }
        if (htCacheDataPath != null)
        {
            nReturn = +htCacheDataPath.size();
        }
        return nReturn;
    }

    /**
     * Reinicia la Cache
     */
    public static void resetCache()
    {
        htCacheDataId.clear();
        htCacheDataId = new Hashtable<Integer, MiqQuests>();
        htCacheDataPath.clear();
        htCacheDataPath = new Hashtable<String, MiqQuests>();
    }

    /**
     * Devuelve los datos de la cache en formato texto
     * 
     * @return COnenido de la caché
     * @throws Exception
     */
    public static String cacheToString() throws Exception
    {
        String strReturn;
        strReturn = Utils.hastablePrettyPrintHtml(htCacheDataId);
        strReturn += "\n";
        strReturn = Utils.hastablePrettyPrintHtml(htCacheDataPath);
        return strReturn;
    }

    public int getIdMiq()
    {
        return nIdMiq;
    }

    public void setIdMiq(int nIdMiq)
    {
        this.nIdMiq = nIdMiq;
    }

    public String getPathRest()
    {
        return strPathRest;
    }

    public void setPathRest(String strPathRest)
    {
        this.strPathRest = strPathRest;
    }

    public String getComponentType()
    {
        return strComponentType;
    }

    public void setComponentType(String strComponentType)
    {
        this.strComponentType = strComponentType;
    }

    public String getEndPoint()
    {
        return strEndPoint;
    }

    public void setEndPoint(String strEndPoint)
    {
        this.strEndPoint = strEndPoint;
    }

    public String getTemplate()
    {
        return strTemplate;
    }

    public void setTemplate(String strTemplate)
    {
        this.strTemplate = strTemplate;
    }

    public JSONObject getJsonOpciones()
    {
        return jsonOpciones;
    }

    public void setJsonOpciones(JSONObject jsonOpciones)
    {
        this.jsonOpciones = jsonOpciones;
    }

    /**
     * Obtiene un objeto URI a del valor de EndPoint
     * 
     * @param strEndPoint
     *            String que contiene la uri
     * @return Objeto URI
     */
    public URI getBaseWSEndPoint(HttpServletRequest pRequest)
    {
        String strRealEndPoint = "";
        URI pUriReturn = null;
        if (this.strEndPoint != null && this.strEndPoint.startsWith("/api/"))
        {
            strRealEndPoint = "http://localhost:" + pRequest.getLocalPort() + this.strEndPoint;
            pUriReturn = UriBuilder.fromUri(strRealEndPoint).build();
        }
        else
        {
            pUriReturn = UriBuilder.fromUri(this.strEndPoint).build();
            strRealEndPoint = this.strEndPoint;
        }
        pLog.debug("Uri final: " + strRealEndPoint);
        return pUriReturn;
    }

    /** Contructor generico */
    public MiqQuests()
    {
    }

    /**
     * Contructor con parámetros
     * 
     * @param nIdMiq
     *            Identificador de operación
     * @param strComponentType
     *            Tipo de componente
     * @param strEndPoint
     *            Dirección endPoint
     * @param strTemplate
     *            Plantilla asociada
     * @param opciones
     */
    public MiqQuests(int nIdMiq, String strPathRest, String strComponentType, String strEndPoint, String strTemplate,
            String strOpciones)
    {
        this.nIdMiq = nIdMiq;
        this.strPathRest = strPathRest;
        this.strComponentType = strComponentType;
        this.strEndPoint = strEndPoint;
        this.strTemplate = strTemplate;
        JSONObject opciones = null;
        if (strOpciones != null && strOpciones.trim().length() > 0)
        {
            try
            {
                opciones = new JSONObject(strOpciones);
            }
            catch (JSONException ex)
            {
                pLog.error("Error al realizar la conversión del string opciones a JSON de " + nIdMiq, ex);
            }
        }
        this.jsonOpciones = opciones;
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
            String strQuery = "SELECT * from bel.bdptb222_miq_quests";
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pResultSet = pPreparedStatement.executeQuery();
            while (pResultSet.next())
            {
                MiqQuests pMiqQuests = new MiqQuests(pResultSet.getInt("id_miq"), pResultSet.getString("path_rest"), pResultSet.getString("component_type"), pResultSet.getString("end_point"), pResultSet.getString("miq_out_template"), pResultSet.getString("opciones"));
                if (!htCacheDataId.containsKey(pResultSet.getInt("id_miq")))
                    htCacheDataId.put(pResultSet.getInt("id_miq"), pMiqQuests);
                if (!htCacheDataPath.containsKey(pResultSet.getString("path_rest")))
                    htCacheDataPath.put(pResultSet.getString("path_rest"), pMiqQuests);
                loadInputParams(pResultSet.getString("path_rest"));
            }
            pLog.debug("Se carga la cache de MiqQuest con " + getCacheSize()
                    + " elementos. (La mitad es por id y la otra mitad por Path");
        }
        catch (Exception ex)
        {
            pLog.error("Error al realizar la consulta a la BBDD. Trace: \n\n\t" + ex.getMessage());
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
        }
    }

    /**
     * Funcion que carga las entradas del servicio en cache desde base de datos
     * 
     * @throws Exception
     */
    private static void loadInputParams(String strPathRest) throws Exception
    {
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        String strQuery = "select a.id_miq, c.* from " + " BEL.BDPTB222_MIQ_QUESTS a, "
                + " BEL.BDPTB226_MIQ_QUEST_RL_SESSION b, " + " BEL.BDPTB225_MIQ_SESSION_PARAMS c "
                + " where a.id_miq=b.id_miq " + " and b.ID_MIQ_PARAM=c.ID_MIQ_PARAM " + " and a.path_rest='"
                + strPathRest + "' order by c.ID_MIQ_PARAM";
        pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
        pPreparedStatement = pConnection.prepareStatement(strQuery);
        pResultSet = pPreparedStatement.executeQuery();
        while (pResultSet.next())
        {
            MiqQuestParam pMiqQuestParam = new MiqQuestParam(pResultSet.getInt("id_miq_param"), pResultSet.getString("paramname"), pResultSet.getString("paramvalue"), pResultSet.getString("paramdesc"), pResultSet.getString("paramtype"), pResultSet.getString("headername"), pResultSet.getString("aliasname"));
            if (!htParamsInput.containsKey(pResultSet.getString("aliasname")))
            {
                String idMiq = pResultSet.getString("id_miq");
                htParamsInput.put(idMiq + pResultSet.getString("aliasname"), pMiqQuestParam);
            }
        }
        pLog.trace("Cargadas en cache las entradas del servicio");
        pResultSet.close();
        pPreparedStatement.close();
        pConnection.close();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuilder pSb = new StringBuilder();
        pSb.append("IdMiq         :" + nIdMiq + "\n");
        pSb.append("PathRest      :" + strPathRest + "\n");
        pSb.append("ComponentType :" + strComponentType + "\n");
        pSb.append("EndPoint      :" + strEndPoint + "\n");
        pSb.append("Template      :" + strTemplate + "\n");
        if (jsonOpciones != null)
        {
            pSb.append("Opciones      :" + jsonOpciones.toString());
        }
        return pSb.toString();
    }

    /**
     * Realiza una conexión a la BBDD para obtener los datos necesarios para crear un objeto MiqQuests y darlo como
     * respuesta.
     * 
     * @param strPath
     *            String path primario para la clausula where de la consulta
     * @return MiqQuests con el id:miq, el component_type, el end_point y el template.
     * @throws Exception
     */
    public static MiqQuests getMiqQuests(String strPath) throws Exception
    {
        MiqQuests pMiqQuests = null;
        /* si la caché no está cargada se carga */
        // if (getCacheSize() == 0)
        loadDDBBCache();
        pMiqQuests = htCacheDataPath.get(strPath);
        return pMiqQuests;
    }

    /**
     * Realiza una conexión a la BBDD para obtener los datos necesarios para crear un objeto MiqQuests y darlo como
     * respuesta.
     * 
     * @param nMiqQuestId
     *            identificador de la operación
     * @return MiqQuests con el id:miq, el component_type, el end_point y el template.
     * @throws Exception
     */
    public static MiqQuests getMiqQuests(int nMiqQuestId) throws Exception
    {
        MiqQuests pMiqQuests = null;
        /* si la caché no está cargada se carga */
        // if (getCacheSize() == 0)
        loadDDBBCache();
        pMiqQuests = htCacheDataId.get(nMiqQuestId);
        return pMiqQuests;
    }

    /**
     * Realiza una conexión a la BBDD para obtener los datos necesarios para crear un objeto MiqQuests y darlo como
     * respuesta.
     * 
     * @param nMiqQuestId
     *            identificador de la operación
     * @return MiqQuests con el id:miq, el component_type, el end_point y el template.
     * @throws Exception
     */
    public MultivaluedMap<String, String> testInputParams(MultivaluedMap<String, String> pAllInputs) throws Exception
    {
        if (pAllInputs == null)
            return pAllInputs;
        if (htParamsInput == null)
            return pAllInputs;
        MultivaluedMap<String, String> paramsToRvia = new MultivaluedHashMap<String, String>();
        Iterator<String> pIterator = pAllInputs.keySet().iterator();
        int nIdMiq = getIdMiq();
        while (pIterator.hasNext())
        {
            String strAlias = (String) pIterator.next();
            MiqQuestParam paramDetail = htParamsInput.get(nIdMiq + strAlias);
            if (paramDetail == null)
            {
                paramsToRvia.put(strAlias, pAllInputs.get(strAlias));
            }
            else if (!paramDetail.getStrAlias().equals(paramDetail.getStrName()))
            {
                paramsToRvia.put(paramDetail.getStrName(), pAllInputs.get(strAlias));
            }
            else
            {
                paramsToRvia.put(strAlias, pAllInputs.get(strAlias));
            }
        }
        // pMiqQuests = htCacheDataId.get(nMiqQuestId);
        return paramsToRvia;
    }
}
