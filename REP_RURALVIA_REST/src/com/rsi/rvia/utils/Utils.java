package com.rsi.rvia.utils;

import java.util.Iterator;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

public class Utils
{
	/* Nombre temporal. Podeis cambiarlo a uno mas descriptivo */
	public String getPrimaryPath(UriInfo uriInfo)
	{
		String strKeys = "";
		MultivaluedMap<String, String> pListParameters = uriInfo.getPathParameters();
		Iterator<String> it = pListParameters.keySet().iterator();
		while (it.hasNext())
		{
			String strKeyName = (String) it.next();
			strKeys += "/{";
			strKeys += strKeyName;
			strKeys += "}";
		}
		String strPath = uriInfo.getPath();
		String[] lStrPathParts = strPath.split("/");
		strPath = "";
		for (int i = 0; i <= (lStrPathParts.length - pListParameters.size()) - 1; i++)
		{
			if (!strPath.isEmpty())
			{
				strPath += "/";
			}
			strPath += lStrPathParts[i];
		}
		return ("/" + strPath + strKeys);
	}
}
