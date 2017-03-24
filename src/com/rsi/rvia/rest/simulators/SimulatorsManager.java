package com.rsi.rvia.rest.simulators;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.Hashtable;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants;
import com.rsi.Constants.SimulatorType;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.error.exceptions.ApplicationException;
import com.rsi.rvia.rest.error.exceptions.LogicalErrorException;
import com.rsi.rvia.rest.tool.Utils;

public class SimulatorsManager
{
    private static Logger     pLog = LoggerFactory.getLogger(SimulatorsManager.class);
    private static JSONObject pPropNRBENames;

    /**
     * Obtiene la información de simuladores de la entidad
     * 
     * @param strSimulatorName
     *            Nombre del simulador especifico que se quiere recuperar, si se pasa null se obtienen todos los
     *            disponibles para la entidad
     * @param strNRBE
     *            Codigo de entidad con 4 digitos
     * @return Datos de la configuración
     * @throws Exception
     */
    public static SimulatorConfigObjectArray getSimulatorsData(String strNRBE, String strNRBEName,
            String strSimulatorType, String strSimulatorName, String strLanguage) throws Exception
    {
        SimulatorConfigObjectArray alReturn = new SimulatorConfigObjectArray();
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        String strQuery;
        int nSimulatorIdRef = -1;
        int nSimulatorId;
        String strCategory;
        String strSimpleName;
        String strComercialName;
        String strCalcType;
        boolean fIsActive;
        boolean fAllowBooking;
        boolean fAllowUserEmail;
        boolean fAllowUserTelephone;
        String strCustomerSupportEmail;
        String strCustomerSupportTelephone;
        String strReceivingOfficeEmail;
        String strDisclaimer;
        String strContractConditions;
        String strLOPD;
        String strDescription;
        SimulatorType pSimulatorType;
        try
        {
            strQuery = "select s.id_simulador, s.entidad, s.categoria, s.nombre_simple, s.nombre_comercial, s.tipo_calculo, s.activo, "
                    + "s.contratar, s.contacto_email, s.contacto_telef, s.atencion_cliente_email, s.atencion_cliente_telef, "
                    + "s.entidad_email_contacto, "
                    + "(select i.traduccion from BDPTB079_IDIOMA i where i.idioma = ? and codigo = s.texto_lopd) as texto_lopd, "
                    + "(select i.traduccion from BDPTB079_IDIOMA i where i.idioma = ? and codigo = s.texto_condiciones) as texto_condiciones, "
                    + "(select i.traduccion from BDPTB079_IDIOMA i where i.idioma = ? and codigo = s.texto_aviso_legal) as texto_aviso_legal, "
                    + "(select i.traduccion from BDPTB079_IDIOMA i where i.idioma = ? and codigo = s.texto_desc) as texto_desc, "
                    + "p.clave, p.valor " + "from BEL.BDPTB235_SIMULADORES s, " + "BEL.BDPTB236_PARAM_SIMULADORES p "
                    + "where s.id_simulador=p.id_simulador " + "and s.entidad = ? " + "and s.activo = '1' ";
            /* se conmpone la condición de categoria con una clausula 'IN' */
            pSimulatorType = SimulatorType.valueOf(strSimulatorType);
            if (pSimulatorType == null)
            {
                pLog.error("El parámetro recibido strSimulatorType: " + strSimulatorType
                        + ", no coincide con un tipo de simulador");
            }
            strQuery += "and s.CATEGORIA" + (pSimulatorType.equals(SimulatorType.MORTGAGE) ? "=" : "!=") + "? ";
            if (strSimulatorName != null && !strSimulatorName.trim().isEmpty()
                    && !"null".equals(strSimulatorName.trim()))
            {
                strQuery += "and s.NOMBRE_SIMPLE = ? ";
            }
            /* se añade la ordenación */
            strQuery += "order by s.ID_SIMULADOR";
            /* se rellena la query con los datos */
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pPreparedStatement.setString(1, strLanguage);
            pPreparedStatement.setString(2, strLanguage);
            pPreparedStatement.setString(3, strLanguage);
            pPreparedStatement.setString(4, strLanguage);
            pPreparedStatement.setString(5, strNRBE);
            pPreparedStatement.setString(6, Constants.SimulatorMortgageCategory.HIPOTECA.name());
            if (strSimulatorName != null && !strSimulatorName.trim().isEmpty()
                    && !"null".equals(strSimulatorName.trim()))
            {
                pPreparedStatement.setString(7, strSimulatorName);
            }
            pResultSet = pPreparedStatement.executeQuery();
            /* Recupera los parametros de configuración */
            SimulatorConfig pSimulatorObject = null;
            while (pResultSet.next())
            {
                nSimulatorId = pResultSet.getInt("ID_SIMULADOR");
                /* se comprueba si el registo pertenece a un simulador no evaluado todavia */
                if (nSimulatorIdRef != nSimulatorId)
                {
                    nSimulatorIdRef = nSimulatorId;
                    /*
                     * si el obtejo simulador esta vacio siginifa que el la primera iteración, si no lo esta es un
                     * objeto anterior y es necsario guardarlo en el arraylist de resultado
                     */
                    if (pSimulatorObject != null)
                    {
                        alReturn.addSimulator(pSimulatorObject);
                    }
                    /* se crea el nuevo sinulador y posteriormente se añaden sus campos */
                    strCategory = pResultSet.getString("CATEGORIA");
                    strSimpleName = pResultSet.getString("NOMBRE_SIMPLE");
                    strComercialName = pResultSet.getString("NOMBRE_COMERCIAL");
                    strCalcType = pResultSet.getString("TIPO_CALCULO");
                    fIsActive = pResultSet.getBoolean("ACTIVO");
                    fAllowBooking = pResultSet.getBoolean("CONTRATAR");
                    strCustomerSupportEmail = pResultSet.getString("ATENCION_CLIENTE_EMAIL");
                    strCustomerSupportTelephone = pResultSet.getString("ATENCION_CLIENTE_TELEF");
                    strReceivingOfficeEmail = pResultSet.getString("ENTIDAD_EMAIL_CONTACTO");
                    fAllowUserEmail = pResultSet.getBoolean("CONTACTO_EMAIL");
                    fAllowUserTelephone = pResultSet.getBoolean("CONTACTO_TELEF");
                    strDisclaimer = pResultSet.getString("TEXTO_AVISO_LEGAL");
                    strContractConditions = pResultSet.getString("TEXTO_CONDICIONES");
                    strLOPD = pResultSet.getString("TEXTO_LOPD");
                    strDescription = pResultSet.getString("TEXTO_DESC");
                    pSimulatorObject = new SimulatorConfig(nSimulatorId, strNRBE, strNRBEName, strCategory,
                            strSimpleName, strComercialName, strCalcType, fIsActive, fAllowBooking, fAllowUserEmail,
                            fAllowUserTelephone, strCustomerSupportEmail, strCustomerSupportTelephone,
                            strReceivingOfficeEmail, strLOPD, strDisclaimer, strContractConditions, strDescription);
                }
                pSimulatorObject.addConfigParam(pResultSet.getString("CLAVE"), pResultSet.getString("VALOR"));
            }
            /* se añade el último elemento si existe al menos uno */
            if (pSimulatorObject != null)
            {
                alReturn.addSimulator(pSimulatorObject);
            }
        }
        catch (Exception ex)
        {
            pLog.error("Error al cargar la configuración de algoritmos para la entidad " + strNRBE + ". ERROR:" + ex);
            throw ex;
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
        }
        return alReturn;
    }

