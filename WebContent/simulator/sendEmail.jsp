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
	String 	PDF_RENDERER_ENDPOINT = "http://docrender.risa/docrender/rest/render/generate/pdf";
	String	PDF_RENDERER_DOWNLOAD = "http://docrender.risa/docrender/rest/render/download/pdf/";
	
	Logger pLog = LoggerFactory.getLogger("sendEmail.jsp");
	
	/* Se recuperan los parametros de entrada*/
	JSONObject pJson = new JSONObject();
    JSONObject pJsonData = new JSONObject();
    String strResponse;
    int nSimulatorId;
    String strNRBE;
    String strNRBEName;
    String strSimulatorType;
    String strSimulatorName;
    String strLanguage;
    String strPdfConfig;
    String strEmailTemplate;
    String strEmailUserName;
    String strEmailMail;
    String strEmailTelephone;
    String strEmailComment;
    boolean fIsCustomer;
    String strNif;
    String strEmailFrom;
    String strHtmlTemplate;
    
    /* si no se detecta como parámetro el codigo de entidad se entiende que se está enviadno como JSON la información*/
    if (request.getParameter(Constants.SIMULADOR_NRBE) == null)
    {
   	 	/* si la petición viene en formato JSON */
        StringBuffer jb = new StringBuffer();
        String line = null;
        try
        {
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null)
                jb.append(line);
        }
        catch (Exception e)
        {
      	  pLog.error("Error al procesar los datos JSON recibidos", e);
        }
        JSONObject jsonObject = new JSONObject(jb.toString());
        nSimulatorId  = jsonObject.optInt(Constants.SIMULADOR_ID);
        strNRBE = jsonObject.optString(Constants.SIMULADOR_NRBE);
        strNRBEName = jsonObject.optString(Constants.SIMULADOR_NRBE_NAME);
        strSimulatorType = jsonObject.optString(Constants.SIMULADOR_TYPE);
        strSimulatorName = jsonObject.optString(Constants.SIMULADOR_SIMPLE_NAME);
        strLanguage = jsonObject.optString(Constants.SIMULADOR_LANGUAGE);
        strPdfConfig = jsonObject.optString(Constants.SIMULADOR_EMAIL_PDF_CONF);
        strEmailTemplate = jsonObject.optString(Constants.SIMULADOR_EMAIL_TEMPLATE);
        strEmailUserName = jsonObject.optString(Constants.SIMULADOR_EMAIL_USER_NAME);
        strEmailMail = jsonObject.optString(Constants.SIMULADOR_EMAIL_USER_EMAIL);
        strEmailTelephone = jsonObject.optString(Constants.SIMULADOR_EMAIL_USER_TELEFONO);
        strEmailComment = jsonObject.optString(Constants.SIMULADOR_EMAIL_USER_COMMENT);
        fIsCustomer = jsonObject.optBoolean(Constants.SIMULADOR_EMAIL_IS_CUSTOMER);
        strNif = jsonObject.optString(Constants.SIMULADOR_EMAIL_USER_NIF);
    }
    else
    {
        /* Se recuperan los parametros de entrada si el conetindo no es JSON*/
        nSimulatorId  = Integer.parseInt(request.getParameter(Constants.SIMULADOR_ID));
        strNRBE = (String) request.getParameter(Constants.SIMULADOR_NRBE);
        strNRBEName = (String) request.getParameter(Constants.SIMULADOR_NRBE_NAME);
        strSimulatorType = (String) request.getParameter(Constants.SIMULADOR_TYPE);
        strSimulatorName = (String) request.getParameter(Constants.SIMULADOR_SIMPLE_NAME);
        strLanguage = (String) request.getParameter(Constants.SIMULADOR_LANGUAGE);
        strPdfConfig = (String) request.getParameter(Constants.SIMULADOR_EMAIL_PDF_CONF);
        strEmailTemplate = (String) request.getParameter(Constants.SIMULADOR_EMAIL_TEMPLATE);
        strEmailUserName = (String) request.getParameter(Constants.SIMULADOR_EMAIL_USER_NAME);
        strEmailMail = (String) request.getParameter(Constants.SIMULADOR_EMAIL_USER_EMAIL);
        strEmailTelephone = (String) request.getParameter(Constants.SIMULADOR_EMAIL_USER_TELEFONO);
        strEmailComment = (String) request.getParameter(Constants.SIMULADOR_EMAIL_USER_COMMENT);
        fIsCustomer = Boolean.parseBoolean(request.getParameter(Constants.SIMULADOR_EMAIL_IS_CUSTOMER));
        strNif = (String) request.getParameter(Constants.SIMULADOR_EMAIL_USER_NIF);        
    }	
	
	pLog.info("Se recupera la configuración que le llega al jsp: \n" +
			"nSimulatorId:      " + nSimulatorId +"\n" +	
			"strNRBE:           " + strNRBE +"\n" +	
			"strNRBEName:       " + strNRBEName +"\n" +			
			"strSimulatorType:  " + strSimulatorType +"\n" +		
			"strSimulatorName:  " + strSimulatorName +"\n" + 		
			"strLanguage:       " + strLanguage +"\n" +	
			"strPdfConfig: 	    " + strPdfConfig +"\n" +
			"strEmailTemplate:  " + strEmailTemplate +"\n" +	
			"strEmailUserName:  " + strEmailUserName +"\n" +	
			"strEmailMail:      " + strEmailMail +"\n" +	
			"strEmailTelephone: " + strEmailTelephone +"\n" +	
			"strEmailComment:   " + strEmailComment +"\n" +	
			"fIsCustomer:       " + fIsCustomer +"\n" +	
			"strNif:            " + strNif +"");

	/* se genera el pdf */
  	pLog.info("Se procede a llamar al servicio de generación de pdf con los datos: " + strPdfConfig);
	Client pClient = RviaRestHttpClient.getClient ();
    WebTarget pTarget = pClient.target(PDF_RENDERER_ENDPOINT);
  	Response pReturnPdf = pTarget.request().post(Entity.json(strPdfConfig));
  	pLog.info("Respose POST: " + pReturnPdf.toString());
	String strPdfId = (new JSONObject(pReturnPdf.getEntity().toString())).getJSONObject("response").getJSONObject("data").getString("id");
	pLog.info("Se recibe una petición de descarga PDF con ID " + strPdfId);
	ByteArrayOutputStream pPdfStream = Utils.getFileFromUrl(PDF_RENDERER_DOWNLOAD + strPdfId);		
	/* se obtiene la configuración de BBDD para el simulador solicitado */
	strEmailFrom = SimulatorsManager.getEmailToOffice(strNRBE, strSimulatorName);	
	/* se obtiene el template del email */
	strHtmlTemplate = SimulatorsManager.getEmailTemplate("//simuladores//plantillaEmail.html");
	/* se lee la configuración de envio de email */
	Properties pEmailProterties = Email.loadProperties("simulator.email.properties");	
	/* se envia el email */
	Email pEmail = new Email();
	pEmail.setConfig(pEmailProterties);
	pEmail.setFrom(strEmailFrom);
	pEmail.addTo(strEmailMail);
	pEmail.setSubject("Pruebas simuladores");
	pEmail.setBodyContent(strHtmlTemplate);
	pEmail.send();	
	
%>
{}
