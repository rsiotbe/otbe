package com.rsi.rvia.rest.tool;

import java.util.Iterator;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

public class Utils
{
	/* Nombre temporal. Podeis cambiarlo a uno mas descriptivo */
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
}
