package com.rsi.rvia.rest.translatejson;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.rsi.isum.IsumValidation;
import com.rsi.rvia.rest.endpoint.ruralvia.translatejson.TranslateJsonCache;
import com.rsi.rvia.rest.endpoint.ruralvia.translatejson.TranslateJsonManager;
import com.rsi.rvia.rest.error.exceptions.ISUMException;
import com.rsi.rvia.rest.session.RequestConfigRvia;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * The Class TranslateJsonManagerTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ TranslateJsonCache.class, IsumValidation.class, TranslateJsonManager.class })
public class TranslateJsonManagerTest
{
    /** The p request. */
    @Mock
    HttpServletRequest          pRequest;
    /** The p uri info. */
    @Mock
    UriInfo                     pUriInfo;
    /** The p session. */
    @Mock
    HttpSession                 pSession;
    /** The p request config rvia. */
    @Mock
    RequestConfigRvia           pRequestConfigRvia;
    /** The translate json manager. */
    @InjectMocks
    TranslateJsonManager        translateJsonManager;
    /** The str code. */
    String                      strCode     = "Mars";
    /** The str desc. */
    String                      strDesc     = "32";
    /** The Constant RESPONSE_OK. */
    private static final String RESPONSE_OK = "{\"response\":\"0\"}";
    /** The Constant RESPONSE_KO. */
    private static final String RESPONSE_KO = "{\"response\":\"1\"}";

    /**
     * Sets the up.
     */
    @Before
    public void setUp()
    {
        PowerMockito.mockStatic(TranslateJsonCache.class);
        PowerMockito.mockStatic(IsumValidation.class);
    }

    /**
     * Process data json OK.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void processData_JsonOK() throws Exception
    {
        PowerMockito.when(TranslateJsonCache.isErrorCode(strCode, strDesc)).thenReturn(true);
        PowerMockito.when(IsumValidation.IsValidService(pRequestConfigRvia)).thenReturn(true);
        PowerMockito.stub(PowerMockito.method(TranslateJsonManager.class, "getValidateSession")).toReturn(pRequestConfigRvia);
        when(pRequest.getSession(true)).thenReturn(pSession);
        Response response = TranslateJsonManager.processData(pRequest, pUriInfo, strCode, strDesc);
        assertTrue(((String) response.getEntity()).equals(RESPONSE_OK));
    }

    /**
     * Process data json KO.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void processData_JsonKO() throws Exception
    {
        PowerMockito.when(TranslateJsonCache.isErrorCode(strCode, strDesc)).thenReturn(false);
        PowerMockito.when(IsumValidation.IsValidService(pRequestConfigRvia)).thenReturn(true);
        PowerMockito.stub(PowerMockito.method(TranslateJsonManager.class, "getValidateSession")).toReturn(pRequestConfigRvia);
        when(pRequest.getSession(true)).thenReturn(pSession);
        Response response = TranslateJsonManager.processData(pRequest, pUriInfo, strCode, strDesc);
        assertTrue(((String) response.getEntity()).equals(RESPONSE_KO));
    }

    /**
     * Process data ISUM exception.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void processData_ISUMException() throws Exception
    {
        PowerMockito.when(TranslateJsonCache.isErrorCode(strCode, strDesc)).thenReturn(false);
        PowerMockito.when(IsumValidation.IsValidService(pRequestConfigRvia)).thenReturn(false);
        PowerMockito.stub(PowerMockito.method(TranslateJsonManager.class, "getValidateSession")).toReturn(pRequestConfigRvia);
        Response response = TranslateJsonManager.processData(pRequest, pUriInfo, strCode, strDesc);
        JSONParser parser = new JSONParser();
        JSONObject jsonObjectResp = (JSONObject) parser.parse((String) response.getEntity());
        assertTrue((Long) jsonObjectResp.get("code") == 9999999);
        assertTrue((Long) jsonObjectResp.get("httpCode") == 401);
    }

    /**
     * Process data exception.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void processData_Exception() throws Exception
    {
        PowerMockito.when(TranslateJsonCache.isErrorCode(strCode, strDesc)).thenReturn(false);
        PowerMockito.when(IsumValidation.IsValidService(pRequestConfigRvia)).thenReturn(false);
        PowerMockito.stub(PowerMockito.method(TranslateJsonManager.class, "getValidateSession")).toReturn(Exception.class);
        Response response = TranslateJsonManager.processData(pRequest, pUriInfo, strCode, strDesc);
        JSONParser parser = new JSONParser();
        JSONObject jsonObjectResp = (JSONObject) parser.parse((String) response.getEntity());
        assertTrue((Long) jsonObjectResp.get("code") == 9999999);
        assertTrue((Long) jsonObjectResp.get("httpCode") == 500);
    }

    /**
     * Gets the validate session OK.
     * 
     * @return the validate session OK
     * @throws Exception
     *             the exception
     */
    @Test
    public void getValidateSession_OK() throws Exception
    {
        RequestConfigRvia pRequestConfigRviaResp = null;
        PowerMockito.mock(RequestConfigRvia.class);
        PowerMockito.whenNew(RequestConfigRvia.class).withArguments(pRequest).thenReturn(pRequestConfigRvia);
        PowerMockito.when(IsumValidation.IsValidService(pRequestConfigRvia)).thenReturn(true);
        pRequestConfigRviaResp = TranslateJsonManager.getValidateSession(pRequest);
        assertNotNull(pRequestConfigRviaResp);
    }

    /**
     * Gets the validate session ISUM exception.
     * 
     * @return the validate session ISUM exception
     * @throws Exception
     *             the exception
     */
    @Test(expected = ISUMException.class)
    public void getValidateSession_ISUMException() throws Exception
    {
        PowerMockito.mock(RequestConfigRvia.class);
        PowerMockito.whenNew(RequestConfigRvia.class).withArguments(pRequest).thenReturn(pRequestConfigRvia);
        PowerMockito.when(IsumValidation.IsValidService(pRequestConfigRvia)).thenReturn(false);
        TranslateJsonManager.getValidateSession(pRequest);
    }
}
