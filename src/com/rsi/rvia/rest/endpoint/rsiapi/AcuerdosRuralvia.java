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
import com.rsi.rvia.rest.tool.AppConfiguration;

public class AcuerdosRuralvia
{
    private static Logger   pLog               = LoggerFactory.getLogger(QueryCustomizer.class);
    private static String[] _reserved          = { "select", "update", "delete", "insert", "alter", "drop", "create" };
    private static String   _strClopsExcluidos = "'070002', '070001', '410003', '410001'";
    private static String   _strClopsAlDebe    = ",'060011'";
    // private static String _strClopsAlHaber = ",'060001'"; // Dejamos comentado hasta identificar los traspasos.
    private static String   _strClopsAlHaber   = "";

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
                strTarjetasEmpresa = " and to_number(acuerdo) not in (" + strTarjetasEmpresa + ") ";
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
        String strComoPrimerTitular = "";
        String strComoPrimerTitularF1 = "";
        String strComoPrimerTitularF2 = "";
        try
        {
            strQuery = " select num_sec_ac \"acuerdo\" from rdwc01.mi_clte_rl_ac t1 "
                    + " WHERE t1.MI_FECHA_FIN = to_date('31.12.9999','dd.mm.yyyy') " + " AND t1.COD_NRBE_EN = '"
                    + strEntidad + "' " + " and id_interno_pe = " + strIdInterno + " AND t1.COD_RL_PERS_AC = '01' "
                    + " AND t1.NUM_RL_ORDEN = 1 " + " and t1.fecha_crre=to_date('99991231','yyyymmdd') ";
            pLog.info("Query para extracción acuerdos como primer titular: " + strQuery);
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleCIP);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pResultSet = pPreparedStatement.executeQuery();
            coma = "";
            while (pResultSet.next())
            {
                String strAcuerdo = (String) pResultSet.getString("acuerdo");
                strComoPrimerTitular = strComoPrimerTitular + coma + " " + strAcuerdo;
                coma = ",";
            }
            if (coma.equals(","))
            {
                strComoPrimerTitularF1 = " and to_number(substr(b.cta_aso,11,20)) in (" + strComoPrimerTitular + ") ";
                strComoPrimerTitularF2 = " and to_number(acuerdo) in (" + strComoPrimerTitular + ") ";
            }
        }
        catch (Exception ex)
        {
            pLog.error("Error al intentar extraer los acuerdos como primer titular", ex);
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
                    + "   trim (b.descr_txt) \"txtproducto\" " + " from "
                    + AppConfiguration.getInstance().getProperty("BELScheme").trim() + ".belts009 b "
                    + "   where trim(b.tarjeta_cod) = '" + strNumTarjeta + "' " + strComoPrimerTitularF1 + " union "
                    + " select " + "   acuerdo, " + "   trim (c.descr_txt) \"txtproducto\" " + " from "
                    + AppConfiguration.getInstance().getProperty("BELScheme").trim() + ".BDPTB083_TARJETAS_MP c "
                    + " where trim(c.tarjeta_cod) = '" + strNumTarjeta + "' " + strTarjetasEmpresa
                    + strComoPrimerTitularF2;
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

    public static String getLastProcessDateMasUno(String pTabla) throws ApplicationException
    {
        if (pTabla == null)
        {
            throw new ApplicationException(500, 99999, "No permitido", "Palabra reservada ha entrado como parámetro", new Exception());
        }
        String strRetorno = null;
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        String strQuery = null;
        try
        {
            strQuery = " select to_char(mi_fecha_oprcn + 1,'yyyy-mm-dd') \"fecha\" from rdwc01.ce_carga_tabla"
                    + " where nomtabla = upper('" + pTabla + "') " + " and mi_periodicidad='D' ";
            pLog.info("Query para extracción de fecha de cargam para tabla recibida como parámetro: " + strQuery);
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleCIP);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pResultSet = pPreparedStatement.executeQuery();
            while (pResultSet.next())
            {
                strRetorno = (String) pResultSet.getString("fecha");
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
                break;
            }
        }
        return strFields;
    }

    public static String getExcludedClops()
    {
        return _strClopsExcluidos;
    }

    public static String getExcludedClopsAlDebe()
    {
        return _strClopsAlDebe;
    }

    public static String getExcludedClopsAlHaber()
    {
        return _strClopsAlHaber;
    }
}
