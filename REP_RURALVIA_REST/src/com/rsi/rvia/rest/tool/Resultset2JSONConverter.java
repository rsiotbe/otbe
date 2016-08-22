package com.rsi.rvia.rest.tool;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class Resultset2JSONConverter
{
	public static JSONArray convert(ResultSet pResultSet) throws SQLException, JSONException
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
