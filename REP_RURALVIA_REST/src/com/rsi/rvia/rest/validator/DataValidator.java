package com.rsi.rvia.rest.validator;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import org.json.JSONObject;
import com.rsi.rvia.rest.DDBB.DDBBConnection;
import com.rsi.rvia.rest.DDBB.DDBBFactory;
import com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider;
import com.rsi.rvia.translates.TranslateEntry;

public class DataValidator
{
	public DataValidator()
	{
	}

	public String validation(String strPathRest, Hashtable<String, String> htParams) throws Exception
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
		ResultSet pResultSet = pPreparedStatement.executeQuery();
		while ((pResultSet.next()) && (fCheck))
		{
			int nParamLong = (int) pResultSet.getInt("paramlong");
			int nParamMin = (int) Integer.parseInt((String) pResultSet.getString("parammin"));
			int nParamMax = (int) Integer.parseInt((String) pResultSet.getString("parammax"));
			String strParamMask = (String) pResultSet.getString("parammask");
			String strParamDataType = (String) pResultSet.getString("paramdatatype");
			String strParamName = (String) pResultSet.getString("paramname");
			
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
				case "date":
					String strValue = htParams.get(strParamName);
					if (!validateDate(strValue, strParamMask))
					{
						fCheck = false;
					}
					break;
				case "integer":
					strValue = htParams.get(strParamName);
					if (!validateInteger(strValue, nParamMin, nParamMax, nParamLong))
					{
						fCheck = false;
					}
					break;
				case "entidad":
					strValue = htParams.get(strParamName);
					if(!validateEntidad(strValue)){
						fCheck = false;
					}
					break;
				default:
					break;
			}
		}
		//Si check es falso ha dado un error en algun lado.
		if(!fCheck){
			JSONObject pJson = new JSONObject();
			for(JSONObject pItem : alError){
				pJson.put(pItem.getString("ParamName"), pItem);
			}
			strReturn = pJson.toString();
		}
		return strReturn;
	}

	private boolean validateDate(String strValue, String strMask)
	{
		boolean fReturn = true;
		SimpleDateFormat pDateFormat = new SimpleDateFormat(strMask);
		if ((strValue != null) && (strValue.trim().isEmpty()))
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
			return fReturn;
		}
		return fReturn;
	}

	private boolean validateInteger(String strValue, int nMin, int nMax, int nLong)
	{
		boolean fReturn = true;
		try
		{
			int nValue = Integer.parseInt(strValue);
			if ((nValue <= nMin) || (nValue >= nMax))
			{
				fReturn = false;
			}
		}
		catch (Exception ex)
		{
			fReturn = false;
			return fReturn;
		}
		return fReturn;
	}

	private boolean validateEntidad(String strValue)
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
			return fReturn;
		}
		return fReturn;
	}
}
