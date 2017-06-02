<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@page import="java.io.*"%>
<%@page import="java.sql.*"%>
<%@page import="java.util.*"%>
<%@page import= "com.rsi.rvia.rest.DDBB.*"%>
<%@page import= "com.rsi.rvia.rest.tool.*"%>
<%@page import= "com.rsi.rvia.rest.DDBB.DDBBPoolFactory.*"%>

<%! 
private String VERSION_FILE_NAME = "/tool/version.info";
private String CLASS_JNDI = "";
private String APP_NAME = "";
private String DB_JNDI = "";
private String DB_QUERY = "";
private String SERVER_USERNAME = "";
private String SERVER_PASS = "";

private static String CheckVersionDate(InputStream pInputStream) throws Exception
{
	Scanner pScanner = null;
	String sFechaToSearch = "Version-Date:";
	String strResult = "";
	String strContent = "";
	strContent = getStringFromInputStream (pInputStream);
	if(strContent.contains(sFechaToSearch))
	{
		int nStart = strContent.indexOf(sFechaToSearch) + sFechaToSearch.length();
		strResult = strContent.substring(nStart).trim();
	}
	return strResult;
}	

private static String getStringFromInputStream(InputStream pInputStream) throws Exception
{
	BufferedReader pBufferedReader = null;
	StringBuilder pStringBuilder = new StringBuilder();
	String strLine;
	pBufferedReader = new BufferedReader(new InputStreamReader(pInputStream));
	while ((strLine = pBufferedReader.readLine()) != null) 
	{
		pStringBuilder.append(strLine);
	}
	pBufferedReader.close();
	return pStringBuilder.toString();
}

private static String getMemStatus()
{
	double dbTotal;
	double dbFree;
	double dbUsed;
	double dbPercent;
	String strReturn;
	dbFree = ((Runtime.getRuntime().freeMemory() / 1024.0) /1024.0);
	dbTotal = ((Runtime.getRuntime().maxMemory() / 1024.0) /1024.0);
	dbUsed = dbTotal-dbFree;
	dbPercent = (dbUsed * 100)/ dbTotal;
	String strUsed = String.format("%.1f",dbUsed);
	String strTotal = String.format("%.1f",dbTotal);
	String strPercent = String.format("%.0f",dbPercent);
	strReturn = "{\"used\":\"" + strUsed + "\",";
	strReturn +=  "\"total\":\"" + strTotal + "\",";
	strReturn +=  "\"percent\":\"" + strPercent + "\"}";
	return strReturn;
}

private long getTimeInMillis()
{
	java.util.Date dtAux = new java.util.Date();
	return dtAux.getTime();
}

private String checkDB() throws Exception
{
	String strReturn = "";
	long lT1;
	long lT2;
	long lTotal;
	Connection pConnection = null;
	PreparedStatement pPreparedStatement = null;
	ResultSet pResultSet = null;
	try
	{	    
		lT1 = getTimeInMillis();
		pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
        pPreparedStatement = pConnection.prepareStatement("Select * from " + AppConfiguration.getInstance().getProperty("BELScheme").trim() + ".bdptb229_Css_Multibank");
        pResultSet = pPreparedStatement.executeQuery();
		lT2 = getTimeInMillis();
		lTotal = lT2-lT1;
    }
    catch (Exception ex)
    {
        throw ex;
    }
    finally
    {
        DDBBPoolFactory.closeDDBBObjects(null, pResultSet, pPreparedStatement, pConnection);
    }
	strReturn += lTotal;
	return strReturn;
}


%>
<%
	String strResponse = "";
	try
	{		
		ServletContext pServletContext = session.getServletContext();
		String strDate = CheckVersionDate(pServletContext.getResourceAsStream(VERSION_FILE_NAME));
		String strMemory = getMemStatus();
		String strDDBB_Value = checkDB();
		strResponse = Utils.generateWSResponseJsonOk("checkStatus", "{\"ESTADO\":\"OK\"}");
	}
	catch(Exception ex)
	{
		strResponse = Utils.generateWSResponseJsonError("checkStatus", 999999, "ERROR");
	}
	
	/* se imprime la salida en la respuesta */
	out.print(strResponse);
%>
