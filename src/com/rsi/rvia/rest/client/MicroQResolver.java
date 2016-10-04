package com.rsi.rvia.rest.client;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;

/*
 * 1.- Con el objeto request, tomamos el path y el verbo, y extraemos los inputs 2.- Revisamos los parámetros de entrada
 * con los inputs censados y comprobamos 3.- Según path y verbo extraemos orquestación 4.- Encadenamos las llamadas
 * según lo necesario, revisando el mapping de entradas si es necesario 5.- Tratamos las salidas como micro objetos. 6.-
 * Componemos el Response global y devolvemos.
 */
public class MicroQResolver
{
	private static Logger	pLog		= LoggerFactory.getLogger(MicroQResolver.class);
	private static String	RviaXML	= "http://10.1.243.142";
	private static String	RviaURI	= "http://10.1.243.142";
	private static String	WSURI		= "http://10.1.243.142";

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public static Response getData(@Context HttpServletRequest pRequest) throws Exception
	{
		String strComponentType = "";
		String strEndPoint = "";
		Response pReturn = null;
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		try
		{
			String strPath = pRequest.getPathInfo();
			String strMethod = pRequest.getMethod();
			String strQuery = "select end_point,componet_type from MIQ_QUESTS where path_rest = ?";
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.MySql);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setString(1, strPath);
			pResultSet = pPreparedStatement.executeQuery();
			while (pResultSet.next())
			{
				strComponentType = pResultSet.getString("componet_type");
				strEndPoint = pResultSet.getString("end_point");
			}
			pReturn = get(pRequest, strComponentType, strEndPoint);
		}
		catch (Exception ex)
		{
			pLog.error("Error al realizar la consulta a la BBDD.");
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
		}
		return pReturn;
	}

	private static URI getBaseRviaXML()
	{
		return UriBuilder.fromUri(RviaXML).build();
	}

	private static URI getBaseWSURI()
	{
		return UriBuilder.fromUri("http://xxx.xxx:/api/").build();
	}

	private static URI getBaseWSURI(String url)
	{
		return UriBuilder.fromUri(url).build();
	}

	private static Response getRVIAInputs(String endp) throws Exception
	{
		Client client = RviaRestHttpClient.getClient();
		WebTarget target = client.target(getBaseRviaXML());
		Response rp = target.request().accept(MediaType.TEXT_PLAIN).get(Response.class);
		pLog.info("RVIA____________: " + rp.getHeaders().toString());
		/*
		 * rp contiene la respuesta xml con las entradas a la página de ruralvia.
		 */
		return rp;
	}

	private static Response performRviaConnection(Response rp)
	{
		/*
		 * Completamos las entradas faltantes con las que se reciban en el request y solicitamos a Ruralvía la página en
		 * cuestión.
		 */
		return rp;
	}

	private static void getWSData()
	{
		// TODO Auto-generated method ssdFAStub
	}

	private static Response rviaPost(@Context HttpServletRequest request, String ct, String endp) throws Exception
	{
		Client client = RviaRestHttpClient.getClient();
		WebTarget target = client.target(getBaseRviaXML());
		Response rp = null;
		/*
		 * Response rp = target.path("rest"). path("hello"). request(). accept(MediaType.TEXT_PLAIN). get(Response.class);
		 * pLog.info("POST: " + rp.toString());
		 */
		pLog.info(endp);
		pLog.info(ct);
		rp = getRVIAInputs(endp);
		rp = performRviaConnection(rp);
		return rp;
	}

	// @GET
	// @Produces(MediaType.TEXT_PLAIN)
	private static Response get(HttpServletRequest request, String ct, String endp) throws Exception
	{
		Client client = RviaRestHttpClient.getClient();
		WebTarget target = client.target(getBaseWSURI(endp));
		Response rp = target.request().accept(MediaType.TEXT_PLAIN).get(Response.class);
		pLog.info("GET: " + rp.toString());
		return rp;
	}

	// @POST
	// @Produces(MediaType.TEXT_PLAIN)
	private static Response post(@Context HttpServletRequest request) throws Exception
	{
		Client client = RviaRestHttpClient.getClient();
		WebTarget target = client.target(getBaseRviaXML());
		Response rp = null;
		/*
		 * Response rp = target.path("rest"). path("hello"). request(). accept(MediaType.TEXT_PLAIN). get(Response.class);
		 * pLog.info("POST: " + rp.toString());
		 */
		return rp;
	}
}