    public static SimulatorEmailConfig getSimulatorEmailConfig(int nSimulatorId, String strNRBE) throws Exception
    {
        SimulatorEmailConfig pReturn = null;
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        String strQuery;
        String strClave;
        int nId = -1;
        String strNRBEName = null;
        String strSimpleName = null;
        String strComercialName = null;
        String strOfficeTo = null;
        String strOfficeTemplate = null;
        String strOfficeSubject = null;
        String strOfficeFrom = null;
        String strCustomerTemplate = null;
        String strCustomerSubject = null;
        String strCustomerFrom = null;
        try
        {
            strQuery = "select s.*, o.NOM_ENT_TXT, e.clave, e.valor  "
                    + "from BEL.BDPTB235_SIMULADORES s, BEL.BELTS002 o, BEL.BDPTB273_SIMULADORES_EMAIL e "
                    + "where s.ENTIDAD = o.NRBE and s.id_simulador=e.id_simulador and s.id_simulador = ? and s.ENTIDAD=?";
            /* se rellena la query con los datos */
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pPreparedStatement.setInt(1, nSimulatorId);
            pPreparedStatement.setString(2, strNRBE);
            pResultSet = pPreparedStatement.executeQuery();
            while (pResultSet.next())
            {
                if (nId == -1)
                    nId = pResultSet.getInt("ID_SIMULADOR");
                if (strNRBEName == null)
                    strNRBEName = pResultSet.getString("NOM_ENT_TXT");
                if (strSimpleName == null)
                    strSimpleName = pResultSet.getString("NOMBRE_SIMPLE");
                if (strComercialName == null)
                    strComercialName = pResultSet.getString("NOMBRE_COMERCIAL");
                if (strOfficeTo == null)
                    strOfficeTo = pResultSet.getString("entidad_email_contacto");
                strClave = pResultSet.getString("CLAVE");
                switch (strClave)
                {
                    case Constants.SIMULADOR_EMAIL_CONFIG_OFFICE_TEMPLATE:
                        strOfficeTemplate = pResultSet.getString("VALOR");
                        break;
                    case Constants.SIMULADOR_EMAIL_CONFIG_OFFICE_SUBJECT:
                        strOfficeSubject = pResultSet.getString("VALOR");
                        break;
                    case Constants.SIMULADOR_EMAIL_CONFIG_OFFICE_FROM:
                        strOfficeFrom = pResultSet.getString("VALOR");
                        break;
                    case Constants.SIMULADOR_EMAIL_CONFIG_CUSTOMER_TEMPLATE:
                        strCustomerTemplate = pResultSet.getString("VALOR");
                        break;
                    case Constants.SIMULADOR_EMAIL_CONFIG_CUSTOMER_SUBJECT:
                        strCustomerSubject = pResultSet.getString("VALOR");
                        break;
                    case Constants.SIMULADOR_EMAIL_CONFIG_CUSTOMER_FROM:
                        strCustomerFrom = pResultSet.getString("VALOR");
                        break;
                    default:
                        pLog.warn("Se ha leido una propiedad que no está censada para el envio de email. Porpiedad: "
                                + strClave);
                        break;
                }
            }
            pReturn = new SimulatorEmailConfig(nId, strNRBE, strNRBEName, strSimpleName, strComercialName, strOfficeTo,
                    strOfficeTemplate, strOfficeSubject, strOfficeFrom, strCustomerTemplate, strCustomerSubject,
                    strCustomerFrom);
            /*
             * if (pReturn == null) throw new
             * Exception("No se ha encontrado una configuración valida para el simulador con id " + nSimulatorId +
             * " y la entidad " + strNRBE);
             */
        }
        catch (Exception ex)
        {
            pLog.error("Error al cargar la configuración de email para el simulador " + nSimulatorId + ". ERROR:" + ex);
            throw ex;
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
        }
        return pReturn;
    }

