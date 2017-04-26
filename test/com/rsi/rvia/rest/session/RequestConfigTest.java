package com.rsi.rvia.rest.session;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import com.rsi.Constants;
import com.rsi.Constants.CanalFront;
import com.rsi.Constants.CanalHost;
import com.rsi.Constants.Language;
import com.rsi.TestBase;

public class RequestConfigTest extends TestBase
{
    RequestConfig pRequestConfig;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);
        pRequestConfig = buildSession(true, false);
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void testRequestConfigFromRequest() throws Exception
    {
        assertNotNull("testRequestConfigFromRequest: session es null", pRequestConfig);
        assertTrue("testRequestConfigFromRequest: session no es instancia de RequestConfig", pRequestConfig instanceof RequestConfig);
    }

    @Test
    public void testRequestConfigFromStrings() throws Exception
    {
        RequestConfig sessionRviaStrs = new RequestConfig(Constants.DEFAULT_LANGUAGE.getJavaCode(), "MOCKED NRBE");
        assertNotNull("testRequestConfigFromStrings: session es null", sessionRviaStrs);
        assertTrue("testRequestConfigFromStrings: RequestConfigStrs no es instancia de RequestConfig", sessionRviaStrs instanceof RequestConfig);
    }

    @Test
    public void testRequestConfigFromStringsDefault() throws Exception
    {
        RequestConfig sessionRviaStrs = new RequestConfig(null, null);
        assertNotNull("testRequestConfigFromStringsDefault: session es null", sessionRviaStrs);
        assertTrue("testRequestConfigFromStringsDefault: sessionRviaStrs no es instancia de RequestConfig", sessionRviaStrs instanceof RequestConfig);
    }

    @Test
    public void testClearSession() throws Exception
    {
        when(request.getSession(false)).thenReturn(session);
        when(request.getParameter("token")).thenReturn(null);
        when(request.getParameter("node")).thenReturn("Banca_LNF04_07");
        when(request.getParameter("RVIASESION")).thenReturn("FOO");
        when(request.getParameter("isumProfile")).thenReturn("FOO");
        when(request.getParameter("isumServiceId")).thenReturn("FOO");
        when(request.getParameter("lang")).thenReturn("FOO");
        when(request.getParameter("NRBE")).thenReturn("FOO");
        when(request.getParameter("canalAix")).thenReturn(CanalFront.WEB.getValue() + "");
        when(request.getParameter("canal")).thenReturn(CanalHost.BANCA_INTERNET.getValue() + "");
        RequestConfig clearSession = new RequestConfig(request);
        assertNotNull("testClearSession: clearSession es null", clearSession);
        assertTrue("testClearSession: clearSession no es instancia de RequestConfig", clearSession instanceof RequestConfig);
    }

    @Test
    public void testGetLanguage()
    {
        Language pLanguage = pRequestConfig.getLanguage();
        assertNotNull("testGetLanguage: language es null", pLanguage);
        assertTrue("testGetLanguage: language no es instancia de String", pLanguage instanceof Language);
    }

    @Test
    public void testGetNRBE()
    {
        String nrbe = pRequestConfig.getNRBE();
        assertNotNull("testGetNRBE: nrbe es null", nrbe);
        assertTrue("testGetNRBE: nrbe no es instancia de String", nrbe instanceof String);
    }
}
