<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="org.json.JSONException"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.sql.Types"%>
<%@page import="java.sql.CallableStatement"%>
<%@page import="org.json.JSONArray"%>
<%@page import="com.rsi.rvia.rest.tool.Utils"%>
<%@page import="oracle.jdbc.OracleTypes"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.rsi.rvia.rest.client.MiqAdminValidator"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory"%>
<%
	pLog.info("GetNews ::: Start");
	JSONObject pJsonResponse = new JSONObject();
	String strMethod = request.getMethod();
	pLog.info("GetNews ::: Start ::: strMethod " + strMethod);
	response.setHeader("content-type", "application/json");
	
	switch (MethodType.byName(strMethod))
	{
		case GET:
			String codUser = request.getParameter("codUser");
			String isumProfile = request.getParameter("isumProfile");
			String codNrbe = request.getParameter("codNrbe");
			JSONArray pJsonResult = getNews(codNrbe, isumProfile, codUser);
			pJsonResponse.put("news", pJsonResult);
			%><%=Utils.generateWSResponseJsonOk("news", pJsonResponse.toString())%>
<%
			break;
		case POST:
			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
		    JSONObject pJsonInput = new JSONObject(br.readLine());
		    JSONArray pViewed = pJsonInput.getJSONArray("viewed");
		    pJsonResponse = updateNews(pJsonInput.getString("codUser"), pViewed , pJsonInput.get("noMore"));
		    %><%=Utils.generateWSResponseJsonOk("news", pJsonResponse.toString())%>
<%
			break;
		default:
			pJsonResponse.put(strErrorCode, -10);
			pJsonResponse.put(strErrorMessage, "Method Erroneo");
			pLog.error("GetNews ::: Inicio ::: Method erroneo ");	
		    %><%=Utils.generateWSResponseJsonOk("news", pJsonResponse.toString())%>
<%
			break;
	}	
%>
<%!

String strErrorCode = "errorCode";
String strErrorMessage = "errorMessage";
Logger pLog = LoggerFactory.getLogger("getNews.jsp");

/*
 * Devuelve el listado de noticias para mostrar al usuario.
 */
public JSONArray getNews (String strCodNrbe, String strProfile, String strUser) throws Exception
{
	pLog.info("GetNews ::: getNews ::: Start ");
	pLog.info("getNews ::: updateNews ::: User " + strUser);
	Connection pConnection = null;
	JSONArray pJsongetNewsResponse = null;
	String strQuery = "{call BEL.PK_COMUNICACION_CLIENTE.getNews(?,?,?,?)}";
	CallableStatement pCallableStatement = null;
	try
	{
		pLog.info("GetNews ::: getNews ::: DDBBProvider ");
		pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
		
		pConnection.setAutoCommit(false);
		int iResultCode = 0;
		String strError;
		pLog.info("GetNews ::: getNews ::: pCallableStatement ");
	    pCallableStatement = pConnection.prepareCall(strQuery);
		pCallableStatement.setString(1, strCodNrbe);
	  	pCallableStatement.setString(2, strProfile);
		pCallableStatement.setString(3, strUser);
		pCallableStatement.registerOutParameter(4, OracleTypes.CURSOR);
		
		pCallableStatement.executeUpdate();
		ResultSet pResultSet = (ResultSet) pCallableStatement.getObject(4);
		
		pJsongetNewsResponse = Utils.convertResultSetToJSON(pResultSet);
		pLog.info("GetNews ::: getNews ::: pJsongetNewsResponse " + pJsongetNewsResponse);
	}
	catch (Exception e)
	{
		pLog.error("GetNews ::: getNews ::: pCallableStatement Exception " + e.getMessage());		
	}
	finally
	{
	    DDBBPoolFactory.closeDDBBObjects(pLog, null, pCallableStatement, pConnection);
	}  
	return pJsongetNewsResponse;
}

/*
 * Actualiza las visualizaciones del usuario 
 */
