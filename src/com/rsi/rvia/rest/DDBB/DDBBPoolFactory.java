package com.rsi.rvia.rest.DDBB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
            pReturn = loadDDBBDataSource(pDDBBProvider).getConnection();
        }
        catch (Exception ex)
        {
            pLog.error("Error al obtener la conexión con la base de datos. ", ex);
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
     * @param pStatement
     *            Objeto en caso de existir
     * @param pConnection
     *            Objeto en caso de existir
     */
    public static void closeDDBBObjects(Logger pLogger, ResultSet pResultSet, Statement pStatement,
            Connection pConnection)
    {
        try
        {
            if (pResultSet != null)
                pResultSet.close();
            if (pStatement != null)
                pStatement.close();
            if (pConnection != null)
                pConnection.close();
        }
        catch (Exception ex)
        {
            if (pLog != null)
                pLog.error("Error al cerrar los objetos de base de datos", ex);
        }
    }

    /**
     * Mñetodo sincronizado que se utiliza para obtener el datasource de conexiones a una BBDD
     * 
     * @param pDDBBProvider
     *            Proveedor de BBDD a utilizar
     * @return Datasource asociado al proveedor de datos
     * @throws Exception
     */
    private synchronized static DataSource loadDDBBDataSource(DDBBProvider pDDBBProvider) throws Exception
    {
        DataSource pReturn = null;
        pLog.error("Se procede a conectar con la BD de tipo ", pDDBBProvider.name());
        switch (pDDBBProvider)
        {
            case OracleBanca:
                if (pOracleBanca == null)
                {
                    pOracleBanca = DDBBPool.getDatasourceFromBancaOracleServerPool();
                }
                pReturn = pOracleBanca;
                break;
            case OracleCIP:
                if (pOracleCIP == null)
                {
                    pOracleCIP = DDBBPool.getDatasourceFromCIPOracleServerPool();
                }
                pReturn = pOracleCIP;
                break;
            default:
                break;
        }
        return pReturn;
    }
}
