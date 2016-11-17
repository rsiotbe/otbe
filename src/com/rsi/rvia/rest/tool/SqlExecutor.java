package com.rsi.rvia.rest.tool;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.client.MiqAdminValidator;
import com.rsi.rvia.rest.error.exceptions.LogicalErrorException;

public class SqlExecutor
{
    private static Logger pLog           = LoggerFactory.getLogger(MiqAdminValidator.class);
    private int           cntOperaciones = 0;
    private int           cntExtito      = 0;
    private int           cntErrores     = 0;
    private int           cntDesconocido = 0;

    public String exec(HttpServletRequest pRequest, HttpServletResponse pResponse) throws MalformedClaimException,
            NoSuchAlgorithmException, InvalidKeySpecException, JoseException, IOException, LogicalErrorException,
            SQLException
    {
        String querys = pRequest.getParameter("SQLcode");
        String esquema = pRequest.getParameter("esquema");
        Connection pConnection = null;
        if (querys != null)
        {
            if ("".equals(querys.trim()))
            {
                return "Nada que procesar";
            }
        }
        else
        {
            return "Nada que procesar";
        }
        try
        {
            if ("OracleBanca".equals(esquema))
            {
                pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            }
            else if ("OracleCIP".equals(esquema))
            {
                pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleCIP);
            }
            pConnection.setAutoCommit(false);
        }
        catch (Exception ex)
        {
            throw new LogicalErrorException(500, 9999, "Database connection failure", "No se ha conseguido una instancia de conexión a base de datos válida.", ex);
        }
        String strReturn = "";
        Statement pPreparedStatement = pConnection.createStatement();
        for (String line : querys.split(";"))
        {
            line = " " + line + " ";
            pPreparedStatement.addBatch(line);
            cntOperaciones++;
            // strReturn = processQuerys(line, pConnection, strReturn) + "<br>";
        }
        try
        {
            pPreparedStatement.executeBatch();
            strReturn = strReturn + "Sentencias recibidas: " + cntOperaciones;
        }
        catch (BatchUpdateException buex)
        {
            buex.printStackTrace();
            int[] updateCounts = buex.getUpdateCounts();
            for (int i = 0; i < updateCounts.length; i++)
            {
                if (updateCounts[i] >= 0)
                {
                    cntExtito++;
                }
                else if (updateCounts[i] == Statement.SUCCESS_NO_INFO)
                {
                    cntDesconocido++;
                }
                else if (updateCounts[i] == Statement.EXECUTE_FAILED)
                {
                    cntErrores++;
                }
            }
        }
        finally
        {
            strReturn = strReturn + "Procesadas con éxtito: " + cntExtito + "<br>";
            strReturn = strReturn + "Procesadoas con error: " + cntErrores + "<br>";
            strReturn = strReturn + "Información no disponible: " + cntDesconocido + "<br>";
            pConnection.rollback();
            pConnection.close();
        }
        /*
         * catch (SQLException ex) { strReturn = strReturn + "ERROR: " + ex.getMessage() + "<br>"; strReturn = strReturn
         * + "---------- StackTrace: " + ex.getStackTrace(); }
         */
        return strReturn;
    }

    private String processQuerys(String line, Connection pConnection, String salida) throws SQLException
    {
        String retorno = "No se ha procesado la solicitud";
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        pPreparedStatement = pConnection.prepareStatement(line);
        try
        {
            pResultSet = pPreparedStatement.executeQuery();
            salida = salida + "Procesado: " + line;
            pResultSet.close();
            cntOperaciones++;
        }
        catch (SQLException ex)
        {
            salida = salida + "ERROR: " + ex.getMessage() + "<br>" + ex.getStackTrace() + "<br>";
            salida = salida + "---------- Sentencia: " + line + "<br>" + "---------- StackTrace: " + ex.getStackTrace();
            cntErrores++;
        }
        finally
        {
            pPreparedStatement.close();
        }
        // querys = querys.replaceAll("\"", "\\\"");
        return salida;
    }
}
