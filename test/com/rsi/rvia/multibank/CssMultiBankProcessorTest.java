package com.rsi.rvia.multibank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Hashtable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import com.rsi.Constants;
import com.rsi.TestBase;
import com.rsi.rvia.rest.multibank.CssMultiBankProcessor;
import com.rsi.rvia.rest.session.RequestConfig;

public class CssMultiBankProcessorTest extends TestBase
{
    @Mock
    RequestConfig reqConf;

    @Before
    public void setUp() throws Exception
    {
        super.setUp();
        CssMultiBankProcessor.htCacheData = new Hashtable<String, String>();
        Mockito.when(reqConf.getNRBE()).thenReturn(Constants.CODIGO_ENTIDAD_FORMACION);
    }

    @Test
    public void testGetSizeCacheNoData()
    {
        int size = CssMultiBankProcessor.getCacheSize();
        assertTrue("testGetSizeCacheNoData: size no es 0", size == 0);
    }

    @Test
    public void testGetSizeCache()
    {
        CssMultiBankProcessor.htCacheData.put("foo", "bar");
        int size = CssMultiBankProcessor.getCacheSize();
        assertTrue("testGetSizeCache: size es 0", size != 0);
    }

    @Test
    public void testRestartCache()
    {
        CssMultiBankProcessor.resetCache();
        assertNotNull("testRestartCache: htCacheData es null", CssMultiBankProcessor.htCacheData);
    }

    @Test
    public void testProcessXHTML() throws Exception
    {
        final String TARGET_URL = "http://bar";
        CssMultiBankProcessor.htCacheData.put(Constants.CODIGO_ENTIDAD_FORMACION + "_http://foo", TARGET_URL);
        String documentStr = "<html><link href=\"http://foo\" rel=\"stylesheet\"></html>";
        Document document = Jsoup.parse(documentStr, "", Parser.htmlParser());
        Document processed = CssMultiBankProcessor.processXHTML(document, reqConf);
        assertNotNull("testProcessXHTML: processed es null", processed);
        assertEquals("testProcessXHTML: el documento no ha sido procesado", processed.select("link[href]").attr("href"), TARGET_URL);
    }

    @Test
    public void testProcessXHTMLCache() throws Exception
    {
        String documentStr = "<html><link href=\"http://foo\" rel=\"stylesheet\"></html>";
        Document document = Jsoup.parse(documentStr, "", Parser.htmlParser());
        Document processed = CssMultiBankProcessor.processXHTML(document, reqConf);
        assertNotNull("testProcessXHTML: processed es null", processed);
    }
}
