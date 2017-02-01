<%@page import="org.apache.catalina.util.Base64"%>
<%@page import="java.util.Hashtable"%>
<%@page import="com.rsi.rvia.rest.simulators.SimulatorEmailConfig"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URLConnection"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Properties"%>
<%@page import="java.io.ByteArrayOutputStream"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider"%>
<%@page import="com.rsi.rvia.rest.tool.Utils"%>
<%@page import="com.rsi.rvia.rest.simulators.SimulatorsManager"%>		 
<%@page import="com.rsi.rvia.rest.simulators.SimulatorConfigObjectArray"%>
<%@page import="com.rsi.rvia.rest.client.RviaRestHttpClient"%>		 
<%@page import="com.rsi.rvia.rest.tool.*"%>		 
<%@page import="com.rsi.rvia.mail.*"%>		 
<%@page import="com.rsi.Constants"%>
<%@page import="javax.ws.rs.core.*"%>
<%@page import="javax.ws.rs.client.*"%>
<%@ page language="java" pageEncoding="UTF-8"%>


<%
	Logger pLog = LoggerFactory.getLogger("generatePDF.jsp");
	
	/* Se recuperan los parametros de entrada*/
    String strPdfConfig;
	String strReturn;
    try
    {
	    /* si no se detecta como parámetro el codigo de entidad se entiende que se está enviadno como JSON la información*/
	    if (request.getParameter(Constants.SIMULADOR_PDF_CONF) == null)
	    {
	   	 	/* si la petición viene en formato JSON */
	        StringBuffer jb = new StringBuffer();
	        String line = null;
	        try
	        {
	        	request.setCharacterEncoding("UTF-8");
	            BufferedReader reader = request.getReader();
	            while ((line = reader.readLine()) != null)
	                jb.append(line);
	        }
	        catch (Exception e)
	        {
	      	  pLog.error("Error al procesar los datos JSON recibidos", e);
	        }
	        JSONObject jsonObject = new JSONObject(jb.toString());
	        strPdfConfig = jsonObject.toString();
	    }
	    else
	    {
	        /* Se recuperan los parámetros de entrada si el conetindo no es JSON*/
	        strPdfConfig = (String) request.getParameter(Constants.SIMULADOR_PDF_CONF);
	    }	
		
		pLog.info("Se recupera la configuración que le llega al jsp: \n" +
				"PdfConfig: 	    " + strPdfConfig);
	
		/* Se cargan las propiedades del servicio */
		Properties pProperties = new Properties();
		pProperties.load(Email.class.getResourceAsStream("/simulator.pdf.properties"));
		
		/* se genera el pdf */
	  	pLog.info("Se procede a llamar al servicio de generación de pdf (" + (String)pProperties.get("SERVICE_GENERATE") + ")con los datos: " + strPdfConfig);
		Client pClient = RviaRestHttpClient.getClient ();
	    WebTarget pTarget = pClient.target((String)pProperties.get("SERVICE_GENERATE"));
	  	Response pReturnPdf = pTarget.request().post(Entity.json(strPdfConfig));
	  	String strResponseData = pReturnPdf.readEntity(String.class);
	  	pLog.info("Respuesta del servicio: " + strResponseData);
	  	JSONObject pResponse = (new JSONObject(strResponseData)).getJSONObject("DOC_RENDER");
	  	/* s ecomprueba si ha devuelto error */
	  	if("0".equals(pResponse.getString("codigoRetorno")))
	  	{
	  		pLog.error("El servicio de generación de PDF ha devuelto un error");
	  		throw new Exception("Error en la respuesta del servicio");
	  	}
		String strPdfId = pResponse.getJSONObject("Respuesta").getString("id");
		pLog.info("Se recibe una petición de descarga PDF con ID " + strPdfId);
		
		/* se descarga el pdf */
		String strUrlDownload = (String)pProperties.get("SERVICE_DOWNLOAD") + strPdfId;
		ByteArrayOutputStream pPdfStream = Utils.getFileFromUrl(strUrlDownload);		
		
		/* se genera el churro  para ser respondido */
		String strBase64 = Base64.encode(pPdfStream.toByteArray());
		strReturn = Utils.generateWSResponseJsonOk("SimulatorPdf", "{\"url\":\"" + strUrlDownload + "\",\"file\":\"" + strBase64 + "\"}");
    }
    catch (Exception ex)
    {
  	  pLog.error("Error al procesar la petición de PDF", ex);
		strReturn = Utils.generateWSResponseJsonError("SimulatorPdf", 99999, ex.toString());
    }
%><%=strReturn%>
