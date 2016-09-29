package com.rsi.rvia.rest.session;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import java.net.URI;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.rsi.rvia.rest.session.SessionRviaData.CanalAix;

public class SessionRviaDataTest
{
	@Mock
	HttpServletRequest	request;
	@Mock
	HttpSession				session;
	SessionRviaData		sessionRvia;

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
	public void testSessionRviaData() throws Exception
	{
		assertNotNull("testSessionRviaData: session es null", sessionRvia);
		assertTrue("testSessionRviaData: session no es instancia de SessionRviaData", sessionRvia instanceof SessionRviaData);
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
	@Ignore
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
		assertTrue("testGetNRBE: nrbe no es instancia de URI", nrbe instanceof String);
	}

	@Test
	@Ignore
	public void testGetCanalAix()
	{
		CanalAix canalAix = sessionRvia.getCanalAix();
		assertNotNull("testGetCanalAix: canalAix es null", canalAix);
		assertTrue("testGetCanalAix: canalAix no es instancia de URI", canalAix instanceof CanalAix);
	}

	@Test
	public void testGetToken()
	{
		String nrbe = sessionRvia.getNRBE();
		assertNotNull("testGetNRBE: nrbe es null", nrbe);
		assertTrue("testGetNRBE: nrbe no es instancia de URI", nrbe instanceof String);
	}

	@Ignore
	@Test
	public void testToString()
	{
		fail("Not yet implemented");
	}

	private SessionRviaData buildSession(boolean hasRequestToken, boolean hasSessionToken) throws Exception
	{
		final String TOKEN = "HqJ/6fJk2+8jXi90/VU/H29V08ygqXMTqSbSkP+r715kWSdhPR6eEGqAM86sATxKgtnVPHAee2dLyX+MjxGQCqA2U6MTejqDbcjvidfIemotQcsYp/GzRhPLqFds+S6nt3MMpTchkEpTzoH0IRuTy3LaZaNghZmC+iOdiEMRiN7GGnVyJrD/P0y2hrwScmOm96Xjlu4nAAPc/+cRUyrzKtBzNcxyYxbiII8rw4S0dbruN9mPQMTFJCVPK425UWSmP3eQ/FbElgu3wZxMy/Ekgw==";
		if (hasRequestToken)
		{
			when(request.getParameter("token")).thenReturn(TOKEN);
		}
		else
		{
			when(request.getParameter("token")).thenReturn(null);
			when(request.getSession(false)).thenReturn(session);
			when(session.getAttribute("token")).thenReturn(hasSessionToken ? TOKEN : null);
		}
		return new SessionRviaData(request);
	}
}
