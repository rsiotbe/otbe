package com.rsi.rvia.rest.client;

import static org.junit.Assert.assertTrue;
import org.json.JSONObject;
import org.junit.Test;
import com.rsi.BaseTest;

public class SaveExitHierarchyTest extends BaseTest
{
   @Test
   public void testProcess() throws Exception
   {
      JSONObject jsonData = new JSONObject("{response:{foo:'bar'}}");
      int idMiq = 0;
      String strMethod = "method";
      try
      {
         SaveExitHierarchy.process(jsonData, idMiq, strMethod);
         assertTrue(true);
      }
      catch (Exception e)
      {
         assertTrue("testProcess: Error", false);
      }
   }
}
