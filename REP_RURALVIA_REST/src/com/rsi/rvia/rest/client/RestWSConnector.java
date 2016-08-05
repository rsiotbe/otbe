package com.rsi.rvia.rest.client;

import java.net.*;
import java.io.*;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.rsi.rvia.rest.DDBB.DDBBConnection;
import com.rsi.rvia.rest.DDBB.DDBBFactory;
import com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider;
import com.rsi.rvia.rest.operation.info.InterrogateRvia;
import com.rsi.rvia.rest.session.SessionRviaData;
import javax.xml.bind.JAXBElement;


//@Path("/hello")
public class RestWSConnector
{
	private static Logger pLog = LoggerFactory.getLogger(RestWSConnector.class);

	
	
	
	private static String RviaXML = "http://10.1.243.142";

	
  //@GET
  //@Produces(MediaType.TEXT_PLAIN)	
	public static Response getData( HttpServletRequest request, String data, SessionRviaData sesion_rvia, String strPrimaryPath) throws Exception {		  
	  String ct="",endp="";	  
	  DDBBConnection p3 = DDBBFactory.getDDBB(DDBBProvider.MySql);
	  //String path=request.getPathInfo();	 	  
	  String path=strPrimaryPath;
	  PreparedStatement ps = p3.prepareStatement("select end_point,componet_type from microquest.MIQ_QUESTS where path_rest = '" + path + "'");
	  ResultSet rs = p3.executeQuery(ps);	  
	  String method = request.getMethod();	 
	  Response resultado = null;
	  while (rs.next()){
		  ct=rs.getString("componet_type");
		  endp=rs.getString("end_point");		  
	  }	  
	  pLog.info("Preparando petición para tipo " + ct + " y endpoint " + endp + " # method: " + method);	
	  switch(method){
		  case "GET":
			  resultado = get(request);			  			  
			  if("RVIA".equals(ct)){
				  pLog.info("Derivando petición a Ruralvía");
				  resultado = rviaPost(request,ct,endp, sesion_rvia, data);
			  }
			  else{
				  pLog.info("Solicitando peticióñn REST");
				  resultado = get(request);
			  }			  
			  break;
		  case "POST":
			  if("RVIA".equals(ct)){
				  pLog.info("Derivando petición a Ruralvía");
				  resultado = rviaPost(request,ct,endp, sesion_rvia, data);
			  }
			  else{
				  pLog.info("Solicitando peticióñn REST");
				  resultado = post(request);
			  }
			  break;
		  case "PUT":
			  resultado = put(request);
			  break;
		  case "PATCH":
			  break;
		  case "DELETE":
			  resultado = delete(request);
			  break;
	  }
	  return resultado;
	}	

	private static URI getBaseRviaXML() {
		    return UriBuilder.fromUri(RviaXML).build();
	}	

	private static URI getBaseWSURI() {
	    return UriBuilder.fromUri("http://localhost:8080/api/").build();
}		
	
	private static Response getRVIAInputs(HttpServletRequest req, String endp, SessionRviaData sesion_rvia, String data) throws Exception
	{
		
		SessionRviaData sesiFoo=sesion_rvia;
		
		String sesId=sesiFoo.getRviaSessionId();
		String host=sesiFoo.getUriRvia().toString();
		String nodo=sesiFoo.getNodeRvia();
		
		String url = host + "/portal_rvia/ServletDirectorPortal;RVIASESION=" + sesId; // + "?" + "clave_pagina=" + endp;
				
	    Client client = CustomRSIClient.getClient();
	    WebTarget target = client.target(getBaseRviaXML());	    
	    Document doc = InterrogateRvia.getXmlDatAndUserInfo(req, endp);	    
	    NodeList nodos = doc.getElementsByTagName("field");	    
	    HashMap<String, String> camposDeSession = new HashMap<String, String>();
	    	    
	    // Datos existentes en la sessión
	    for(int i=0; i<nodos.getLength(); i++){
	   	 Element e = (Element)nodos.item(i);
	   	 String value = e.getAttribute("value");
	   	 if(! value.isEmpty()){
	   		 camposDeSession.put(e.getAttribute("name"), e.getAttribute("value"));
	   	 }
	    }
	    
	    // Datos llegados por post
	    String[] arr=data.split("&");
	    if(!data.trim().isEmpty())
	    {
		    for(int i=0; i<arr.length; i++){
		   	 String[] arr2=arr[i].split("=");
		   	 camposDeSession.put(arr2[0],arr2[1]);
		    }
	    }
	    
/*	    
	    Form form = new Form();
	    
	    MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
	    
	    formData.add("key1", "value1");
	    formData.add("key2", "value2");
	    
	    Response response = webTarget.request().post(Entity.form(formData));
*/	    

	    String qParams="";
	    Iterator<?> it = camposDeSession.entrySet().iterator();
	    while (it.hasNext()) {
		    Map.Entry e = (Map.Entry)it.next();
		    qParams = qParams + "&" + e.getKey() + "=" + e.getValue();
	    }	    
	    
	    target=client.target(UriBuilder.fromUri(url).build());
/*	    
	    MultivaluedMap<String, String> formData = new MultivaluedHashMap<String, String>();
	    formData.add("name1", "val1");
	    formData.add("name2", "val2");
*/    
	    //target=target.queryParam(qParams, target);
	    
	    	   Response rp = target.
            request(qParams).
            
            //post(Entity.entity.form(formData)).
            
            accept(MediaType.TEXT_PLAIN).
            get(Response.class);
	    pLog.info("RVIA____________: " + rp.getHeaders().toString());	
	    
	    /*
	     * rp contiene la respuesta xml con las entradas a la página de ruralvia.
	     * 
	     * Considerar censar las entradas en el modelo en la petición, una vez al día, y si han variado.
	     * 
	     * */
	    
	    
	    
		return rp;
   
	}	  
	
