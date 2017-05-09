package com.rsi.rvia.rest.endpoint.rsiapi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.http.HttpServletRequest;
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

    public static String[] getRviaContractsDecodeAliases(HttpServletRequest request) throws ApplicationException
    {
        String strNumTarjeta = protectInject(request.getParameter("codTarjeta"));
        String strEntidad = protectInject(request.getParameter("codEntidad"));
        String strIdInterno = protectInject(request.getParameter("idInternoPe"));
        if (strNumTarjeta == null || strEntidad == null || strIdInterno == null)
        {
            throw new ApplicationException(500, 99999, "No permitido", "Palabra reservada ha entrado como parámetro", new Exception());
        }
        String[] strRetorno = new String[2];
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        String strQuery = null;
        String coma = "";
        String strTarjetasEmpresa = "";
        try
        {
            strQuery = " select num_sec_ac \"acuerdo\" from rdwc01.mi_mpa2_tarjetas t " + " where cod_nrbe_en = '"
                    + strEntidad + "' " + " and mi_fecha_fin = to_date('31-12-9999', 'dd-mm-yyyy') "
                    + " and indtaremp = 'S' " + " and id_interno_pe = " + strIdInterno + " and indulttar = 'S' "
                    + " and fecbaja = to_date('11-11-1111', 'dd-mm-yyyy') ";
            pLog.info("Query para extracción acuerdos de tarjeta de empresa a descartar en todos los procesos: "
                    + strQuery);
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleCIP);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pResultSet = pPreparedStatement.executeQuery();
            while (pResultSet.next())
            {
                String strAcuerdo = (String) pResultSet.getString("acuerdo");
                strTarjetasEmpresa = strTarjetasEmpresa + coma + " " + strAcuerdo;
                coma = ",";
            }
            if (coma.equals(","))
            {
                strTarjetasEmpresa = " and to_number(acuerdo) in (" + strTarjetasEmpresa + ") ";
            }
        }
        catch (Exception ex)
        {
            pLog.error("Error al intentar extraer los acuerdos de tarjeta de empresa", ex);
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
        }
        pPreparedStatement = null;
        pResultSet = null;
        strQuery = null;
        try
        {
            strQuery = " select " + "   substr(b.cta_aso,11,20) \"acuerdo\", "
                    + "   trim (b.descr_txt) \"txtproducto\" " + " from bel.belts009 b "
                    + "   where trim(b.tarjeta_cod) = '" + strNumTarjeta + "' " + " union " + " select "
                    + "   acuerdo, " + "   trim (c.descr_txt) \"txtproducto\" " + " from BEL.BDPTB083_TARJETAS_MP c "
                    + " where trim(c.tarjeta_cod) = '" + strNumTarjeta + "' " + strTarjetasEmpresa;
            pLog.info("Query para extracción de alias de banca: " + strQuery);
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pResultSet = pPreparedStatement.executeQuery();
            String strDecodeAliases = "";
            String strListaAcuerdos = "";
            coma = "";
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
            pLog.error("Error al extraer los acuerdos / alias de Rvia", ex);
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
