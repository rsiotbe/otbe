package com.rsi.rvia.rest.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.tool.Utils;

public class QueryCustomizer
{
	private static Logger	pLog			= LoggerFactory.getLogger(QueryCustomizer.class);
	private static String[]	_reserved	= { "select", "update", "delete", "insert", "alter", "drop", "create" };

	public static String process(HttpServletRequest request, String strQuery) throws Exception
	{
		String strFieldslist = request.getParameter("fieldslist");
		if (strFieldslist == null)
		{
			strFieldslist = "*";
		}
		strQuery = getParsedQuery(strQuery, strFieldslist, request.getParameter("sorterslist"));
		pLog.info("Query resuelta: " + strQuery);
		// TODO: Evitar el intento de validación de token cuando se realiza login, y por tanto, la salida es un callback
		// FIXME: Activar seguridad en producción: OJO: En el caso de login no debe validarse el token.
		/*
		 * String JWT = request.getHeader("Authorization"); HashMap<String, String> pParamsToInject =
		 * ManageJWToken.validateJWT(JWT); if (pParamsToInject == null) { throw new LogicalErrorException(401, 9999,
		 * "Unauthorized", "Sesión no válida", new Exception()); }
		 */
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleCIP);
		pPreparedStatement = pConnection.prepareStatement(strQuery);
		pResultSet = pPreparedStatement.executeQuery();
		JSONObject pJson = new JSONObject();
		JSONObject pJsonExit = new JSONObject();
		JSONArray json = Utils.convertResultSetToJSON(pResultSet);
		pResultSet.close();
		pPreparedStatement.close();
		pConnection.close();
		pJsonExit.put("data", json);
		pJson.put("response", pJsonExit);
		return pJson.toString();
	}

	private static String protectInject(String strFields)
	{
		if (strFields == null)
			return null;
		int i;
		for (i = 0; i < _reserved.length; i++)
		{
			strFields = strFields.replaceAll("(?i)" + _reserved[i], "");
		}
		return strFields;
	}

	private static String setFieldsList(String pFieldsList) throws Exception
	{
		String fieldsList = null;
		pFieldsList = protectInject(pFieldsList);
		if (pFieldsList != null)
			fieldsList = pFieldsList.replaceAll("([A-z]+)", "\"$1\"");
		return fieldsList;
	}

	private static String setSortersList(String pSortersList) throws Exception
	{
		String sortersList = null;
		pSortersList = protectInject(pSortersList);
		if (pSortersList != null)
			sortersList = pSortersList.replaceAll("([A-z]+)", "\"$1\"").replaceAll("\"(desc)\"", "$1");
		return sortersList;
	}

	private static String getParsedQuery(String query, String fieldsList, String sortersList) throws Exception
	{
		fieldsList = setFieldsList(fieldsList);
		sortersList = setSortersList(sortersList);
		if (sortersList == null && fieldsList == null)
			return query;
		String qParsed = "Select __XX__ from ( ";
		qParsed = qParsed + query;
		qParsed = qParsed + " ) __ZZ__ ";
		if (sortersList == null && fieldsList != null)
		{
			String rr = qParsed.replace("__XX__", fieldsList).replace("__ZZ__", "");
			return qParsed.replace("__XX__", fieldsList).replace("__ZZ__", "");
		}
		if (sortersList != null && fieldsList == null)
		{
			String rr = qParsed.replace("__XX__", "").replace("__ZZ__", " order by " + sortersList);
			return qParsed.replace("__XX__", "").replace("__ZZ__", " order by " + sortersList);
		}
		if (sortersList != null && fieldsList != null)
		{
			String rr = qParsed.replace("__XX__", fieldsList).replace("__ZZ__", " order by " + sortersList);
			return qParsed.replace("__XX__", fieldsList).replace("__ZZ__", " order by " + sortersList);
		}
		return query;
	}
}
