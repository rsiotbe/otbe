package com.rsi.rvia.rest.tool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.tool.Utils;

public class ServiceHelper
{
	private static Logger	pLog	= LoggerFactory.getLogger(ServiceHelper.class);
	private static Connection pConnection = null;
	
	public void ServiceHelper(){}
	public static JSONObject getHelp(int nIdMiq) throws Exception, SQLException{
		JSONObject jsonHelp = null;
		JSONObject jsonSInfo = null;
		JSONObject jsonAux = null;
		JSONObject jsonAux2 = null;
		try{
			if(pConnection == null)
				pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			jsonHelp = new JSONObject();			
			jsonAux =  new JSONObject();
			jsonSInfo = getServiceInfo(nIdMiq).getJSONObject(0);
			jsonSInfo.put("inputFields", getAvailableInputs(nIdMiq));
			jsonSInfo.put("exitFields", getAvailableExits(nIdMiq));
			jsonHelp.put("response", jsonSInfo);
		}
		catch(Exception ex){
			throw new Exception();
		}
		finally{
			pConnection.close();
		}
		return jsonHelp;
	}	
	public static JSONObject getHelp(String strPathRest) throws Exception, SQLException{
		pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		int nIdMiq = 0;
		strPathRest = strPathRest.replace("/help", "");
		String strQuery = " select id_miq" +
				" from  BEL.BDPTB222_MIQ_QUESTS" +
				" where path_rest=?" ;
		try
		{
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setString(1, strPathRest);
			pResultSet = pPreparedStatement.executeQuery();		
			if(pResultSet.next()){
				nIdMiq = pResultSet.getInt("id_miq");
			}
		}catch(Exception ex){
			pLog.error(strQuery);
		}finally{
			pResultSet.close();
			pPreparedStatement.close();
			//pConnection.close();
		}
		return getHelp (nIdMiq);
	}		
	private static JSONArray getServiceInfo (int nIdMiq) throws SQLException{
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		JSONArray jsonAr = null;
		String strQuery = " select" +
				"  path_rest \"url\"" +
				" ,miq_name \"serviceName\"" +
				" ,miq_description \"serviceDescription\"" +
				" from  BEL.BDPTB222_MIQ_QUESTS" +
				" where id_miq=2011" ;		
		try
		{
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pResultSet = pPreparedStatement.executeQuery();					
			jsonAr = Utils.convertResultSet2JSON(pResultSet);			
		}catch(Exception ex){
			pLog.error(strQuery);
		}finally{
			pResultSet.close();
			pPreparedStatement.close();
		}
		return jsonAr;	
	}
	private static JSONObject getAvailableExits (int nIdMiq) throws SQLException, JSONException{
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		JSONArray jsonAr = null;
		String strQuery = " select" +
				" 	 c.exitname \"exitName\"" +
				" 	,case" +
				" 		when trim(c.exitdesc) is not null then c.exitdesc" +
				" 		when trim(c.exitdesc) is null then c.exitname else c.exitdesc end  \"exitDescription\"" +
				" from" +
				" 	BEL.BDPTB222_MIQ_QUESTS a" +
				" 	left join BEL.BDPTB233_MIQ_QUEST_RL_EXITS b" +
				" 	on a.id_miq=b.id_miq" +
				" 	left join BEL.BDPTB232_MIQ_EXITS c" +
				" 	on b.ID_MIQ_exit=c.ID_MIQ_exit" +
				" where a.id_miq= ?" +
				" and b.miq_verb='GET'" +
				" and b.opciones not like '%propagate=false%'" +
				" and c.exithierarchy like 'response.data.%'" ;
		try
		{
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setInt(1, nIdMiq);
			pResultSet = pPreparedStatement.executeQuery();					
			jsonAr = Utils.convertResultSet2JSON(pResultSet);			
		}catch(Exception ex){
			pLog.error(strQuery);
		}finally{
			pResultSet.close();
			pPreparedStatement.close();
		}
		return formatJson(jsonAr,"exit");		
	}
	private static JSONObject getAvailableInputs (int nIdMiq) throws SQLException, JSONException{
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;	
		JSONArray jsonAr = null;
		String strQuery = " select" +
				" 	 case when trim(aliasname) is null then paramname else aliasname end  \"inputName\"" +
				" 	,case" +
				" 		when trim(paramdesc) is not null then paramdesc" +
				" 		when trim(aliasname) is null then paramname else aliasname end  \"inputDescription\"" +
				" 	,case" +
				" 		when trim(paramdatatype) = 'Entidad'  then 'String'" +
				" 		when trim(paramdatatype) is null then 'n/a' else paramdatatype end \"inputType\"" +
				" 	,case when trim(to_char(paramlong)) is null then 'n/a' else trim(to_char(paramlong)) end  \"inputLong\"" +
				" 	,case when trim(parammask) is null then 'n/a' else parammask end  \"inputMask\"" +
				" 	,case when trim(to_char(parammax)) is null then 'n/a' else trim(to_char(parammax)) end  \"inputMax\"" +
				" 	,case when trim(to_char(parammin)) is null then 'n/a' else trim(to_char(parammin)) end  \"inputMin\"" +
				" from" +
				" 	BEL.BDPTB222_MIQ_QUESTS a" +
				" 	left join BEL.BDPTB226_MIQ_QUEST_RL_SESSION b" +
				" 	on a.id_miq=b.id_miq" +
				" 	left join BEL.BDPTB225_MIQ_SESSION_PARAMS c" +
				" 	on b.ID_MIQ_PARAM=c.ID_MIQ_PARAM" +
				" 	left outer join BEL.BDPTB228_MIQ_PARAM_VALIDATION d" +
				" 	on c.ID_MIQ_PARAM = d.ID_MIQ_PARAM" +
				" where a.id_miq=?" ;				
		try
		{
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setInt(1, nIdMiq);
			pResultSet = pPreparedStatement.executeQuery();					
			jsonAr = Utils.convertResultSet2JSON(pResultSet);			
		}catch(Exception ex){
			pLog.error(strQuery);
		}finally{
			pResultSet.close();
			pPreparedStatement.close();
		}
		
		return formatJson(jsonAr,"input");		
	}
	private static JSONObject formatJson(JSONArray datos, String strTipo) throws JSONException{
		int i;
		JSONObject json = new JSONObject();
		for(i=0; i<datos.length(); i++){
			JSONObject foo =(JSONObject) datos.get(i);
			if(strTipo.equals("input")){
				json.put((String) foo.get("inputName"), formatOneInput(foo));
			}
			else{
				json.put((String) foo.get("exitName"), formatOneExit(foo));
			}
		}		
		return json;
	}	
	private static JSONObject formatOneInput(JSONObject jsonInput) throws JSONException{
		JSONObject jsonField = new JSONObject();
		JSONObject jsonValidatons= new JSONObject();
		jsonValidatons.put("inputType",jsonInput.getString("inputType"));
		jsonValidatons.put("inputLong",jsonInput.getString("inputLong"));
		jsonValidatons.put("inputMask",jsonInput.getString("inputMask"));
		jsonValidatons.put("inputMax",jsonInput.getString("inputMax"));
		jsonValidatons.put("inputMin",jsonInput.getString("inputMin"));
		jsonField.put("description", jsonInput.getString("inputDescription"));
		jsonField.put("validations", jsonValidatons);
		return jsonField;
	}
	private static JSONObject formatOneExit(JSONObject jsonInput) throws JSONException{
		JSONObject jsonField = new JSONObject();
		jsonField.put("description", jsonInput.getString("exitDescription"));
		return jsonField;
	}
}
