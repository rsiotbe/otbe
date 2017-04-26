package com.rsi.rvia.rest.tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.error.exceptions.LogicalErrorException;

public class ServiceHelper
{
    private static Logger pLog = LoggerFactory.getLogger(ServiceHelper.class);

    public static String getHelp(int nIdMiq) throws Exception, SQLException
    {
        JSONObject jsonPrevious = null;
        Map<String, Map<String, Object>> mapHelp = null;
        Map<String, Object> mapSInfo = null;
        Map<String, Object> mapModifiers = null;
        Map<String, String> mapModifier = null;
        try
        {
            mapHelp = new LinkedHashMap<String, Map<String, Object>>();
            mapSInfo = new LinkedHashMap<String, Object>();
            jsonPrevious = getServiceInfo(nIdMiq).getJSONObject(0);
            Iterator<?> it = jsonPrevious.keys();
            while (it.hasNext())
            {
                String key = (String) it.next();
                mapSInfo.put(key, jsonPrevious.getString(key));
            }
            mapSInfo.put("inputFields", getAvailableInputs(nIdMiq));
            mapSInfo.put("exitFields", getAvailableExits(nIdMiq));
            mapModifiers = new LinkedHashMap<String, Object>();
            mapModifiers.put("description", "Modificadores de comportamiento que pueden usarse como parámetros");
            mapModifier = new LinkedHashMap<String, String>();
            mapModifier.put("description", "Lista de campos a solicitar___ dentro de los disponibles___ y separados por coma.");
            mapModifier.put("example", "..../path?fieldslist··field1___field2...");
            mapModifiers.put("fieldslist", mapModifier);
            mapModifier = new LinkedHashMap<String, String>();
            mapModifier.put("description", "Lista de campos por los que ordenar___ dentro de los disponibles___ y separados por coma.");
            mapModifier.put("example", "..../path?sorterslist··field1___field2...");
            mapModifiers.put("sorterslist", mapModifier);
            mapModifier = new LinkedHashMap<String, String>();
            mapModifier.put("description", "Muestra este sistema de ayuda.");
            mapModifier.put("example", "..../path?help");
            mapModifiers.put("help", mapModifier);
            mapSInfo.put("modifiers", mapModifiers);
            mapHelp.put("response", mapSInfo);
        }
        catch (Exception ex)
        {
            pLog.error("Error en el proceso de obtención de los campos de ayuda", ex);
            throw new LogicalErrorException(500, 9999, "Internal server error", "Error en la lectura de entradas", new Exception());
        }
        String strResultado = mapHelp.toString().replaceAll("([A-z]*)=", "\"$1\":").replaceAll("(:)([^{])([^,}]*)([,}])", "$1\"$2$3\"$4");
        strResultado = strResultado.replaceAll("··", "=").replaceAll("___", ",");
        strResultado = strResultado.replaceAll("##", "{").replaceAll("@@", "}").replaceAll("zCOMAz", ",");
        return strResultado;
    }

    public static String getHelp(String strPathRest) throws Exception, SQLException
    {
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        int nIdMiq = 0;
        String strQuery = " select id_miq" + " from  BEL.BDPTB222_MIQ_QUESTS" + " where trim(path_rest)=?";
        strPathRest = strPathRest.replace("/help", "");
        try
        {
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pPreparedStatement.setString(1, strPathRest);
            pResultSet = pPreparedStatement.executeQuery();
            if (pResultSet.next())
            {
                nIdMiq = pResultSet.getInt("id_miq");
            }
        }
        catch (Exception ex)
        {
            pLog.error("Error en el proceso de obtención de la ayuda para el path " + strPathRest, ex);
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
        }
        return getHelp(nIdMiq);
    }

    private static JSONArray getServiceInfo(int nIdMiq) throws SQLException
    {
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        JSONArray mapAr = null;
        String strQuery = " select" + "  replace(replace(path_rest,'{','##'),'}','@@') \"url\""
                + " ,miq_name \"serviceName\"" + " ,replace(miq_description,',','zCOMAz') \"serviceDescription\""
                + " from  BEL.BDPTB222_MIQ_QUESTS" + " where id_miq=?";
        try
        {
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pPreparedStatement.setInt(1, nIdMiq);
            pResultSet = pPreparedStatement.executeQuery();
            mapAr = Utils.convertResultSetToJSON(pResultSet);
        }
        catch (Exception ex)
        {
            pLog.error("Error en el proceso de obtención de la inforamción del servicio para el IdMiq " + nIdMiq, ex);
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
        }
        return mapAr;
    }

