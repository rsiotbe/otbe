package com.rsi.rvia.rest.tool;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils
{
	private static Logger	pLog			= LoggerFactory.getLogger(Utils.class);
	
	/**
	 * Dado el uriInfo, compone el path original (con el nombre del parametro, no el valor)
	 * Ejemplo de salida: /cards/{id}
	 * @param pUriInfo
	 * @return
	 */
	public static String getPrimaryPath(UriInfo pUriInfo)
	{
		String strKeys = "";
		MultivaluedMap<String, String> pListParameters = pUriInfo.getPathParameters();
		Iterator<String> pIterator = pListParameters.keySet().iterator();
		while (pIterator.hasNext())
		{
			String strKeyName = (String) pIterator.next();
			strKeys += "/{";
			strKeys += strKeyName;
			strKeys += "}";
		}
		String strPath = pUriInfo.getPath();
		String[] pStrPathParts = strPath.split("/");
		strPath = "";
		for (int i = 0; i <= (pStrPathParts.length - pListParameters.size()) - 1; i++)
		{
			if (!strPath.isEmpty())
			{
				strPath += "/";
			}
			strPath += pStrPathParts[i];
		}
		return ("/" + strPath + strKeys);
	}
	
	
	
	/**
	 * Convierte un Result Set a un Json bien formado
	 * @param pResultSet
	 * @return
	 * @throws SQLException
	 * @throws JSONException
	 */
	public static JSONArray convertResultSet2JSON(ResultSet pResultSet) throws SQLException, JSONException
	{
		JSONArray pJson = new JSONArray();
		ResultSetMetaData rsmd = pResultSet.getMetaData();
		while (pResultSet.next())
		{
			int numColumns = rsmd.getColumnCount();
			JSONObject pJsonObj = new JSONObject();
			for (int i = 1; i < numColumns + 1; i++)
			{
				String column_name = rsmd.getColumnName(i);
				if (rsmd.getColumnType(i) == java.sql.Types.ARRAY)
				{
					pJsonObj.put(column_name, pResultSet.getArray(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.BIGINT)
				{
					pJsonObj.put(column_name, pResultSet.getInt(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.BOOLEAN)
				{
					pJsonObj.put(column_name, pResultSet.getBoolean(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.BLOB)
				{
					pJsonObj.put(column_name, pResultSet.getBlob(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.DOUBLE)
				{
					pJsonObj.put(column_name, pResultSet.getDouble(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.FLOAT)
				{
					pJsonObj.put(column_name, pResultSet.getFloat(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.INTEGER)
				{
					pJsonObj.put(column_name, pResultSet.getInt(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.NVARCHAR)
				{
					pJsonObj.put(column_name, pResultSet.getNString(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.VARCHAR)
				{
					pJsonObj.put(column_name, pResultSet.getString(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.TINYINT)
				{
					pJsonObj.put(column_name, pResultSet.getInt(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.SMALLINT)
				{
					pJsonObj.put(column_name, pResultSet.getInt(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.DATE)
				{
					pJsonObj.put(column_name, pResultSet.getDate(column_name));
				}
				else if (rsmd.getColumnType(i) == java.sql.Types.TIMESTAMP)
				{
					pJsonObj.put(column_name, pResultSet.getTimestamp(column_name));
				}
				else
				{
					pJsonObj.put(column_name, pResultSet.getObject(column_name));
				}
			}
			pJson.put(pJsonObj);
		}
		return pJson;
	}
	
	public static MultivaluedMap<String,String> getParam4Path(UriInfo pUriInfo){
		String strReturn = "";
		
		MultivaluedMap<String, String> pListParameters = pUriInfo.getPathParameters();
		
		return pListParameters;
	}
	
	public static String multiValuedMap2QueryString(MultivaluedMap<String,String> pMap){
		String strReturn = "";
		
		Iterator<String> pIterator = pMap.keySet().iterator();
		while (pIterator.hasNext())
		{
			String strKey = (String) pIterator.next();
			if(pMap.get(strKey) != null){
				strReturn += "&" + strKey + "=" + pMap.getFirst(strKey);
			}
		}
		
		return strReturn;
	}
	public static String getStringFromInputStream(InputStream is)
	{
		BufferedReader pBufferedReader = null;
		StringBuilder pStringBuilder = new StringBuilder();
		String strLine;
		try
		{
			pBufferedReader = new BufferedReader(new InputStreamReader(is));
			while ((strLine = pBufferedReader.readLine()) != null)
			{
				pStringBuilder.append(strLine);
			}
		}
		catch (Exception ex)
		{
			pLog.error("No es posible leer el contenido del InooutStreamReader", ex);
		}
		finally
		{
			if (pBufferedReader != null)
			{
				try
				{
					pBufferedReader.close();
				}
				catch (Exception ex)
				{
					pLog.error("No es posible cerrar el StringBuilder", ex);
				}
			}
		}
		return pStringBuilder.toString();
	}
}
