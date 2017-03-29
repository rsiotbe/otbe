package com.rsi.rvia.rest.DDBB;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DDBBPool
{
    private static String   BANCA_JNDI_DDBB  = "RVIA_REST_BANCA";
    private static String   BANCA_JNDI_CI    = "RVIA_REST_CI";
    private static String   BANCA_JNDI_MYSQL = "RVIA_REST_MYSQL";
    protected static Logger pLog             = LoggerFactory.getLogger(DDBBPool.class);

    /**
     * Recupera las propiedades de configuración de Banca Oracle para configurar un pool de conexiones del servidor
     * 
     * @return DataSource con la configuración de Banca Oracle ya cargada
     * @throws Exception
     */
    public static DataSource getDatasourceFromBancaOracleServerPool() throws Exception
    {
        Context initCtx = new InitialContext();
        return (DataSource) initCtx.lookup("java:comp/env/jdbc/" + BANCA_JNDI_DDBB);
    }

    /**
     * Recupera las propiedades de configuración de CIP Oracle para configurar un pool de conexiones del servidor
     * 
     * @return DataSource con la configuración de CIP Oracle ya cargada
     * @throws Exception
     */
    public static DataSource getDatasourceFromCIPOracleServerPool() throws Exception
    {
        Context initCtx = new InitialContext();
        return (DataSource) initCtx.lookup("java:comp/env/jdbc/" + BANCA_JNDI_CI);
    }

    /**
     * Recupera las propiedades de configuración de MySql para configurar un pool de conexiones del servidor
     * 
     * @return DataSource con la configuración de MySql ya cargada
     * @throws Exception
     */
    public static DataSource getDatasourceFromMySqlServerPool() throws Exception
    {
        Context initCtx = new InitialContext();
        return (DataSource) initCtx.lookup("java:comp/env/jdbc/" + BANCA_JNDI_MYSQL);
    }
}