    private static LinkedHashMap<String, LinkedHashMap<String, Object>> getAvailableExits(int nIdMiq)
            throws SQLException, JSONException
    {
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        JSONArray mapAr = null;
        String strQuery = " select" + " 	 c.exitname \"exitName\"" + " 	,case"
                + " 		when trim(c.exitdesc) is not null then c.exitdesc"
                + " 		when trim(c.exitdesc) is null then c.exitname else c.exitdesc end  \"exitDescription\"" + " from"
                + " 	BEL.BDPTB222_MIQ_QUESTS a" + " 	left join BEL.BDPTB233_MIQ_QUEST_RL_EXITS b"
                + " 	on a.id_miq=b.id_miq" + " 	left join BEL.BDPTB232_MIQ_EXITS c"
                + " 	on b.ID_MIQ_exit=c.ID_MIQ_exit" + " where a.id_miq= ?" + " and b.miq_verb='GET'"
                + " and b.opciones not like '%propagate=false%'" + " and c.exithierarchy like 'response.data.%'";
        try
        {
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pPreparedStatement.setInt(1, nIdMiq);
            pResultSet = pPreparedStatement.executeQuery();
            mapAr = Utils.convertResultSetToJSON(pResultSet);
        }
        catch (Exception ex)
        {
            pLog.error("Error en el proceso de comprobación de existencia de parámetros para el IdMiq " + nIdMiq, ex);
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
        }
        return formatJson(mapAr, "exit");
    }

    private static LinkedHashMap<String, LinkedHashMap<String, Object>> getAvailableInputs(int nIdMiq)
            throws SQLException, JSONException, LogicalErrorException
    {
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        JSONArray mapAr = null;
        String strQuery = " select"
                + " 	 case when trim(aliasname) is null then paramname else aliasname end  \"inputName\""
                + " 	,case"
                + " 		when trim(paramdesc) is not null then paramdesc"
                + " 		when trim(aliasname) is null then paramname else aliasname end  \"inputDescription\""
                + " 	,case"
                + " 		when trim(paramdatatype) = 'Entidad'  then 'String'"
                + " 		when trim(paramdatatype) is null then 'n/a' else paramdatatype end \"inputType\""
                + " 	,case when trim(to_char(paramlong)) is null then 'n/a' else trim(to_char(paramlong)) end  \"inputLong\""
                + " 	,case when trim(parammask) is null then 'n/a' else parammask end  \"inputMask\""
                + " 	,case when trim(to_char(parammax)) is null then 'n/a' else trim(to_char(parammax)) end  \"inputMax\""
                + " 	,case when trim(to_char(parammin)) is null then 'n/a' else trim(to_char(parammin)) end  \"inputMin\""
                + " from" + " 	BEL.BDPTB222_MIQ_QUESTS a" + " 	left join BEL.BDPTB226_MIQ_QUEST_RL_SESSION b"
                + " 	on a.id_miq=b.id_miq" + " 	left join BEL.BDPTB225_MIQ_SESSION_PARAMS c"
                + " 	on b.ID_MIQ_PARAM=c.ID_MIQ_PARAM" + " 	left outer join BEL.BDPTB228_MIQ_PARAM_VALIDATION d"
                + " 	on c.ID_MIQ_PARAM = d.ID_MIQ_PARAM" + " where a.id_miq=? "
                + " and (b.opciones is null or b.opciones not like '%propagate=false%')";
        try
        {
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pPreparedStatement.setInt(1, nIdMiq);
            pResultSet = pPreparedStatement.executeQuery();
            mapAr = Utils.convertResultSetToJSON(pResultSet);
        }
        catch (Exception ex)
        {
            pLog.error("Error en el proceso de comprobación de disponibilidad de parámetros para el IdMiq " + nIdMiq, ex);
            throw new LogicalErrorException(500, 9999, "Internal server error", "Error en la configuración de parámetros de entradas", new Exception());
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
        }
        return formatJson(mapAr, "input");
    }

    private static LinkedHashMap<String, LinkedHashMap<String, Object>> formatJson(JSONArray datos, String strTipo)
            throws JSONException
    {
        int i;
        Map<String, LinkedHashMap<String, Object>> map = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
        for (i = 0; i < datos.length(); i++)
        {
            JSONObject foo = (JSONObject) datos.get(i);
            if (strTipo.equals("input"))
            {
                map.put((String) foo.get("inputName"), formatOneInput(foo));
            }
            else
            {
                map.put((String) foo.get("exitName"), formatOneExit(foo));
            }
        }
        return (LinkedHashMap<String, LinkedHashMap<String, Object>>) map;
    }

    private static LinkedHashMap<String, Object> formatOneInput(JSONObject mapInput) throws JSONException
    {
        Map<String, Object> mapField = new LinkedHashMap<String, Object>();
        Map<String, String> mapValidatons = new LinkedHashMap<String, String>();
        mapValidatons.put("inputType", mapInput.getString("inputType"));
        mapValidatons.put("inputLong", mapInput.getString("inputLong"));
        mapValidatons.put("inputMask", mapInput.getString("inputMask"));
        mapValidatons.put("inputMax", mapInput.getString("inputMax"));
        mapValidatons.put("inputMin", mapInput.getString("inputMin"));
        mapField.put("description", mapInput.getString("inputDescription"));
        mapField.put("validations", mapValidatons);
        return (LinkedHashMap<String, Object>) mapField;
    }

    private static LinkedHashMap<String, Object> formatOneExit(JSONObject mapInput) throws JSONException
    {
        Map<String, Object> mapField = new LinkedHashMap<String, Object>();
        mapField.put("description", mapInput.getString("exitDescription"));
        return (LinkedHashMap<String, Object>) mapField;
    }
}
