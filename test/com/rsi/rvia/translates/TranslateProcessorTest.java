package com.rsi.rvia.translates;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
      TranslateProcessor.htCacheData.put("foo", new TranslateEntry("foo"));
      TranslateProcessor.restartCache();
      assertNotNull("testRestartCache: es null", TranslateProcessor.htCacheData);
      assertTrue("testRestartCache: tiene longitud", TranslateProcessor.htCacheData.size() == 0);
   }
}
