package com.rsi.rvia.translates;

import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.rsi.TestBase;

public class TranslateCacheTest extends TestBase
{
	@Override
	public void setUp() throws Exception
	{
		super.setUp();
	}

	@Test
	public void testGetSizeCache()
	{
		int size = TranslateCache.getCacheSize();
		assertTrue("testGetSizeCache: size es valor numérico negativo", size > -1);
	}

	@Test
	public void testRestartCache()
	{
		/*
		 * TranslateCache.resetCache(); assertNull("testRestartCache: no es null",
		 * TranslateProcessor.htTranslateCacheData); TranslateCache..htTranslateCacheData = new Hashtable<String,
		 * Hashtable<String, TranslateEntry>>(); Hashtable<String, TranslateEntry> aux = new Hashtable<String,
		 * TranslateEntry>(); aux.put("foo", new TranslateEntry("a", "b"));
		 * TranslateProcessor.htTranslateCacheData.put("foo", new Hashtable<String, TranslateEntry>());
		 * TranslateProcessor.resetCache(); assertNotNull("testRestartCache: es null",
		 * TranslateProcessor.htTranslateCacheData); assertTrue("testRestartCache: tiene longitud",
		 * TranslateProcessor.htTranslateCacheData.size() == 0);
		 */}
}
