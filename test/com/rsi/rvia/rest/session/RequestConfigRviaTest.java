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
import com.rsi.Constants;
import com.rsi.TestBase;
import com.rsi.rvia.rest.session.RequestConfigRvia.CanalFront;

public class RequestConfigRviaTest extends TestBase
{
	RequestConfigRvia	sessionRvia;

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
	public void testRequestConfigFromRequest() throws Exception
	{
		assertNotNull("testRequestConfigFromRequest: session es null", sessionRvia);
		assertTrue("testRequestConfigFromRequest: session no es instancia de RequestConfig", sessionRvia instanceof RequestConfig);
	}

	@Test
	public void testRequestConfigFromStrings() throws Exception
	{
		RequestConfig sessionRviaStrs = new RequestConfig(Constants.DEFAULT_LANGUAGE, "MOCKED NRBE");
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
		when(request.getParameter("lang")).thenReturn("FOO");
		when(request.getParameter("NRBE")).thenReturn("FOO");
		RequestConfig clearSession = new RequestConfig(request);
		assertNotNull("testClearSession: clearSession es null", clearSession);
		assertTrue("testClearSession: clearSession no es instancia de RequestConfig", clearSession instanceof RequestConfig);
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
		CanalFront canalAix = sessionRvia.getCanalFront();
		assertNotNull("testGetCanalAix: canalAix es null", canalAix);
		assertTrue("testGetCanalAix: canalAix no es instancia de CanalAix", canalAix instanceof CanalFront);
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
