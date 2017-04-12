package com.rsi.rvia.rest.endpoint.restadmin;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.OperationManager;

@Path("/restadmin")
public class MiqQuestAdminServices
{
    private static Logger pLog = LoggerFactory.getLogger(MiqQuestAdminServices.class);

    /**
     * Obtiene un agregado de movimientos por mes y signo para vista, y a partir de una fecha, hasta una fecha fin.
     * Adeudos y abonos históricos en acuerdos En el documento WebService3
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/login")
    // @Path("/contracts/movementsbymonth/{idContract: [0-9]+}/from/{mesInicio}/to/{mesFin}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response logn(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Log on");
        return pReturn;
    }

    /**
     * Obtiene un agregado de movimientos por mes y signo para vista, y a partir de una fecha, hasta una fecha fin.
     * Adeudos y abonos históricos en acuerdos En el documento WebService3
     * 
     * @return Objeto que contiene la respuesta y en caso positivo se adjunta el listado de tarjetas @
     */
    @GET
    @Path("/miqs")
    // @Path("/contracts/movementsbymonth/{idContract: [0-9]+}/from/{mesInicio}/to/{mesFin}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listado(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo, String strData)
    {
        Response pReturn = OperationManager.processForAPI(pRequest, pUriInfo, strData, MediaType.TEXT_PLAIN_TYPE);
        pLog.info("Movimientos de un contrato");
        return pReturn;
    }
}
