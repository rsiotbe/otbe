package com.rsi.rvia.translates;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Hashtable;
import org.junit.Test;
import com.rsi.BaseTest;

public class TranslateProcessorTest extends BaseTest
{
   @Override
   public void setUp() throws Exception
   {
      super.setUp();
      TranslateProcessor.htCacheData = null;
   }

   @Test
   public void testGetSizeCache()
   {
      int size = TranslateProcessor.getSizeCache();
      assertTrue("testGetSizeCache: size es valor numérico negativo", size > -1);
   }

   @Test
   public void testRestartCache()
   {
      TranslateProcessor.restartCache();
      assertNull("testRestartCache: no es null", TranslateProcessor.htCacheData);
      TranslateProcessor.htCacheData = new Hashtable<String, TranslateEntry>();
      TranslateProcessor.restartCache();
      assertNotNull("testRestartCache: es null", TranslateProcessor.htCacheData);
      TranslateProcessor.htCacheData.put("foo", null);
      assertTrue("testRestartCache: tiene longitud", TranslateProcessor.htCacheData.size() == 0);
   }

   @Test
   public void testProcessIds()
   {
      fail("Not yet implemented");
   }

   @Test
   public void testProcessXHTMLStringSessionRviaData()
   {
      fail("Not yet implemented");
   }

   @Test
   public void testProcessXHTMLDocumentSessionRviaData()
   {
      fail("Not yet implemented");
   }
}
