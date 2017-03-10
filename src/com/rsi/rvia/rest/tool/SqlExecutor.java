package com.rsi.rvia.rest.tool;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.BatchUpdateException;
import java.sql.Connection;
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

    /**
     * Hace login contra lo que corresponda. De momento la intranet.
     * 
     * @param pRequest
     *            Objeto request del jsp
     * @param pResponse
     *            Objeto response del jsp
     * @return Resultado de las ejecuciones
     */
    public String exec(HttpServletRequest pRequest, HttpServletResponse pResponse) throws MalformedClaimException,
            NoSuchAlgorithmException, InvalidKeySpecException, JoseException, IOException, LogicalErrorException,
            SQLException
    {
        String querys = pRequest.getParameter("SQLcode");
        String esquema = pRequest.getParameter("esquema");
        pLog.debug("se recibe la query (" + querys + ") contra el esquema " + esquema);
        Connection pConnection = null;
        if (querys != null)
        {
            if ("".equals(querys.trim()))
            {
                pLog.error("El contenido de la query es vacio");
                return "Nada que procesar";
            }
        }
        else
        {
            pLog.error("El contenido de la query es null");
            return "Nada que procesar";
        }
        try
        {
            if ("OracleBanca".equals(esquema))
            {
                pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
                pLog.info("Se obtiene la conexión de " + esquema);
            }
            else if ("OracleCIP".equals(esquema))
            {
                pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleCIP);
                pLog.info("Se obtiene la conexión de " + esquema);
            }
            pConnection.setAutoCommit(false);
        }
        catch (Exception ex)
        {
            pLog.error("Error detectado al obetner la conexión", ex);
            throw new LogicalErrorException(500, 9999, "Database connection failure", "No se ha conseguido una instancia de conexión a base de datos válida.", ex);
        }
        String strReturn = "";
        Statement pPreparedStatement = pConnection.createStatement();
        for (String line : querys.split(";"))
        {
            line = " " + line + " ";
            if ("".equals(line.trim()))
                continue;
            pPreparedStatement.addBatch(line);
            cntOperaciones++;
            pLog.info("Se añade al bloque de ejecución SQL la query: " + line);
        }
        try
        {
            strReturn = strReturn + "Sentencias recibidas: " + cntOperaciones + "<br>";
            pLog.info("Se van a ejecutar " + cntOperaciones + " operaciones");
            pPreparedStatement.executeBatch();
            pConnection.commit();
            cntExtito = cntOperaciones;
            pLog.info("Ejecución correcta y commit realizado");
        }
        catch (BatchUpdateException buex)
        {
            pLog.error("Se detecta un error en la ejecución del batch SQL", buex);
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
            pConnection.rollback();
        }
        finally
        {
            pPreparedStatement.close();
            pConnection.close();
            pLog.debug("Se cierra la conexión con la BBDD");
        }
        strReturn = strReturn + "Procesadas cosqln éxtito: " + cntExtito + "<br>";
        if (cntOperaciones > cntExtito)
        {
            strReturn = strReturn + "<br>Lanzado ROLLBACK. ";
        }
        pLog.debug("Se han ejecutado " + cntExtito + " operaciones correctamente de " + cntOperaciones + " queries");
        return strReturn;
    }
}
