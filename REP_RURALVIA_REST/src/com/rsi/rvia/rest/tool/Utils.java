package com.rsi.rvia.rest.tool;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Iterator;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils
{
	
	/**
	 * Dado el uriInfo, compone el path original (con el nombre del parametro, no el valor)
	 * Ejemplo de salida: /cards/{id}
	 * @param pUriInfo
	 * @return
	 */
	public String getPrimaryPath(UriInfo pUriInfo)
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
}