	private static Response performRviaConnection(Response rp)
	{
		/*
		 * Completamos las entradas faltantes con las que se reciban en el request y solicitamos
		 * a Ruralvía la página en cuestión.
		 * 
		 * 
		 * 
		 * */
		
		return rp;	
	}		
	private static void getWSData()
	{
		// TODO Auto-generated method ssdFAStub		
	}	
	  private static Response rviaPost(@Context HttpServletRequest request, String ct, String endp, SessionRviaData sesion_rvia, String data) throws Exception { 
		  Client client = CustomRSIClient.getClient();
		    
		    URI targetxml = getBaseRviaXML();
		    pLog.info("RVIA_POST: " +
		   		 targetxml.toString());	
		    
		    WebTarget target = client.target(getBaseRviaXML());	

		    Response rp=null;
		    
		    rp = target.path("rest").
	             path("hello").
	             request().
	             accept(MediaType.TEXT_PLAIN).
	             get(Response.class);	
		    
		    pLog.info("RVIA_POST: " + rp.toString());		  
			

		  pLog.info(endp);
		  pLog.info(ct);
		  rp = getRVIAInputs(request,endp, sesion_rvia, data);
		  rp = performRviaConnection(rp);
		  return rp;
	  }	
	  //@GET
	  //@Produces(MediaType.TEXT_PLAIN)	
		private static Response get(HttpServletRequest request) throws Exception {	
			Client client = CustomRSIClient.getClient();
		    WebTarget target = client.target(getBaseRviaXML());
		    Response rp = target.path("rest").
	             path("hello").
	             request().
	             accept(MediaType.TEXT_PLAIN).
	             get(Response.class);
		    pLog.info("GET: " + rp.toString());
		    return rp;		  
	  }	
		
		/**
		 * Verbo post. Recibe HttpServletRequest de contexto para derivar a RESTfull
		 * 
		 * 
		 * @return Response con el objeto respuesta */
		
	  //@POST
	  //@Produces(MediaType.TEXT_PLAIN)	
	  private static Response post(@Context HttpServletRequest request) throws Exception { 
		  Client client = CustomRSIClient.getClient();
		    WebTarget target = client.target(getBaseRviaXML());	
		    
		    Response rp=null;
		    /*
		    Response rp = target.path("rest").
	             path("hello").
	             request().
	             accept(MediaType.TEXT_PLAIN).
	             get(Response.class);
		    pLog.info("POST: " + rp.toString());		  
			*/
		    
		  return rp;
	  }
	  
	  //@PUT
	  //@Produces(MediaType.TEXT_PLAIN)	
	  private static Response put(@Context HttpServletRequest request) throws Exception {	
		  Client client = CustomRSIClient.getClient();
		    WebTarget target = client.target(getBaseRviaXML());	
		    Response rp=null;
		    /*
		    Response rp = target.path("rest").
	             path("hello").
	             request().
	             accept(MediaType.TEXT_PLAIN).
	             put(Response.class);
		    pLog.info("PUT: " + rp.toString());		
		    */  
		  return rp;
	  }	
	
	 
	  //@DELETE
	  //@Produces(MediaType.TEXT_PLAIN)	
	  private static Response delete(@Context HttpServletRequest request) throws Exception {	
		  Client client = CustomRSIClient.getClient();
		    WebTarget target = client.target(getBaseRviaXML());		  
		    Response rp = target.path("rest").
	             path("hello").
	             request().
	             accept(MediaType.TEXT_PLAIN).
	             delete(Response.class);
	       
		  pLog.info("DELETE: " + rp.toString());
		  return rp;
	  }		
	
}
