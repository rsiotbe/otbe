package com.rsi.rvia.rest.validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBConnection;
import com.rsi.rvia.rest.DDBB.DDBBFactory;
import com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider;

public class DataValidator
{
	private static Logger	pLog	= LoggerFactory.getLogger(DataValidator.class);
	public DataValidator()
	{
	}

	public static String validation(String strPathRest, Hashtable<String, String> htParams) throws Exception
	{
		boolean fCheck = true;
		ArrayList<JSONObject> alError = new ArrayList();
		String strReturn = "{}";
		String strQuery = "select * from" + "bel.bdptb222_miq_quests z," + "bel.bdptb226_miq_quest_rl_session x,"
				+ "bel.bdptb225_miq_session_params a," + "BEL.BDPTB228_MIQ_PARAM_VALIDATION b" + "where z.path_rest='"
				+ strPathRest + "'" + "and z.id_miq=x.id_miq" + "and x.id_miq_param= a.id_miq_param"
				+ "and a.id_miq_param=b.id_miq_param";
		
		DDBBConnection pDDBBConnection = DDBBFactory.getDDBB(DDBBProvider.OracleBDES, "beld");
		PreparedStatement pPreparedStatement = pDDBBConnection.prepareStatement(strQuery);
		pLog.info("Se prepara la Query para la validación: " + pPreparedStatement.toString());
		ResultSet pResultSet = pPreparedStatement.executeQuery();
		pLog.info("Query ejecutada con exito.");
		while (pResultSet.next())
		{
			int nParamLong = 0;
			int nParamMin = 0;
			int nParamMax = 0;
			String strParamMask = null;
			String strParamDataType = null;
			String strParamName = null;
			String strAliasName = null;
			try
			{
				nParamLong = (int) pResultSet.getInt("paramlong");
				if ((String) pResultSet.getString("parammin") != null)
				{
					nParamMin = (int) Integer.parseInt((String) pResultSet.getString("parammin"));
				}else{
					nParamMin = -1;
				}
				if ((String) pResultSet.getString("parammax") != null)
				{
					nParamMax = (int) Integer.parseInt((String) pResultSet.getString("parammax"));
				}else{
					nParamMin = -1;
				}
				strParamMask = (String) pResultSet.getString("parammask");
				strParamDataType = (String) pResultSet.getString("paramdatatype");
				strParamName = (String) pResultSet.getString("paramname");
				strAliasName = (String) pResultSet.getString("aliasname");
			}
			catch (Exception ex)
			{
				fCheck = false;
				continue;
			}
			JSONObject pJsonObj = new JSONObject();
			pJsonObj.put("ParamName", strParamName);
			pJsonObj.put("ParamType:", strParamDataType);
			pJsonObj.put("ParamMask", strParamMask);
			pJsonObj.put("ParamLong", nParamLong);
			pJsonObj.put("ParamMin", nParamMin);
			pJsonObj.put("ParamMax", nParamMax);
			alError.add(pJsonObj);
			if (strParamName == null)
			{
				continue;
			}
			switch (strParamDataType)
			{
				case "Date":
					String strValue = htParams.get(strParamName);
					if(strValue == null){
						strValue = htParams.get(strAliasName);
					}
					pLog.info("Validan Date: " + strValue);
					if (!validateDate(strValue, strParamMask))
					{
						fCheck = false;
					}
					break;
				case "Integer":
					strValue = htParams.get(strParamName);
					if(strValue == null){
						strValue = htParams.get(strAliasName);
					}
					pLog.info("Validan Integer: " + strValue);
					if (!validateInteger(strValue, nParamMin, nParamMax, nParamLong))
					{
						fCheck = false;
					}
					break;
				case "Entidad":
					strValue = htParams.get(strParamName);
					if(strValue == null){
						strValue = htParams.get(strAliasName);
					}
					pLog.info("Validan Entida: " + strValue);
					if (!validateEntidad(strValue))
					{
						fCheck = false;
					}
					break;
				case "String":
					strValue = htParams.get(strParamName);
					if(strValue == null){
						strValue = htParams.get(strAliasName);
					}
					pLog.info("Validan String: " + strValue);
					if (!validateString(strValue, nParamMin, nParamMax, nParamLong))
					{
						fCheck = false;
					}
					break;
				default:
					break;
			}
		}
		// Si fCheck es falso ha dado un error en algun lado.
		if (!fCheck)
		{
			pLog.info("Validacion fallida. Devolviendo JSON con información de los campos.");
			JSONObject pJson = new JSONObject();
			for (JSONObject pItem : alError)
			{
				pJson.put(pItem.getString("ParamName"), pItem);
			}
			strReturn = pJson.toString();
		}
		return strReturn;
	}

	private static boolean validateDate(String strValue, String strMask)
	{
		boolean fReturn = true;
		SimpleDateFormat pDateFormat = new SimpleDateFormat(strMask);
		if ((strValue != null) || (strValue.trim().isEmpty()))
		{
			fReturn = false;
		}
		Date fechaIni;
		try
		{
			fechaIni = pDateFormat.parse(strValue);
		}
		catch (Exception ex)
		{
			fReturn = false;
		}
		return fReturn;
	}

	private static boolean validateInteger(String strValue, int nMin, int nMax, int nLong)
	{
		boolean fReturn = true;
		try
		{
			int nValue = Integer.parseInt(strValue);
			if((nMin != -1)){
				if(nValue <= nMin){
					fReturn = false;
				}
			}
			if(nMax != -1){
				if(nValue >= nMax){
					fReturn = false;
				}
			}
		}
		catch (Exception ex)
		{
			fReturn = false;
		}
		return fReturn;
	}

	private static boolean validateEntidad(String strValue)
	{
		boolean fReturn = true;
		try
		{
			String strQuery = "select cod_nrbe_en from prox01.sx_entidad " + " where cod_nrbe_en=?";
			DDBBConnection pDDBBConnection = DDBBFactory.getDDBB(DDBBProvider.OracleCIP, "cip");
			PreparedStatement pPreparedStatement = pDDBBConnection.prepareStatement(strQuery);
			pPreparedStatement.setString(1, strValue);
			ResultSet pResultSet = pPreparedStatement.executeQuery();
			if (!pResultSet.next())
			{
				fReturn = false;
			}
			pResultSet.close();
			pPreparedStatement.close();
		}
		catch (Exception ex)
		{
			fReturn = false;
		}
		return fReturn;
	}
	
	private static boolean validateString(String strValue, int nMin, int nMax, int nLong)
	{
		boolean fReturn = true;
		try
		{
			if(nLong != 0){
				if ((strValue.length() <= nMin) || (strValue.length() >= nMax) || (strValue.length() != nLong))
				{
					fReturn = false;
				}
			}
			
		}
		catch (Exception ex)
		{
			fReturn = false;
			return fReturn;
		}
		return fReturn;
	}
}
