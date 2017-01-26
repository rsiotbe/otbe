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
	Logger pLog = LoggerFactory.getLogger("sendEmail.jsp");
	String strReturn;
	
	/* Se recuperan los parametros de entrada*/
	JSONObject pJson = new JSONObject();
    JSONObject pJsonData = new JSONObject();
    String strResponse;
    int nSimulatorId;
    String strNRBE;
    String strNRBEName;
    String strSimulatorType;
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
    try
    {
	    /* si no se detecta como parámetro el codigo de entidad se entiende que se está enviadno como JSON la información*/
	    if (request.getParameter(Constants.SIMULADOR_NRBE) == null)
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
	        nSimulatorId  = jsonObject.optInt(Constants.SIMULADOR_ID);
	        strNRBE = jsonObject.optString(Constants.SIMULADOR_NRBE);
	        strLanguage = jsonObject.optString(Constants.SIMULADOR_LANGUAGE);
	        strPdfConfig = jsonObject.optString(Constants.SIMULADOR_PDF_CONF);
	        strEmailUserName = jsonObject.optString(Constants.SIMULADOR_EMAIL_USER_NAME);
	        strEmailMail = jsonObject.optString(Constants.SIMULADOR_EMAIL_USER_EMAIL);
	        strEmailTelephone = jsonObject.optString(Constants.SIMULADOR_EMAIL_USER_TELEFONO);
	        strEmailComment = jsonObject.optString(Constants.SIMULADOR_EMAIL_USER_COMMENT);
	        fIsCustomer = jsonObject.optBoolean(Constants.SIMULADOR_EMAIL_USER_IS_CUSTOMER);
	        strNif = jsonObject.optString(Constants.SIMULADOR_EMAIL_USER_NIF);
	    }
	    else
	    {
	        /* Se recuperan los parámetros de entrada si el conetindo no es JSON*/
	        nSimulatorId  = Integer.parseInt(request.getParameter(Constants.SIMULADOR_ID));
	        strNRBE = (String) request.getParameter(Constants.SIMULADOR_NRBE);
	        strLanguage = (String) request.getParameter(Constants.SIMULADOR_LANGUAGE);
	        strPdfConfig = (String) request.getParameter(Constants.SIMULADOR_PDF_CONF);
	        strEmailUserName = (String) request.getParameter(Constants.SIMULADOR_EMAIL_USER_NAME);
	        strEmailMail = (String) request.getParameter(Constants.SIMULADOR_EMAIL_USER_EMAIL);
	        strEmailTelephone = (String) request.getParameter(Constants.SIMULADOR_EMAIL_USER_TELEFONO);
	        strEmailComment = (String) request.getParameter(Constants.SIMULADOR_EMAIL_USER_COMMENT);
	        fIsCustomer = Boolean.parseBoolean(request.getParameter(Constants.SIMULADOR_EMAIL_USER_IS_CUSTOMER));
	        strNif = (String) request.getParameter(Constants.SIMULADOR_EMAIL_USER_NIF);        
	    }	
		
		pLog.info("Se recupera la configuración que le llega al jsp: \n" +
				"SimulatorId:    " + nSimulatorId +"\n" +	
				"NRBE:           " + strNRBE +"\n" +		
				"Language:       " + strLanguage +"\n" +	
				"PdfConfig: 	 " + strPdfConfig +"\n" +
				"EmailUserName:  " + strEmailUserName +"\n" +	
				"EmailMail:      " + strEmailMail +"\n" +	
				"EmailTelephone: " + strEmailTelephone +"\n" +	
				"EmailComment:   " + strEmailComment +"\n" +	
				"IsCustomer:     " + fIsCustomer +"\n" +	
				"Nif:            " + strNif +"");
	
		/* se genera el pdf */
	 	/* Se cargan las propiedades del servicio */
		Properties pPdfProperties = new Properties();
		pPdfProperties.load(Email.class.getResourceAsStream("/simulator.pdf.properties"));
			
	  	pLog.info("Se procede a llamar al servicio de generación de pdf (" + (String)pPdfProperties.get("SERVICE_GENERATE") + ")con los datos: " + strPdfConfig);
		Client pClient = RviaRestHttpClient.getClient ();
	    WebTarget pTarget = pClient.target((String)pPdfProperties.get("SERVICE_GENERATE"));
	  	Response pReturnPdf = pTarget.request().post(Entity.json(strPdfConfig));
	  	String strResponseData = pReturnPdf.readEntity(String.class);
	  	pLog.info("Respuesta del servicio: " + strResponseData);
	  	JSONObject pResponse = (new JSONObject(strResponseData)).getJSONObject("DOC_RENDER");
	  	/* s ecomprueba si ha devuelto error */
	  	if("0".equals(pResponse.getString("codigoRetorno")))
	  	{
	  		pLog.error("El servicio de generación de PDF ha devuelto un error");
	  		throw new Exception("Error en la respuesta del servicio pdf");
	  	}
		String strPdfId = pResponse.getJSONObject("Respuesta").getString("id");
		pLog.info("Se recibe una petición de descarga PDF con ID " + strPdfId);
		
		/* se descarga el pdf */
		String strUrlDownload = (String)pPdfProperties.get("SERVICE_DOWNLOAD") + strPdfId;
		pLog.info("Se intenta la descarga del pdf de la URL:  strUrlDownload");
		ByteArrayOutputStream pPdfStream = Utils.getFileFromUrl(strUrlDownload);		
		if(pPdfStream == null)
			pLog.error("El stream de PDF es nulo, la descarga ha fallado");
		else
			pLog.info("Se obtiene el stream de PDF. Tamaño: " + pPdfStream.size() + "bytes");
	
		/* se obtiene la configuracion de envio del email */
		SimulatorEmailConfig pSimulatorEmailConfig =  SimulatorsManager.getSimulatorEmailConfig(nSimulatorId, strNRBE);
		pLog.info("Se obtiene el objeto de configuración de envio de email: " + pSimulatorEmailConfig);
		/* se lee la configuración del servidor de envio de email */
		Properties pEmailProterties = Email.loadProperties("simulator.email.properties");	
		pLog.info("Se obtiene el objeto de configuración del servidor de email: " + pEmailProterties);
		/* se envia el email a la sucursal*/
		Email pEmail = new Email();
		pEmail.setConfig(pEmailProterties);
		pEmail.setFrom(pSimulatorEmailConfig.getOfficeFrom());
		pEmail.addTo(pSimulatorEmailConfig.getOfficeTo());
		pEmail.setSubject(pSimulatorEmailConfig.getOfficeSubject());
		pLog.info("Se asignan el origen y destino del email");
		
		/* se lee la platinlla y se remplazan los valores */
		String strOfficeTemplate = pSimulatorEmailConfig.getOfficeTemplate();
		String strHtmlOfficeTemplate = SimulatorsManager.getEmailTemplate(strOfficeTemplate);
		/* se cra un hashtable donde se meten losd atos que se van a substituir en la plantilla */
		pLog.info("Se procede a obtener y componer el cuerpo del mensaje");
		Hashtable<String, String> htEmailTemplateData = new Hashtable<String, String>();
		htEmailTemplateData.put("NRBEName",pSimulatorEmailConfig.getNRBEName());
		htEmailTemplateData.put("ComercialName",pSimulatorEmailConfig.getComercialName());
		htEmailTemplateData.put("EmailUserName",strEmailUserName);
		htEmailTemplateData.put("EmailMail",strEmailMail);
		htEmailTemplateData.put("EmailTelephone",strEmailTelephone);
		htEmailTemplateData.put("EmailComment",strEmailComment);
		htEmailTemplateData.put("IsCustomer",String.valueOf(fIsCustomer));
		htEmailTemplateData.put("Nif",strNif);
		String strFinalHtml = SimulatorsManager.proccessEmailTemplate(strHtmlOfficeTemplate, htEmailTemplateData);	 
		pEmail.setBodyContent(strFinalHtml);
		pLog.info("Se procede a adjuntar el fichero pdf al email");
		pEmail.addAttachedFile(new EmailAttachObject("solicitud.pdf", "application/pdf", pPdfStream.toByteArray()) );
		pLog.info("Se compone el email y se procede a su envio");
		pEmail.send();	
		strReturn = Utils.generateWSResponseJsonOk("sendMail", "{\"status\":\"OK\"}");
    }
    catch (Exception ex)
    {
  	  pLog.error("Error al procesar la petición de PDF", ex);
		strReturn = Utils.generateWSResponseJsonError("sendMail", 99999, ex.toString());
    }
%><%=strReturn%>
