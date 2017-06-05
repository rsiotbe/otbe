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
import javax.ws.rs.core.UriInfo;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.security.IdentityProviderFactory;
import com.rsi.rvia.rest.tool.AppConfiguration;
import com.rsi.rvia.rest.tool.Utils;

/**
 * Objeto que representa una operativa o operación definida en la aplicación
 */
public class MiqQuests
{
    private static Logger                          pLog                          = LoggerFactory.getLogger(MiqQuests.class);
    private int                                    nIdMiq;
    private String                                 strPathRest;
    private CompomentType                          pCompomentType;
    private String                                 strEndPoint;
    private String                                 strTemplate;
    private IdentityProviderFactory.IdProvider     pIdProvider;
    private JSONObject                             jsonOpciones;
    public static Hashtable<String, MiqQuestParam> htParamsInput                 = new Hashtable<String, MiqQuestParam>();
    public static Hashtable<Integer, MiqQuests>    htCacheDataId                 = new Hashtable<Integer, MiqQuests>();
    public static Hashtable<String, MiqQuests>     htCacheDataPath               = new Hashtable<String, MiqQuests>();
    public static final String                     OPTION_PARAM_PROPAGATE_ID_MIQ = "propagateIdMiq";

    /**
     * Enumeración con el tipo de componente que gestiona la petición
     */
    public enum CompomentType
    {
        API, RVIA, LITE, SIMULATOR, WS, COORD
    };

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
        if (htParamsInput != null)
        {
            nReturn = +htParamsInput.size();
        }
        return nReturn;
    }

    /**
     * Reinicia la Cache
     */
    public static void resetCache()
    {
        htCacheDataId.clear();
        htCacheDataPath.clear();
        htCacheDataId = new Hashtable<Integer, MiqQuests>();
        htCacheDataPath = new Hashtable<String, MiqQuests>();
        htParamsInput.clear();
        htParamsInput = new Hashtable<String, MiqQuestParam>();
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
        strReturn += Utils.hastablePrettyPrintHtml(htCacheDataPath);
        strReturn += "\n";
        strReturn += Utils.hastablePrettyPrintHtml(htParamsInput);
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

    public CompomentType getComponentType()
    {
        return pCompomentType;
    }

    public void setComponentType(CompomentType pCompomentType)
    {
        this.pCompomentType = pCompomentType;
    }

    public IdentityProviderFactory.IdProvider getIdProvider()
    {
        return pIdProvider;
    }

    public void setIdProvider(IdentityProviderFactory.IdProvider pIdProvider)
    {
        this.pIdProvider = pIdProvider;
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

    public JSONObject getOptions()
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
            if (pRequest.isSecure())
            {
                strRealEndPoint = "https://localhost";
            }
            else
            {
                String servicePort = "9082";
                if (AppConfiguration.getInstance().getProperty("catalinaServicePort") != null)
                {
                    servicePort = AppConfiguration.getInstance().getProperty("catalinaServicePort").trim();
                }
                strRealEndPoint = "http://localhost:" + servicePort;
            }
            strRealEndPoint += this.strEndPoint;
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

    /** Contructor genérico */
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
    public MiqQuests(int nIdMiq, String strPathRest, String strComponentType, String strIdProvider, String strEndPoint,
            String strTemplate, String strOpciones)
    {
        this.nIdMiq = nIdMiq;
        this.strPathRest = strPathRest;
        this.pCompomentType = CompomentType.valueOf(strComponentType);
        this.pIdProvider = IdentityProviderFactory.IdProvider.valueOf(strIdProvider);
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
                    + ".bdptb222_miq_quests order by 1 asc";
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pResultSet = pPreparedStatement.executeQuery();
            while (pResultSet.next())
            {
                if (pResultSet.getString("direccionador") == null)
                {
                    pLog.error("Tipo de proveedor de identidad del miqQuest " + pResultSet.getInt("id_miq")
                            + " no esta informado. Es necesario definir uno en el campo 'Direccionador' de la tabla");
                    pLog.warn("Se descarta la carga del del miqQuest " + pResultSet.getInt("id_miq")
                            + " y se continua con el resto");
                    continue;
                }
                MiqQuests pMiqQuests = new MiqQuests(pResultSet.getInt("id_miq"), pResultSet.getString("path_rest"), pResultSet.getString("component_type"), pResultSet.getString("direccionador"), pResultSet.getString("end_point"), pResultSet.getString("miq_out_template"), pResultSet.getString("opciones"));
                if (!htCacheDataId.containsKey(pResultSet.getInt("id_miq")))
                    htCacheDataId.put(pResultSet.getInt("id_miq"), pMiqQuests);
                if (!htCacheDataPath.containsKey(pResultSet.getString("path_rest")))
                    htCacheDataPath.put(pResultSet.getString("path_rest"), pMiqQuests);
                pLog.debug("Se cachea la operación con idMiq " + pResultSet.getInt("id_miq") + ": "
                        + pResultSet.getString("path_rest"));
                loadInputParams(pResultSet.getString("path_rest"));
            }
            pLog.debug("Se carga la cache de MiqQuest con " + getCacheSize()
                    + " elementos. (La mitad es por id y la otra mitad por Path");
        }
        catch (Exception ex)
        {
            pLog.error("Error al realizar la consulta a la BBDD. Error: " + ex);
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
        // String idMiq = pResultSet.getString("id_miq");
        String strQuery = "select a.id_miq, c.* from  "
                + AppConfiguration.getInstance().getProperty("BELScheme").trim() + ".BDPTB222_MIQ_QUESTS a, " + " "
                + AppConfiguration.getInstance().getProperty("BELScheme").trim() + ".BDPTB226_MIQ_QUEST_RL_SESSION b, "
                + AppConfiguration.getInstance().getProperty("BELScheme").trim() + ".BDPTB225_MIQ_SESSION_PARAMS c "
                + " where a.id_miq=b.id_miq  and b.ID_MIQ_PARAM=c.ID_MIQ_PARAM  and a.path_rest='" + strPathRest
                + "' order by c.ID_MIQ_PARAM";
        try
        {
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pResultSet = pPreparedStatement.executeQuery();
            while (pResultSet.next())
            {
                String idMiq = pResultSet.getString("id_miq");
                MiqQuestParam pMiqQuestParam = new MiqQuestParam(pResultSet.getInt("id_miq_param"), pResultSet.getString("paramname"), pResultSet.getString("paramvalue"), pResultSet.getString("paramdesc"), pResultSet.getString("paramtype"), pResultSet.getString("headername"), pResultSet.getString("aliasname"));
                String keyForHtParamsInput = idMiq + pResultSet.getString("aliasname");
                if (!htParamsInput.containsKey(keyForHtParamsInput))
                {
                    pLog.info("Añadiendo parametro: " + pResultSet.getString("aliasname"));
                    htParamsInput.put(keyForHtParamsInput, pMiqQuestParam);
                }
            }
        }
        catch (Exception ex)
        {
            pLog.error("Error en el proceso de cargar los parámetros", ex);
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
        }
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
        pSb.append("ComponentType :" + pCompomentType.name() + "\n");
        pSb.append("IdProvider    :" + pIdProvider.name() + "\n");
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
     * @param pUriInfo
     *            Objeto uriinfo recogido de la request
     * @return MiqQuests con el id:miq, el component_type, el end_point y el template.
     * @throws Exception
     */
    public static MiqQuests getMiqQuests(UriInfo pUriInfo) throws Exception
    {
        String strPrimaryPath = Utils.getPrimaryPath(pUriInfo);
        return getMiqQuests(strPrimaryPath);
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
        if (getCacheSize() == 0)
        {
            synchronizeLoadCache();
        }
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
        if (getCacheSize() == 0)
        {
            synchronizeLoadCache();
        }
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
        {
            return pAllInputs;
        }
        if (htParamsInput == null)
        {
            return pAllInputs;
        }
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
