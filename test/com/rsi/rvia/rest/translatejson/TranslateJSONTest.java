package com.rsi.rvia.rest.translatejson;

import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.http.HttpStatus;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.rsi.rvia.rest.endpoint.ruralvia.translatejson.TranslateJSON;
import com.rsi.rvia.rest.endpoint.ruralvia.translatejson.TranslateJsonManager;
import com.rsi.rvia.rest.error.exceptions.ISUMException;
import static org.junit.Assert.assertTrue;

/**
 * The Class TranslateJSONTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ TranslateJsonManager.class })
public class TranslateJSONTest
{
    /** The Constant ENCODING_UTF8. */
    private static final String ENCODING_UTF8 = "UTF-8";
    /** The Constant RESPONSE_OK. */
    private static final String RESPONSE_OK   = "{\"response\":\"0\"}";
    /** The Constant RESPONSE_KO. */
    private static final String RESPONSE_KO   = "{\"response\":\"1\"}";
    /** The pRequest. */
    @Mock
    HttpServletRequest          pRequest;
    /** The pUriInfo. */
    @Mock
    UriInfo                     pUriInfo;
    /** The translate json manager. */
    @Mock
    TranslateJsonManager        translateJsonManager;
    /** The translate JSON. */
    @InjectMocks
    TranslateJSON               translateJSON;
    /** The json object. */
    JSONObject                  jsonObject;

    /**
     * Initial configuration.
     */
    @SuppressWarnings("unchecked")
    @Before
    public void setUp()
    {
        PowerMockito.mockStatic(TranslateJsonManager.class);
        jsonObject = new JSONObject();
        Hashtable<String, Object> data = new Hashtable<String, Object>();
        data.put("strCode", "Mars");
        data.put("strDesc", 32);
        data.put("city", "NY");
        jsonObject.putAll(data);
    }

    /**
     * Translate JSON test no json error.
     * 
     * @throws ISUMException
     *             the ISUM exception
     * @throws ParseException
     *             the parse exception
     */
    @Test
    public void translateJSONTest_NoJsonError() throws ISUMException, ParseException
    {
        PowerMockito.when(TranslateJsonManager.processData(pRequest, pUriInfo, "Mars", "32")).thenReturn(Response.status(HttpStatus.SC_OK).entity(RESPONSE_OK).encoding(ENCODING_UTF8).build());
        Response response = translateJSON.translateJSON(pRequest, pUriInfo, jsonObject.toJSONString());
        JSONParser parser = new JSONParser();
        JSONObject jsonObjectResp = (JSONObject) parser.parse((String) response.getEntity());
        assertTrue(jsonObjectResp.get("response").equals("0"));
    }

    /**
     * Translate JSON test json error.
     * 
     * @throws ISUMException
     *             the ISUM exception
     * @throws ParseException
     *             the parse exception
     */
    @Test
    public void translateJSONTest_JsonError() throws ISUMException, ParseException
    {
        PowerMockito.when(TranslateJsonManager.processData(pRequest, pUriInfo, "Mars", "32")).thenReturn(Response.status(HttpStatus.SC_OK).entity(RESPONSE_KO).encoding(ENCODING_UTF8).build());
        Response response = translateJSON.translateJSON(pRequest, pUriInfo, jsonObject.toJSONString());
        JSONParser parser = new JSONParser();
        JSONObject jsonObjectResp = (JSONObject) parser.parse((String) response.getEntity());
        assertTrue(jsonObjectResp.get("response").equals("1"));
    }

    /**
     * Translate JSON test ISUMException.
     * 
     * @throws ISUMException
     *             the ISUM exception
     * @throws ParseException
     *             the parse exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void translateJSONTest_ISUMException() throws ISUMException, ParseException
    {
        PowerMockito.when(TranslateJsonManager.processData(pRequest, pUriInfo, "Mars", "32")).thenThrow(ISUMException.class);
        Response response = translateJSON.translateJSON(pRequest, pUriInfo, jsonObject.toJSONString());
        assertTrue(response.getStatus() == HttpStatus.SC_INTERNAL_SERVER_ERROR);
        assertTrue(response.getEntity() instanceof ISUMException);
    }

    /**
     * Translate JSON test ParseException.
     * 
     * @throws ISUMException
     *             the ISUM exception
     * @throws ParseException
     *             the parse exception
     */
    @SuppressWarnings("unchecked")
    @Test
    public void translateJSONTest_ParseException() throws ISUMException, ParseException
    {
        PowerMockito.when(TranslateJsonManager.processData(pRequest, pUriInfo, "Mars", "32")).thenThrow(ParseException.class);
        Response response = translateJSON.translateJSON(pRequest, pUriInfo, jsonObject.toJSONString());
        assertTrue(response.getStatus() == HttpStatus.SC_INTERNAL_SERVER_ERROR);
        assertTrue(response.getEntity() instanceof ParseException);
    }
}