public JSONObject updateNews (String strUser, JSONArray pViews, Object pNoMore)
{
	JSONObject pJsongetNewsResponse = new JSONObject();
	pLog.info("getNews ::: updateNews ::: Start ");
	pLog.info("getNews ::: updateNews ::: User " + strUser);
	String strQueryNoMore = "{call BEL.PK_COMUNICACION_CLIENTE.noMore(?,?,?,?)}";
	String strQueryViews  = "{call BEL.PK_COMUNICACION_CLIENTE.viewed(?,?,?,?)}";
	int iErrorCode = 0;
	String strErrorMessage = "";
	try
	{
		try
		{
			if ((pViews != null && pViews.length()>0) || (pNoMore != null))
			{
				  for(int i=0; i<pViews.length(); i++)
				  {
					  int icodNew = pViews.getInt(i);
					  iErrorCode = -1;
					  pLog.info("getNews ::: updateNews ::: Viewed " + icodNew);
					  pJsongetNewsResponse = callBBDD(icodNew, strUser, strQueryViews);
				  }
				  if (pNoMore != null )
				  {
					  iErrorCode = -2;
					  pLog.info("getNews ::: updateNews ::: NoMore " + pNoMore);
					  pJsongetNewsResponse = callBBDD(Integer.parseInt(pNoMore.toString()), strUser, strQueryNoMore);			  
				  }
			}
			pJsongetNewsResponse.put(this.strErrorCode, 0);
		}
		catch (Exception e)
		{
			pLog.error("getNews ::: updateNews ::: Error " + e.getMessage());
			pJsongetNewsResponse.put(this.strErrorCode, iErrorCode);
			pJsongetNewsResponse.put(this.strErrorMessage, e.getMessage());			
		}
	}
	catch (JSONException je)
	{
		pLog.error("GetNews ::: updateNews ::: Error Json ", je);
	}
	return pJsongetNewsResponse;
}

public JSONObject callBBDD(int strCod, String strUser, String strQuery) throws Exception
{
	JSONObject pJsonUpdateResponse = new JSONObject();
	Connection pConnection = null;
	CallableStatement pCallableStatement = null;
		
	try
	{
		pLog.info("GetNews ::: callBBDD ::: Start");
		pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);		
		pConnection.setAutoCommit(true);
		int iResultCode = 0;
		String strError;
		pLog.info("GetNews ::: callBBDD ::: pCallableStatement ");
	    pCallableStatement = pConnection.prepareCall(strQuery);
		pCallableStatement.setInt(1, strCod);
	  	pCallableStatement.setString(2, strUser);
		pCallableStatement.registerOutParameter(3, Types.NUMERIC);
		pCallableStatement.registerOutParameter(4, Types.VARCHAR);
		
		pCallableStatement.execute();
		iResultCode = pCallableStatement.getInt(4);
		pLog.info("GetNews ::: callBBDD ::: pCallableStatement iResultCode:" + iResultCode);
		if (iResultCode != 0)
		{
			strError = pCallableStatement.getString(5);
			pLog.info("GetNews ::: callBBDD ::: pCallableStatement strError:" + strError);
			pJsonUpdateResponse.put(this.strErrorMessage, strError);
		}
		pJsonUpdateResponse.put(this.strErrorCode, iResultCode);
	}
	catch (Exception e)
	{
		pLog.info("GetNews ::: callBBDD ::: pCallableStatement Exception " + e.getMessage());
		throw e;
	}
	finally
	{
	    DDBBPoolFactory.closeDDBBObjects(pLog, null, pCallableStatement, pConnection);
	}
	return pJsonUpdateResponse;
}

public enum MethodType
{
    GET("GET"), POST("POST"), PATCH("PATCH"), PUT("PUT"), DELETE("DELETE");
    
    private final String strMethod;
    
    private MethodType(String strMethod)
    {
    	this.strMethod = strMethod;
    }
        
    public static MethodType byName(String strMethod)
    {
    	for (MethodType s : values())
    	{
			if (s.getMethod().equals(strMethod)) return s;
		}
    	return null;
    }
    
    public String getMethod()
    {
    	return strMethod;
    }
};
%>

