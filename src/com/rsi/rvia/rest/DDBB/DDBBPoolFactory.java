package com.rsi.rvia.rest.DDBB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Clase que gestiona la solicitud de una instancia de base de datos */
public class DDBBPoolFactory
{
    /** Enumeración que contiene los diferentes tipos de Pool de BBDD implementados en la aplicación */
    public enum DDBBProvider
    {
        MySql, OracleBanca, OracleCIP;
    }

    private static Logger     pLog         = LoggerFactory.getLogger(DDBBProvider.class);
    private static DataSource pOracleBanca = null;
    private static DataSource pOracleCIP   = null;
    private static DataSource pMySql       = null;

    /**
     * Obtiene la clase que gestiona la conexión con base de datos
     * 
     * @param pDDBBProvider
     *            Tipo de base de datos a instanciar
     * @return Conexión con la base de datos seleccionar
     * @throws Exception
     */
    public static Connection getDDBB(DDBBProvider pDDBBProvider) throws Exception
    {
        Connection pReturn = null;
        try
        {
            switch (pDDBBProvider)
            {
                case OracleBanca:
                    pOracleBanca = loadDDBBDataSource(pDDBBProvider, pOracleBanca, "/Banca.OracleConfig.properties");
                    pReturn = pOracleBanca.getConnection();
                    break;
                case OracleCIP:
                    pOracleCIP = loadDDBBDataSource(pDDBBProvider, pOracleBanca, "/CIP.OracleConfig.properties");
                    pReturn = pOracleCIP.getConnection();
                    break;
                case MySql:
                    pOracleCIP = loadDDBBDataSource(pDDBBProvider, pOracleBanca, "/MySqlConfig.properties");
                    pReturn = pMySql.getConnection();
                    break;
            }
        }
        catch (Exception ex)
        {
            pLog.error("Error al obtener la conexión con la base de datos", ex);
        }
        return pReturn;
    }

    /**
     * Encapsula el cierre de objetos d e base de datos abiertos por el código
     * 
     * @param pLogger
     *            Logger de la aplicación
     * @param pResultSet
     *            Objeto en caso de existir
     * @param pPreparedStatement
     *            Objeto en caso de existir
     * @param pConnection
     *            Objeto en caso de existir
     */
    public static void closeDDBBObjects(Logger pLogger, ResultSet pResultSet, PreparedStatement pPreparedStatement,
            Connection pConnection)
    {
        try
        {
            if (pResultSet != null)
                pResultSet.close();
            if (pPreparedStatement != null)
                pPreparedStatement.close();
            if (pConnection != null)
                pConnection.close();
        }
        catch (Exception ex)
        {
            if (pLogger != null)
                pLogger.error("Error al cerrar los objetos de base de datos", ex);
        }
    }

    private synchronized static DataSource loadDDBBDataSource(DDBBProvider pDDBBProvider, DataSource pDataSource,
            String strPropertiesFile) throws Exception
    {
        if (pDataSource == null)
        {
            pLog.debug("Se procede a conectar con la base de datos de tipo " + pDDBBProvider.name());
            /* se leen las propiedaddes de de esta conexión */
            Properties pProperties = new Properties();
            pProperties.load(DDBBPoolFactory.class.getResourceAsStream(strPropertiesFile));
            boolean fUseLocalPool = Boolean.parseBoolean(pProperties.getProperty("fUseLocalPool"));
            if (fUseLocalPool)
            {
                /* por conexión con el pool del servidor */
                pLog.debug("Se utiliza el pool de base de datos del servidor");
                switch (pDDBBProvider)
                {
                    case OracleBanca:
                        pDataSource = DDBBPool.getDatasourceFromBancaOracleServerPool();
                        break;
                    case OracleCIP:
                        pDataSource = DDBBPool.getDatasourceFromCIPOracleServerPool();
                        break;
                    case MySql:
                        pDataSource = DDBBPool.getDatasourceFromMySqlServerPool();
                        break;
                    default:
                        pLog.error("Proveedor de base de datos no encontrado, no existe configuración para este proveedor. Proveedor:"
                                + pDDBBProvider.name());
                        pDataSource = null;
                        break;
                }
            }
            else
            {
                /* por conexión al pool interno generado por la aplicación */
                pLog.debug("Se utiliza el pool local de base de datos");
                switch (pDDBBProvider)
                {
                    case OracleBanca:
                        pDataSource = DDBBPool.getDatasourceFromBancaOracleLocalPool(pProperties);
                        break;
                    case OracleCIP:
                        pDataSource = DDBBPool.getDatasourceFromCIPOracleLocalPool(pProperties);
                        break;
                    case MySql:
                        pDataSource = DDBBPool.getDatasourceFromMySqlLocalPool(pProperties);
                        break;
                    default:
                        pLog.error("Proveedor de base de datos no encontrado, no existe configuración para este proveedor. Proveedor:"
                                + pDDBBProvider.name());
                        pDataSource = null;
                        break;
                }
            }
        }
        return pDataSource;
    }
}
