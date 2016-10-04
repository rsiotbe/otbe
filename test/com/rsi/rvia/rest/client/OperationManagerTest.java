package com.rsi.rvia.rest.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import com.rsi.BaseTest;
import com.rsi.rvia.rest.operation.MiqQuests;

public class OperationManagerTest extends BaseTest
{
   @Mock
   UriInfo   uriInfo;
   @Mock
   MiqQuests miqQuests;

   @Test
   public void testProcessDataFromRvia() throws Exception
   {
      String strData = "";
      MediaType mediaType = null;
      MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
      when(request.getSession(true)).thenReturn(session);
      when(request.getParameter("token")).thenReturn(TOKEN);
      when(request.getMethod()).thenReturn("GET");
      Mockito.when(uriInfo.getPathParameters()).thenReturn(map);
      Mockito.when(uriInfo.getPath()).thenReturn("rsiapi/contracts");
      Mockito.when(miqQuests.getComponentType()).thenReturn("API");
      Response response = OperationManager.processDataFromRvia(request, uriInfo, strData, mediaType);
      assertNotNull("testProcessDataFromRvia: response es null", response);
      assertEquals("testProcessDataFromRvia: response erronea", response.getStatus(), 200);
   }

   @Test
   public void testProcessDataFromRviaError() throws Exception
   {
      String strData = "";
      MediaType mediaType = null;
      MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
      map.add("card", "foo");
      when(request.getSession(true)).thenReturn(session);
      when(request.getParameter("token")).thenReturn(TOKEN);
      when(request.getMethod()).thenReturn("GET");
      Mockito.when(uriInfo.getPathParameters()).thenReturn(map);
      Mockito.when(uriInfo.getPath()).thenReturn("foo/card");
      Mockito.when(miqQuests.getComponentType()).thenReturn("RVIA");
      Response response = OperationManager.processDataFromRvia(request, uriInfo, strData, mediaType);
      assertNotNull("testProcessDataFromRvia: response es null", response);
      assertEquals("testProcessDataFromRvia: response es null", response.getStatus(), 500);
   }

   @Test
   public void testProcessTemplateFromRvia() throws Exception
   {
      String strData = null;
      MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
      map.add("foo", "bar");
      when(request.getSession(true)).thenReturn(session);
      when(request.getParameter("token")).thenReturn(TOKEN);
      when(request.getMethod()).thenReturn("GET");
      Mockito.when(uriInfo.getPathParameters()).thenReturn(map);
      Mockito.when(uriInfo.getPath()).thenReturn("foo/bar");
      Mockito.when(miqQuests.getComponentType()).thenReturn("RVIA");
      Response response = OperationManager.processTemplateFromRvia(request, uriInfo, strData);
      assertNotNull("testProcessTemplateFromRvia: response es null", response);
      // No se crean los MiqQuests.
      assertEquals("testProcessTemplateFromRvia: response es null", response.getStatus(), 500);
   }

   @Test
   public void testProcessForAPI() throws Exception
   {
      String strData = null;
      MediaType mediaType = null;
      MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
      map.add("foo", "bar");
      when(request.getSession(true)).thenReturn(session);
      when(request.getParameter("token")).thenReturn(TOKEN);
      when(request.getMethod()).thenReturn("GET");
      Mockito.when(uriInfo.getPathParameters()).thenReturn(map);
      Mockito.when(uriInfo.getPath()).thenReturn("foo/bar");
      Mockito.when(miqQuests.getComponentType()).thenReturn("RVIA");
      Response response = OperationManager.processForAPI(request, uriInfo, strData, mediaType);
      assertNotNull("testProcessForAPI: response es null", response);
      // No se crean los MiqQuests.
      assertEquals("testProcessForAPI: response es null", response.getStatus(), 401);
   }

   @Test
   public void testProcessForAPILogin() throws Exception
   {
      String strData = null;
      MediaType mediaType = null;
      MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
      when(request.getSession(true)).thenReturn(session);
      when(request.getParameter("token")).thenReturn(TOKEN);
      when(request.getMethod()).thenReturn("GET");
      Mockito.when(uriInfo.getPathParameters()).thenReturn(map);
      Mockito.when(uriInfo.getPath()).thenReturn("foo/login");
      Mockito.when(miqQuests.getComponentType()).thenReturn("RVIA");
      Response response = OperationManager.processForAPI(request, uriInfo, strData, mediaType);
      assertNotNull("testProcessForAPILogin: response es null", response);
      // No se crean los MiqQuests.
      assertEquals("testProcessForAPILogin: response es null", response.getStatus(), 500);
   }

   @Test
   public void testProcessTemplate()
   {
      String strData = null;
      MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
      map.add("foo", "bar");
      Mockito.when(uriInfo.getPathParameters()).thenReturn(map);
      Mockito.when(uriInfo.getPath()).thenReturn("foo/bar");
      Response response = OperationManager.processTemplate(request, uriInfo, strData);
      assertNotNull("testProcessTemplate: response es null", response);
      // No se crean los MiqQuests.
      assertEquals("testProcessTemplate: response es null", response.getStatus(), 500);
   }

   @Test
   public void testProcessGenericAPP()
   {
      String strData = null;
      MediaType mediaType = null;
      MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
      map.add("foo", "bar");
      when(request.getSession(true)).thenReturn(session);
      when(request.getParameter("token")).thenReturn(TOKEN);
      when(request.getMethod()).thenReturn("GET");
      Mockito.when(uriInfo.getPathParameters()).thenReturn(map);
      Mockito.when(uriInfo.getPath()).thenReturn("foo/bar");
      Mockito.when(miqQuests.getComponentType()).thenReturn("RVIA");
      Response response = OperationManager.processGenericAPP(request, uriInfo, strData, mediaType);
      assertNotNull("testProcessGenericAPP: response es null", response);
      // No se crean los MiqQuests.
      assertEquals("testProcessGenericAPP: response es null", response.getStatus(), 500);
   }

   @Ignore
   @Test
   public void testGetValidateSession()
   {
   }
}
