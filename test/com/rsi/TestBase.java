package com.rsi;

import static org.mockito.Mockito.when;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.rsi.rvia.rest.session.SessionRviaData;

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

   protected SessionRviaData buildSession(boolean hasRequestToken, boolean hasSessionToken) throws Exception
   {
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
