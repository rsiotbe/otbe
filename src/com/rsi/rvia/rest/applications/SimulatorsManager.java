package com.rsi.rvia.rest.applications;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.error.exceptions.ApplicationException;

public class SimulatorsManager
{
	private static Logger pLog = LoggerFactory.getLogger(SimulatorsManager.class);

	public static String getFunctions4Entity(String strEntity, String strFunctions, String strJSType)
			throws JSONException, ApplicationException
	{
		String strReturn = "{}";
		JSONObject pJson = new JSONObject();
		Hashtable<String, String> htFunctions = new Hashtable<String, String>();
		Hashtable<String, String> htConfig;
		try
		{
			htFunctions = getFunctionsFromDDBB(strFunctions, strJSType);
			htConfig = getParamConfigFromDDBB(strEntity);
			String strJSMin = "";
			/* Se sustituyen en todos los algoritmos, y se añaden a el JSON de respuesta */
			for (Enumeration e = htFunctions.keys(); e.hasMoreElements();)
			{
				String strFunction = (String) e.nextElement();
				strJSMin = htFunctions.get(strFunction);
				for (Enumeration en = htConfig.keys(); en.hasMoreElements();)
				{
					String strReplace = (String) en.nextElement();
					String strValue = htConfig.get(strReplace);
					strJSMin = strJSMin.replace(strReplace, strValue);
				}
				pLog.debug("Añadiendo algoritmo al JSON.");
				// pJson.put(strFunction, "var " + strFunction + " = " + strJSMin);
				pJson.put(strFunction, strJSMin);
			}
			strReturn = pJson.toString();
		}
		catch (ApplicationException ex)
		{
			throw ex;
		}
		return strReturn;
	}

	private static Hashtable<String, String> getFunctionsFromDDBB(String strFunctions, String strJSType)
			throws ApplicationException
	{
		Hashtable<String, String> htReturn = new Hashtable<String, String>();
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		if (strJSType == null || strJSType.trim().isEmpty())
		{
			strJSType = "min";
		}
		try
		{
			String[] pPartes = strFunctions.split(";");
			String strQuery = "select * from bdptb235_functions_simuladores";
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pResultSet = pPreparedStatement.executeQuery();
			if ("normal".equals(strJSType))
			{
				strJSType = "javascript";
			}
			else
			{
				strJSType = "javascript_min";
			}
			/*
			 * Recupera el JS minificado, dentro de la tabla BDPTB235_FUNCTIONS_SIMULADORES tambien esta la versión sin
			 * minificar. Si se realiza un cambio en cualquiera de las dos versión hay que modificarlo en la otra. La
			 * aplicación que se ha usado para minificar es: https://jscompress.com/
			 */
			/* Se recogen de la BBDD las funciones y su JS que se reciben por parametro */
			while (pResultSet.next())
			{
				String strFunction = (String) pResultSet.getString("funcion");
				for (String strItem : pPartes)
				{
					if (strItem.equals(strFunction))
					{
						String strJSMin = (String) pResultSet.getString(strJSType);
						pLog.trace("Funcion encontrada y recuperada: " + strFunction);
						htReturn.put(strFunction, "var " + strItem + " = " + strJSMin + ";");
					}
				}
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al realizar la consulta a la BBDD.");
			throw new ApplicationException(500, 999999, "No se han podido recuperar las funciones.", "Error al obtener obtener las funciones JS para simuladores.", ex);
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
		}
		return htReturn;
	}

	public static Hashtable<String, String> getParamConfigFromDDBB(String strEntity)
	{
		Hashtable<String, String> htReturn = new Hashtable<String, String>();
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		try
		{
			String strQuery = "select * from bdptb234_param_simuladores where entidad = ?";
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setString(1, strEntity);
			pResultSet = pPreparedStatement.executeQuery();
			/* Recupera los parametros de configuración */
			while (pResultSet.next())
			{
				String strKey = (String) pResultSet.getString("clave");
				String strValue = (String) pResultSet.getString("valor");
				String strReplace = "";
				/* Se mira que tipo de parametro es para sustituirlo */
				if ("minCuota".equals(strKey))
				{
					strReplace = "__MIN_FEE__";
				}
				else if ("maxCuota".equals(strKey))
				{
					strReplace = "__MAX_FEE__";
				}
				else if ("minImporte".equals(strKey))
				{
					strReplace = "__MIN_INITIAL_AMOUNT__";
				}
				else if ("maxImporte".equals(strKey))
				{
					strReplace = "__MAX_INITIAL_AMOUNT__";
				}
				else if ("minPlazos".equals(strKey))
				{
					strReplace = "__MIN_DEADLINES__";
				}
				else if ("maxPlazos".equals(strKey))
				{
					strReplace = "__MAX_DEADLINES__";
				}
				htReturn.put(strReplace, strValue);
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al realizar la consulta a la BBDD.");
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
		}
		return htReturn;
	}
}
