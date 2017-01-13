package com.rsi.rvia.translates;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.Hashtable;
import org.junit.Test;
import com.rsi.TestBase;

public class TranslateProcessorTest extends TestBase
{
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
		TranslateProcessor.htTranslateCacheData = null;
	}

	@Test
	public void testGetSizeCache()
	{
		int size = TranslateProcessor.getCacheSize();
		assertTrue("testGetSizeCache: size es valor numérico negativo", size > -1);
	}

	@Test
	public void testRestartCache()
	{
		TranslateProcessor.resetCache();
		assertNull("testRestartCache: no es null", TranslateProcessor.htTranslateCacheData);
		TranslateProcessor.htTranslateCacheData = new Hashtable<String, Hashtable<String, TranslateEntry>>();
		Hashtable<String, TranslateEntry> aux = new Hashtable<String, TranslateEntry>();
		aux.put("foo", new TranslateEntry("a", "b"));
		TranslateProcessor.htTranslateCacheData.put("foo", new Hashtable<String, TranslateEntry>());
		TranslateProcessor.resetCache();
		assertNotNull("testRestartCache: es null", TranslateProcessor.htTranslateCacheData);
		assertTrue("testRestartCache: tiene longitud", TranslateProcessor.htTranslateCacheData.size() == 0);
	}
}
