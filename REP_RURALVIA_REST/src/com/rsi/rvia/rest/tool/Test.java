package com.rsi.rvia.rest.tool;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/tool/test")
public class Test 
{
	  
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	public Response sayPlainTextHello()
	{
		return Response.status(Response.Status.PAYMENT_REQUIRED).entity("Soy plano - Hello Jersey").build();
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response sayJSONHello(ControlAPI pControlAPI)
	{
		/*if(pControlAPI != null)
		{
			String token = pControlAPI.getToken();
			pControlAPI.setToken(token + "-kike");
			return Response.ok(pControlAPI).build();			
		}
		else
			return Response.status(500).build();*/

		return Response.ok().build();			
	}

	@POST
	@Produces(MediaType.TEXT_XML)
	public String sayXMLHello()
	{
		return "<?xml version=\"1.0\"?>" + "<hello> Soy XML - Hello Jersey" + "</hello>";
	}

	// This method is called if HTML is request
	@POST
	@Produces(MediaType.TEXT_HTML)
	public String sayHtmlHello()
	{
		
		return "<html> " + "<title>" + "Hello Jersey" + "</title>" + "<body><h1>" + "Soy html - Hello Jersey" + "</body></h1>" + "</html> ";
	}
}
