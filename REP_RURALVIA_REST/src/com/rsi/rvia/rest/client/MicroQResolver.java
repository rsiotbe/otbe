package com.rsi.rvia.rest.client;
import java.net.*;
import java.io.*;
import java.net.URI;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBConnection;
import com.rsi.rvia.rest.DDBB.DDBBFactory;
import com.rsi.rvia.rest.DDBB.DDBBFactory.DDBBProvider;
import com.rsi.rvia.rest.client.CustomRSIClient;
import com.rsi.rvia.rest.tool.LogController;





/*
 * 1.- Con el objeto request, tomamos el path y el verbo, y extraemos los inputs
 * 2.- Revisamos los parámetros de entrada con los inputs censados y comprobamos
 * 3.- Según path y verbo extraemos orquestación
 * 4.- Encadenamos las llamadas según lo necesario, revisando el mapping de entradas si es necesario
 * 5.- Tratamos las salidas como micro objetos.
 * 6.- Componemos el Response global y devolvemos.
 * 
 * 
 */


public class MicroQResolver
{
	private static Logger pLog = LoggerFactory.getLogger(MicroQResolver.class);
	private static LogController pLogC = new LogController();
	private static String RviaXML = "http://10.1.243.142";
	private static String RviaURI = "http://10.1.243.142";
	private static String WSURI = "http://10.1.243.142";
	

	
  @GET
  @Produces(MediaType.TEXT_PLAIN)	
	public static Response getData(@Context HttpServletRequest request) throws Exception {		  
	  String ct="",endp="";
	  DDBBConnection p3 = DDBBFactory.getDDBB(DDBBProvider.MySql);
	  String path=request.getPathInfo();	  
	  PreparedStatement ps = p3.prepareStatement("select end_point,componet_type from MIQ_QUESTS where path_rest = '" + path + "'");
	  ResultSet rs = p3.executeQuery(ps);	  
	  String method = request.getMethod();	 
	  Response resultado = null;
	  while (rs.next()){
		  ct=rs.getString("componet_type");
		  endp=rs.getString("end_point");		  
	  }
	  
	  resultado = get(request,ct,endp);
	  
	  
	  
	  return resultado;
	}	

	private static URI getBaseRviaXML() {
		    return UriBuilder.fromUri(RviaXML).build();
	}	

	private static URI getBaseWSURI() {
	    return UriBuilder.fromUri("http://localhost:8080/api/").build();
   }		

	private static URI getBaseWSURI(String url) {
	    return UriBuilder.fromUri(url).build();
  }	
	
	private static Response getRVIAInputs(String endp) throws Exception
	{
		Client client = CustomRSIClient.getClient();
	    WebTarget target = client.target(getBaseRviaXML());
	    Response rp = target.
            request().
            accept(MediaType.TEXT_PLAIN).
            get(Response.class);
	    pLog.info("RVIA____________: " + rp.getHeaders().toString());	
	    pLogC.addLog("Info", "RVIA____________: " + rp.getHeaders().toString());
	    
	    
	    /*
	     * rp contiene la respuesta xml con las entradas a la página de ruralvia.
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
	  private static Response rviaPost(@Context HttpServletRequest request, String ct, String endp) throws Exception { 
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
		  pLog.info(endp);
		  pLogC.addLog("Info", endp);
		  pLog.info(ct);
		  pLogC.addLog("Info", ct);
		  rp = getRVIAInputs(endp);
		  rp = performRviaConnection(rp);
		  return rp;
	  }	
	  //@GET
	  //@Produces(MediaType.TEXT_PLAIN)	
		private static Response get(HttpServletRequest request, String ct, String endp) throws Exception {	
			Client client = CustomRSIClient.getClient();
		    WebTarget target = client.target(getBaseWSURI(endp));
		    Response rp = target.
	             request().
	             accept(MediaType.TEXT_PLAIN).
	             get(Response.class);
		    
		    pLog.info("GET: " + rp.toString());
		    pLogC.addLog("Info", "GET: " + rp.toString());
	    
		    return rp;		  
	  }	
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
	  	
	
}
