package com.rsi.isum;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import javax.servlet.http.HttpServletRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.rsi.rvia.rest.error.exceptions.ISUMException;
import com.rsi.rvia.rest.session.SessionRviaData;

public class IsumValidationTest
{
	@Mock
	HttpServletRequest	request;
	@Mock
	SessionRviaData		session;

	@Before
	public void setUp() throws Exception
	{
		MockitoAnnotations.initMocks(this);
	}

	@After
	public void tearDown() throws Exception
	{
	}

	@Test
	public void testIsValidServiceOk() throws Exception
	{
		final String ISSUM_PROFILE = "0198TS00";
		final String ISSUM_SERVICE_ID = "PROXY_RVIA_REST_1022";
		when(session.getIsumUserProfile()).thenReturn(ISSUM_PROFILE);
		when(session.getIsumServiceId()).thenReturn(ISSUM_SERVICE_ID);
		boolean isValid = IsumValidation.IsValidService(session);
		assertTrue("IsValidServiceThrowsISUMExceptionTest: No es una sesión válida", isValid);
	}

	@Test
	public void testIsValidServiceNotOk() throws Exception
	{
		boolean isValid = IsumValidation.IsValidService(session);
		assertTrue("IsValidServiceThrowsISUMExceptionTest: Es una sesión inválida", !isValid);
	}

	@Test
	public void testIsValidServiceThrowsISUMException()
	{
		try
		{
			IsumValidation.IsValidService(null);
		}
		catch (ISUMException e)
		{
			assertTrue(true);
		}
		catch (Exception e)
		{
			assertTrue("IsValidServiceThrowsISUMExceptionTest: No lanza excepción tipo ISUMException", false);
		}
	}
}
