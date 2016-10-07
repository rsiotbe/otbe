package com.rsi.rvia.rest.session;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.net.URI;
import javax.servlet.http.Cookie;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import com.rsi.TestBase;
import com.rsi.Constantes;
import com.rsi.rvia.rest.session.SessionRviaData.CanalAix;

public class SessionRviaDataTest extends TestBase
{
	RequestConfigRvia		sessionRvia;

   @Before
   public void setUp() throws Exception
   {
      MockitoAnnotations.initMocks(this);
      sessionRvia = buildSession(true, false);
   }

   @After
   public void tearDown() throws Exception
   {
   }

   @Test
   public void testSessionRviaDataFromRequest() throws Exception
   {
      assertNotNull("testSessionRviaDataFromRequest: session es null", sessionRvia);
      assertTrue("testSessionRviaDataFromRequest: session no es instancia de SessionRviaData", sessionRvia instanceof SessionRviaData);
   }

   @Test
   public void testSessionRviaDataFromStrings() throws Exception
   {
      SessionRviaData sessionRviaStrs = new SessionRviaData(Constantes.DEFAULT_LANGUAGE, "MOCKED NRBE");
      assertNotNull("testSessionRviaDataFromStrings: session es null", sessionRviaStrs);
      assertTrue("testSessionRviaDataFromStrings: sessionRviaStrs no es instancia de SessionRviaData", sessionRviaStrs instanceof SessionRviaData);
   }

   @Test
   public void testSessionRviaDataFromStringsDefault() throws Exception
   {
      SessionRviaData sessionRviaStrs = new SessionRviaData(null, null);
      assertNotNull("testSessionRviaDataFromStringsDefault: session es null", sessionRviaStrs);
      assertTrue("testSessionRviaDataFromStringsDefault: sessionRviaStrs no es instancia de SessionRviaData", sessionRviaStrs instanceof SessionRviaData);
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
      when(request.getParameter("canalAix")).thenReturn(CanalAix.BANCA_INTERNET.getValue() + "");
      SessionRviaData clearSession = new SessionRviaData(request);
      assertNotNull("testClearSession: clearSession es null", clearSession);
      assertTrue("testClearSession: clearSession no es instancia de SessionRviaData", clearSession instanceof SessionRviaData);
   }

   @Test
   public void testGetNodeRvia()
   {
      String nodeRvia = sessionRvia.getNodeRvia();
      assertNotNull(nodeRvia);
   }

   @Test
   public void testGetCookiesRviaData() throws Exception
   {
      when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("foo", "bar") });
      when(request.getParameter("node")).thenReturn("baz");
      Cookie[] cookiesRviaData = buildSession(false, true).getCookiesRviaData();
      assertNotNull("testGetCookiesRviaData: cookiesRviaData es null", cookiesRviaData);
   }

   @Test
   public void testGetUriRvia() throws Exception
   {
      URI uriRvia = sessionRvia.getUriRvia();
      assertNotNull("testGetUriRvia: uriRvia es null", uriRvia);
      assertTrue("testGetUriRvia: uriRvia no es instancia de URI", uriRvia instanceof URI);
   }

   @Test
   public void testGetRviaSessionId()
   {
      String rviaSessionId = sessionRvia.getRviaSessionId();
      assertNotNull("testGetRviaSessionId: rviaSessionId es null", rviaSessionId);
      assertTrue("testGetRviaSessionId: rviaSessionId no es instancia de String", rviaSessionId instanceof String);
   }

   @Test
   public void testGetIsumUserProfile()
   {
      String isumUserProfile = sessionRvia.getIsumUserProfile();
      assertNotNull("testGetIsumUserProfile: isumUserProfile es null", isumUserProfile);
      assertTrue("testGetIsumUserProfile: isumUserProfile no es instancia de String", isumUserProfile instanceof String);
   }

   @Test
   public void testGetIsumServiceId()
   {
      String isumServiceId = sessionRvia.getIsumServiceId();
      assertNotNull("testGetIsumServiceId: isumServiceId es null", isumServiceId);
      assertTrue("testGetIsumServiceId: isumServiceId no es instancia de String", isumServiceId instanceof String);
   }

   @Test
   public void testGetLanguage()
   {
      String language = sessionRvia.getLanguage();
      assertNotNull("testGetLanguage: language es null", language);
      assertTrue("testGetLanguage: language no es instancia de String", language instanceof String);
   }

   @Test
   public void testGetNRBE()
   {
      String nrbe = sessionRvia.getNRBE();
      assertNotNull("testGetNRBE: nrbe es null", nrbe);
      assertTrue("testGetNRBE: nrbe no es instancia de String", nrbe instanceof String);
   }

   @Test
   public void testGetCanalAix()
   {
      CanalAix canalAix = sessionRvia.getCanalAix();
      assertNotNull("testGetCanalAix: canalAix es null", canalAix);
      assertTrue("testGetCanalAix: canalAix no es instancia de CanalAix", canalAix instanceof CanalAix);
   }

   @Test
   public void testGetToken()
   {
      String token = sessionRvia.getToken();
      assertNotNull("testGetToken: token es null", token);
      assertTrue("testGetToken: token no es instancia de String", token instanceof String);
   }

   @Test
   public void testToString() throws Exception
   {
      when(request.getCookies()).thenReturn(new Cookie[] { new Cookie("foo1", "bar1"), new Cookie("foo2", "bar2") });
      String str = buildSession(true, false).toString();
      assertNotNull("testToString: str es null", str);
      assertTrue("testToString: nrbe no es instancia de String", str instanceof String);
      assertNotNull("testToString: str no tiene contenido", str.length() > 0);
   }
}
