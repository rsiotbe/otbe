package com.rsi.rvia.multibank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Hashtable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.rsi.rvia.rest.session.SessionRviaData;

public class CssMultiBankProcessorTest
{
   @Before
   public void setUp() throws Exception
   {
      CssMultiBankProcessor.htCacheData = new Hashtable<String, String>();
   }

   @After
   public void tearDown() throws Exception
   {
   }

   @Test
   public void testGetSizeCacheNoData()
   {
      int size = CssMultiBankProcessor.getSizeCache();
      assertTrue("testGetSizeCacheNoData: size no es 0", size == 0);
   }

   @Test
   public void testGetSizeCache()
   {
      CssMultiBankProcessor.htCacheData.put("foo", "bar");
      int size = CssMultiBankProcessor.getSizeCache();
      assertTrue("testGetSizeCache: size es 0", size != 0);
   }

   @Test
   public void testRestartCache()
   {
      CssMultiBankProcessor.restartCache();
      assertNotNull("testRestartCache: htCacheData es null", CssMultiBankProcessor.htCacheData);
   }

   @Test
   public void testProcessXHTML() throws Exception
   {
      final String TARGET_URL = "http://bar";
      CssMultiBankProcessor.htCacheData.put(SessionRviaData.CODIGO_BANCO_COOPERATIVO_ESPANOL
            + "_http://foo", TARGET_URL);
      String documentStr = "<html><link href=\"http://foo\" rel=\"stylesheet\"></html>";
      Document document = Jsoup.parse(documentStr, "", Parser.htmlParser());
      Document processed = CssMultiBankProcessor.processXHTML(document, null);
      assertNotNull("testProcessXHTML: processed es null", processed);
      assertEquals("testProcessXHTML: el documento no ha sido procesado", processed.select("link[href]").attr("href"), TARGET_URL);
   }

   @Test
   public void testProcessXHTMLCache() throws Exception
   {
      String documentStr = "<html><link href=\"http://foo\" rel=\"stylesheet\"></html>";
      Document document = Jsoup.parse(documentStr, "", Parser.htmlParser());
      Document processed = CssMultiBankProcessor.processXHTML(document, null);
      assertNotNull("testProcessXHTML: processed es null", processed);
   }
}
