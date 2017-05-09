package com.rsi.rvia.rest.endpoint.rsiapi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.client.QueryCustomizer;
import com.rsi.rvia.rest.error.exceptions.ApplicationException;

public class AcuerdosRuralvia
{
    private static Logger   pLog      = LoggerFactory.getLogger(QueryCustomizer.class);
    private static String[] _reserved = { "select", "update", "delete", "insert", "alter", "drop", "create" };

    public static String[] getRviaContractsDecodeAliases(String strNumTarjeta) throws ApplicationException
    {
        strNumTarjeta = protectInject(strNumTarjeta);
        if (strNumTarjeta == null)
        {
            throw new ApplicationException(500, 99999, "No permitido", "Palabra reservada como parámetro", new Exception());
        }
        String[] strRetorno = new String[2];
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        try
        {
            String strQuery = " select " + "   substr(b.cta_aso,11,20) \"acuerdo\", "
                    + "   trim (b.descr_txt) \"txtproducto\" " + " from bel.belts009 b "
                    + "   where trim(b.tarjeta_cod) = '" + strNumTarjeta + "' " + " union " + " select "
                    + "   acuerdo, " + "   trim (c.descr_txt) \"txtproducto\" " + " from BEL.BDPTB083_TARJETAS_MP c "
                    + " where trim(c.tarjeta_cod) = '" + strNumTarjeta + "' ";
            pLog.info("Query para extracción de alias de banca: " + strQuery);
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pResultSet = pPreparedStatement.executeQuery();
            String strDecodeAliases = "";
            String strListaAcuerdos = "";
            String coma = "";
            while (pResultSet.next())
            {
                String strAcuerdo = (String) pResultSet.getString("acuerdo");
                String strAlias = (String) pResultSet.getString("txtproducto");
                strDecodeAliases = strDecodeAliases + coma + " " + strAcuerdo + "  , '" + strAlias + "'";
                strListaAcuerdos = strListaAcuerdos + coma + " " + strAcuerdo;
                coma = ",";
            }
            if (coma.equals(","))
            {
                strRetorno[0] = strDecodeAliases;
                strRetorno[1] = strListaAcuerdos;
            }
        }
        catch (Exception ex)
        {
            pLog.error("Error al comprobar la última carga", ex);
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
        }
        return strRetorno;
    }

    private static String protectInject(String strFields)
    {
        if (strFields == null)
            return null;
        int i;
        for (i = 0; i < _reserved.length; i++)
        {
            if (strFields.indexOf(_reserved[i]) != -1)
            {
                strFields = null;
            }
        }
        return strFields;
    }
}
