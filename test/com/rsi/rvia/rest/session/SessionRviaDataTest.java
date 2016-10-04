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

public class SessionRviaDataTest
{
	@Mock
	HttpServletRequest	request;
	@Mock
	HttpSession				session;
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
	public void testSessionRviaData() throws Exception
	{
		assertNotNull("testSessionRviaData: session es null", sessionRvia);
		assertTrue("testSessionRviaData: session no es instancia de SessionRviaData", sessionRvia instanceof RequestConfigRvia);
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
	@Ignore
	public void testGetUriRvia() throws Exception
	{
		URI uriRvia = buildSession(false, true).getUriRvia();
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetRviaSessionId()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetIsumUserProfile()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetIsumServiceId()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetLanguage()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetNRBE()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetCanalAix()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testGetToken()
	{
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testToString()
	{
		fail("Not yet implemented");
	}

	private RequestConfigRvia buildSession(boolean hasRequestToken, boolean hasSessionToken) throws Exception
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
		return new RequestConfigRvia(request);
	}
}
