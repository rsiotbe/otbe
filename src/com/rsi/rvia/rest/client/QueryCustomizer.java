package com.rsi.rvia.rest.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
		String strPageSize = "100";
		String pageNumber = "1";
		if (request.getParameter("pagesize") != null)
		{
			strPageSize = request.getParameter("pagesize");
			if (request.getParameter("pagenumber") != null)
			{
				pageNumber = request.getParameter("pagenumber");
			}
			strQuery = paginator(strQuery, strPageSize, pageNumber);
		}
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
		JSONObject converted = Utils.convertResultSetToJSONWithTotalRegCount(pResultSet);
		JSONArray json = (JSONArray) converted.get("records");
		int nTotalNumReg = converted.getInt("totalrecordcount");
		JSONObject jsonMeta = new JSONObject();
		jsonMeta.put("pagesize", Integer.parseInt(strPageSize));
		jsonMeta.put("pagenumber", Integer.parseInt(pageNumber));
		jsonMeta.put("totalrecordcount", nTotalNumReg);
		pResultSet.close();
		pPreparedStatement.close();
		pConnection.close();
		pJsonExit.put("paginationinfo", jsonMeta);
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
			return qParsed.replace("__XX__", fieldsList).replace("__ZZ__", "");
		}
		if (sortersList != null && fieldsList == null)
		{
			return qParsed.replace("__XX__", "").replace("__ZZ__", " order by " + sortersList);
		}
		if (sortersList != null && fieldsList != null)
		{
			return qParsed.replace("__XX__", fieldsList).replace("__ZZ__", " order by " + sortersList);
		}
		return query;
	}

	private static String paginator(String query, String strPageSize, String strPageNumber) throws Exception
	{
		String strPaginator = " ";
		strPaginator = strPaginator + " SELECT paginator.*, regis_count.* FROM ( ";
		strPaginator = strPaginator + "   SELECT cachis.*, rownum rownum_NOPRINT FROM ( ";
		strPaginator = strPaginator + "     select * from ( " + query + " ) ";
		strPaginator = strPaginator + "   ) cachis ";
		strPaginator = strPaginator + "   WHERE rownum < ((" + strPageNumber + " * " + strPageSize + ") + 1 ) ";
		strPaginator = strPaginator + " ) paginator, ( ";
		strPaginator = strPaginator + "     select  count(*) c_reg_NOPRINT from ( " + query + " ) ";
		strPaginator = strPaginator + " ) regis_count ";
		strPaginator = strPaginator + " WHERE paginator.rownum_NOPRINT >= (((" + strPageNumber + " - 1) * " + strPageSize
				+ ") + 1) ";
		return strPaginator;
	}

	public static String yearMonthToFirstDayOfNextMonth(String pYearMonth) throws Exception
	{
		pYearMonth = pYearMonth + "-01";
		String partes[] = pYearMonth.split("-");
		Calendar dateDateAux = Calendar.getInstance();
		dateDateAux.set(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]) - 1, Integer.parseInt(partes[2]));
		dateDateAux.add(Calendar.MONTH, 1);
		SimpleDateFormat pSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		pYearMonth = pSimpleDateFormat.format(dateDateAux.getTime());
		return pYearMonth;
	}

	public static String yearMonthToLastDayOfPreviousMonth(String pYearMonth) throws Exception
	{
		pYearMonth = pYearMonth + "-01";
		String partes[] = pYearMonth.split("-");
		Calendar dateDateAux = Calendar.getInstance();
		dateDateAux.set(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]) - 1, Integer.parseInt(partes[2]));
		dateDateAux.add(Calendar.DATE, -1);
		SimpleDateFormat pSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		pYearMonth = pSimpleDateFormat.format(dateDateAux.getTime());
		return pYearMonth;
	}
}
