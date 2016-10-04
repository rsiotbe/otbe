package com.rsi.rvia.rest.client;

import static org.junit.Assert.assertTrue;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.rsi.BaseTest;

public class SaveExitHierarchyTest extends BaseTest
{
   @Before
   public void setUp() throws Exception
   {
   }

   @After
   public void tearDown() throws Exception
   {
   }

   @Test
   public void testProcess() throws Exception
   {
      JSONObject jsonData = new JSONObject("{response:{foo:'bar'}}");
      int idMiq = 0;
      String strMethod = "method";
      SaveExitHierarchy.process(jsonData, idMiq, strMethod);
      assertTrue(true);
   }
}