    /**
     * Obtiene el código de entidad a partir de su nombre comercial
     * 
     * @param strNRBEName
     *            Nombre comercial de la entidad
     * @return Numero NRBE de la entidad
     * @throws Exception
     */
    public static String getNRBEFromName(String strNRBEName) throws Exception
    {
        String strReturn;
        /* se carga el fichero de propiedades que contien la resolución de nombres */
        if (pPropNRBENames == null)
        {
            String strJsonContent;
            try
            {
                InputStream pInputStream = (SimulatorsManager.class.getResourceAsStream("/NRBE.properties"));
                strJsonContent = Utils.getStringFromInputStream(pInputStream);
                pPropNRBENames = new JSONObject(strJsonContent);
            }
            catch (Exception ex)
            {
                throw new ApplicationException(500, 9999, "Se ha producido un error interno de la aplicación",
                        "No ha sido posible recuperar la información necesaria para la entidad", ex);
            }
        }
        /* se obtiene el codigo de entidad para el nombre dado */
        JSONObject pDataObject = pPropNRBENames.getJSONObject(strNRBEName);
        if (pDataObject == null)
        {
            throw new LogicalErrorException(400, 9998, "No se ha encontrado información para esta entidad",
                    "No ha sido posible recuperar la información necesaria para esta entidad", null);
        }
        strReturn = pDataObject.getString("NRBE");
        if (strReturn == null || strReturn.trim().isEmpty())
        {
            throw new LogicalErrorException(400, 9997, "No se ha encontrado información para esta entidad",
                    "No ha sido posible recuperar la información necesaria para esta entidad", null);
        }
        while (strReturn.length() < 4)
            strReturn = "0" + strReturn;
        return strReturn;
    }

    public static String getEmailTemplate(String strTemplate) throws Exception
    {
        String strReturn;
        InputStream pInputStream = (SimulatorsManager.class.getResourceAsStream(strTemplate));
        if (pInputStream == null)
        {
            pLog.error("No se encuentra el fichero template " + strTemplate);
        }
        strReturn = Utils.getStringFromInputStream(pInputStream);
        strReturn = strReturn.replace("", "");
        return strReturn;
    }

    public static String proccessEmailTemplate(String strTemplate, Hashtable<String, String> htKeyValues)
    {
        String strRetun = strTemplate;
        Enumeration<String> pEnumKeyValues = htKeyValues.keys();
        while (pEnumKeyValues.hasMoreElements())
        {
            String strHtKey = (String) pEnumKeyValues.nextElement();
            String strHtValue = (String) htKeyValues.get(strHtKey);
            strRetun = strRetun.replaceAll("<%" + strHtKey + "%>", strHtValue.replaceAll("\n", "<br/>"));
        }
        return strRetun;
    }
}
