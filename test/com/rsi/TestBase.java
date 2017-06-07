package com.rsi;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestBase
{
    protected final String       TOKEN = "HqJ/6fJk2+8jXi90/VU/H29V08ygqXMTqSbSkP+r715kWSdhPR6eEGqAM86sATxKgtnVPHAee2dLyX+MjxGQCqA2U6MTejqDbcjvidfIemotQcsYp/GzRhPLqFds+S6nt3MMpTchkEpTzoH0IRuTy3LaZaNghZmC+iOdiEMRiN7GGnVyJrD/P0y2hrwScmOm96Xjlu4nAAPc/+cRUyrzKtBzNcxyYxbiII8rw4S0dbruN9mPQMTFJCVPK425UWSmP3eQ/FbElgu3wZxMy/Ekgw==";
    @Mock
    protected HttpServletRequest request;
    @Mock
    protected HttpSession        session;

    @Before
    public void setUp() throws Exception
    {
        MockitoAnnotations.initMocks(this);
    }
}
