package com.rsi.rvia.rest.endpoint.ruralvia.translatejson;

import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.codec.CharEncoding;
import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;
// import org.jose4j.json.internal.json_simple.JSONObject;
// import org.jose4j.json.internal.json_simple.parser.JSONParser;
// import org.jose4j.json.internal.json_simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.error.exceptions.ISUMException;

/**
 * The Class TranslateJSON.
 */
@Path("/api")
public class TranslateJSON
{
    /** The p log. */
    private static Logger       pLog          = LoggerFactory.getLogger(TranslateJSON.class);
    /** The Constant ENCODING_UTF8. */
    private static final String ENCODING_UTF8 = "UTF-8";

    /**
     * Check if a json has error
     * 
     * @param pRequest
     *            the request
     * @param pUriInfo
     *            the uri info
     * @param strData
     *            the input data
     * @return the response
     */
    @GET
    @Produces({ MediaType.APPLICATION_JSON })
    @Path("/rviatranslatejson/{strData}")
    public Response translateJSON(@Context HttpServletRequest pRequest, @Context UriInfo pUriInfo,
            @PathParam("strData") String strData)
    {
        Response response = null;
        pLog.info("TranslateJSON:get.translateJSON:", strData);
        /*
         * para tracear pLog.debug("TranslateJSON:translateJSON:request:" + pRequest.getRequestURL().toString() + "::" +
         * pRequest.getRequestURI() + "::" + pRequest.getRemoteHost() + "::" + pRequest.getRemoteAddr()); try {
         * java.net.URL surl = new java.net.URL(pRequest.getRequestURL().toString());
         * pLog.debug("TranslateJSON:translateJSON:request:" + surl.getProtocol() + "::" + surl.getHost() + "::" +
         * surl.getPort()); } catch (Exception e) { }
         */
        try
        {
            strData = URLDecoder.decode(strData, ENCODING_UTF8);
            JSONObject jsonObject = new JSONObject(strData);
            response = TranslateJsonManager.processData(pRequest, pUriInfo, jsonObject, true);
        }
        catch (ISUMException e)
        {
            pLog.error("Error en ISUM: ", e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(e).encoding(CharEncoding.UTF_8).build();
        }
        // catch (ParseException e)
        catch (JSONException e)
        {
            pLog.error("Error en el json de entrada: ", e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(e).encoding(CharEncoding.UTF_8).build();
        }
        catch (Exception e)
        {
            pLog.error("Error en Decodificacion: ", e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(e).encoding(CharEncoding.UTF_8).build();
        }
        return response;
    }
}
