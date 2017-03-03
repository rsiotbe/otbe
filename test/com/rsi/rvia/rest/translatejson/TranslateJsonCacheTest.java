package com.rsi.rvia.rest.translatejson;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.endpoint.ruralvia.translatejson.TranslateJsonCache;

/**
 * The Class TranslateJsonCacheTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ TranslateJsonCache.class, DDBBPoolFactory.class })
public class TranslateJsonCacheTest
{
    /** The cache data. */
    @Mock
    private Hashtable<String, String> htTranslateCacheData = new Hashtable<String, String>();
    /** The connection. */
    @Mock
    Connection                        pConnection;
    /** The prepared statement. */
    @Mock
    PreparedStatement                 pPreparedStatement;
    /** The result set. */
    @Mock
    ResultSet                         pResultSet;
    /** The translate json cache class. */
    @InjectMocks
    TranslateJsonCache                translateJsonCache;

    /**
     * Setup test.
     */
    @Before
    public void setUp()
    {
        htTranslateCacheData.put("strCode", "Mars");
        htTranslateCacheData.put("strDesc", "32");
        htTranslateCacheData.put("city", "NY");
        /*
         * htTranslateCacheData.put("1", "Uno"); htTranslateCacheData.put("2", "Dos"); htTranslateCacheData.put("3",
         * "Tres");
         */
    }

    /**
     * Gets the cache size test.
     * 
     * @return the cache size test
     * @throws IllegalArgumentException
     *             the illegal argument exception
     * @throws IllegalAccessException
     *             the illegal access exception
     */
    @Test
    public void getCacheSizeTest() throws IllegalArgumentException, IllegalAccessException
    {
        Field field = PowerMockito.field(TranslateJsonCache.class, "htTranslateCacheData");
        field.set(TranslateJsonCache.class, htTranslateCacheData);
        int size = TranslateJsonCache.getCacheSize();
        assertTrue(size == 3);
    }

    /**
     * Gets the cache size test null.
     * 
     * @return the cache size test null
     * @throws IllegalArgumentException
     *             the illegal argument exception
     * @throws IllegalAccessException
     *             the illegal access exception
     */
    @Test
    public void getCacheSizeTest_Null() throws IllegalArgumentException, IllegalAccessException
    {
        TranslateJsonCache.resetCache();
        int size = TranslateJsonCache.getCacheSize();
        assertTrue(size == 0);
    }

    /**
     * Reset cache test.
     * 
     * @throws IllegalArgumentException
     *             the illegal argument exception
     * @throws IllegalAccessException
     *             the illegal access exception
     */
    @Test
    public void resetCacheTest() throws IllegalArgumentException, IllegalAccessException
    {
        Field field = PowerMockito.field(TranslateJsonCache.class, "htTranslateCacheData");
        field.set(TranslateJsonCache.class, htTranslateCacheData);
        int size = TranslateJsonCache.getCacheSize();
        assertTrue(size == 3);
        TranslateJsonCache.resetCache();
        size = TranslateJsonCache.getCacheSize();
        assertTrue(size == 0);
    }

    /**
     * Reset cache test null.
     * 
     * @throws IllegalArgumentException
     *             the illegal argument exception
     * @throws IllegalAccessException
     *             the illegal access exception
     */
    @Test
    public void resetCacheTest_Null() throws IllegalArgumentException, IllegalAccessException
    {
        TranslateJsonCache.resetCache();
        int size = TranslateJsonCache.getCacheSize();
        assertTrue(size == 0);
    }

    /**
     * Cache to string test.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void cacheToStringTest() throws Exception
    {
        Field field = PowerMockito.field(TranslateJsonCache.class, "htTranslateCacheData");
        field.set(TranslateJsonCache.class, htTranslateCacheData);
        String strCache = TranslateJsonCache.cacheToString();
        assertTrue(!strCache.isEmpty());
    }

    /**
     * Checks if is error code not value in cache and error.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void isErrorCodeTest_NotValueInCacheAndError() throws Exception
    {
        System.out.println("isErrorCodeTest_NotValueInCacheAndError");
        Field field = PowerMockito.field(TranslateJsonCache.class, "htTranslateCacheData");
        field.set(TranslateJsonCache.class, htTranslateCacheData);
        String strQuery = "SELECT tiporesp,desc_coderr FROM bdptb276_ERR_RVIA where id_coderr = ?";
        // String strQuery = "SELECT id_coderr,tiporesp FROM bdptb276_err_rvia where id_coderr =1";
        PowerMockito.mockStatic(DDBBPoolFactory.class);
        PowerMockito.when(DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca)).thenReturn(pConnection);
        when(pConnection.prepareStatement(strQuery)).thenReturn(pPreparedStatement);
        when(pPreparedStatement.executeQuery()).thenReturn(pResultSet);
        // when(pResultSet.getString("id_coderr")).thenReturn("uno");
        // when(pResultSet.getString("tiporesp")).thenReturn("dos");
        when(pResultSet.getString("tiporesp")).thenReturn("uno");
        when(pResultSet.getString("desc_coderr")).thenReturn("dos");
        when(pResultSet.next()).thenReturn(true).thenReturn(false);
        boolean error = TranslateJsonCache.isErrorCode("uno", "dos");
        int size = TranslateJsonCache.getCacheSize();
        try
        {
            System.out.println("isErrorCodeTest_NotValueInCacheAndError:" + TranslateJsonCache.cacheToString());
            System.out.println("isErrorCodeTest_NotValueInCacheAndError:" + error);
        }
        catch (Exception e)
        {
        }
        assertTrue(size == 3);
        assertTrue(error);
    }

    /**
     * Checks if is error code value in cache and error.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void isErrorCodeTest_ValueInCacheAndError() throws Exception
    {
        Field field = PowerMockito.field(TranslateJsonCache.class, "htTranslateCacheData");
        field.set(TranslateJsonCache.class, htTranslateCacheData);
        boolean error = TranslateJsonCache.isErrorCode("strCode", "Mars");
        int size = TranslateJsonCache.getCacheSize();
        assertTrue(size == 3);
        assertTrue(error);
    }

    /**
     * Checks if is error code not value in cache and no error.
     * 
     * @throws Exception
     *             the exception
     */
    @Test
    public void isErrorCodeTest_NotValueInCacheAndNoError() throws Exception
    {
        System.out.println("isErrorCodeTest_NotValueInCacheAndNoError");
        Field field = PowerMockito.field(TranslateJsonCache.class, "htTranslateCacheData");
        field.set(TranslateJsonCache.class, htTranslateCacheData);
        // String strQuery = "SELECT codigo,descripcion FROM bdptb079_idioma where codigo in (?)";
        String strQuery = "SELECT tiporesp,desc_coderr FROM bdptb276_err_rvia where id_coderr = ?";
        PowerMockito.mockStatic(DDBBPoolFactory.class);
        PowerMockito.when(DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca)).thenReturn(pConnection);
        when(pConnection.prepareStatement(strQuery)).thenReturn(pPreparedStatement);
        when(pPreparedStatement.executeQuery()).thenReturn(pResultSet);
        // when(pResultSet.getString("codigo")).thenReturn("uno");
        // when(pResultSet.getString("descripcion")).thenReturn("dos");
        when(pResultSet.getString("tiporesp")).thenReturn("uno");
        when(pResultSet.getString("desc_coderr")).thenReturn("dos");
        when(pResultSet.next()).thenReturn(true).thenReturn(false);
        boolean error = TranslateJsonCache.isErrorCode("99", "00");
        int size = TranslateJsonCache.getCacheSize();
        try
        {
            System.out.println("isErrorCodeTest_NotValueInCacheAndNoError:" + error);
        }
        catch (Exception e)
        {
        }
        assertTrue(size == 3);
        assertFalse(error);
    }
}
