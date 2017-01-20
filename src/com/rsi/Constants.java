package com.rsi;

/**
 * Constantes usadas en toda la aplicación.
 * 
 * @author RSI
 */
public class Constants
{
    // /////////////////////////
    // Default Values
    // /////////////////////////
    public static final String CODIGO_BANCO_COOPERATIVO_ESPANOL         = "0198";
    public static final String DEFAULT_LANGUAGE                         = "es_ES";
    // /////////////////////////
    // Campos JSON Simulador
    // /////////////////////////
    public static final String SIMULADOR_ID                             = "id";
    public static final String SIMULADOR_NRBE                           = "nrbe";
    public static final String SIMULADOR_NRBE_NAME                      = "nrbeName";
    public static final String SIMULADOR_SIMPLE_NAME                    = "simpleName";
    public static final String SIMULADOR_COMERCIAL_NAME                 = "comercialName";
    public static final String SIMULADOR_SAC_EMAIL                      = "sacEmail";
    public static final String SIMULADOR_SAC_TELEPHONE                  = "sacTelephone";
    public static final String SIMULADOR_OFICINA_EMAIL                  = "oficinaEmail";
    public static final String SIMULADOR_CATEGORY                       = "category";
    public static final String SIMULADOR_CALC_TYPE                      = "calcType";
    public static final String SIMULADOR_ACTIVE                         = "fIsActive";
    public static final String SIMULADOR_ALLOW_BOOKING                  = "allowBooking";
    public static final String SIMULADOR_ALLOW_USER_EMAIL               = "allowUserEmail";
    public static final String SIMULADOR_ALLOW_USER_TELEPHONE           = "allowUserTelephone";
    public static final String SIMULADOR_LOPD                           = "lopd";
    public static final String SIMULADOR_DISCLAIMER                     = "disclaimer";
    public static final String SIMULADOR_CONTRACT_CONDITIONS            = "contractConditions";
    public static final String SIMULADOR_DESCRIPTION                    = "description";
    public static final String SIMULADOR_LANGUAGE                       = "language";
    public static final String SIMULADOR_TYPE                           = "simulatorType";
    // /////////////////////////
    // Campos JSON GNERACION PDF
    // /////////////////////////
    public static final String SIMULADOR_PDF_CONF                       = "pdf";
    // /////////////////////////
    // Campos JSON Envio Email Simulador
    // /////////////////////////
    public static final String SIMULADOR_EMAIL_USER_NAME                = "usuarioNombre";
    public static final String SIMULADOR_EMAIL_USER_EMAIL               = "usuarioEmail";
    public static final String SIMULADOR_EMAIL_USER_TELEFONO            = "usuarioTelefono";
    public static final String SIMULADOR_EMAIL_USER_COMMENT             = "usuarioComentario";
    public static final String SIMULADOR_EMAIL_USER_NIF                 = "usuarioNif";
    public static final String SIMULADOR_EMAIL_USER_IS_CUSTOMER         = "usuarioEsCliente";
    // /////////////////////////
    // Campos utilizados para parsear la configuración de email desde BBDD
    // /////////////////////////
    public static final String SIMULADOR_EMAIL_CONFIG_OFFICE_TEMPLATE   = "sucursal_plantilla";
    public static final String SIMULADOR_EMAIL_CONFIG_OFFICE_SUBJECT    = "sucursal_asunto";
    public static final String SIMULADOR_EMAIL_CONFIG_OFFICE_FROM       = "sucursal_remitente";
    public static final String SIMULADOR_EMAIL_CONFIG_CUSTOMER_TEMPLATE = "cliente_plantilla";
    public static final String SIMULADOR_EMAIL_CONFIG_CUSTOMER_SUBJECT  = "cliente_asunto";
    public static final String SIMULADOR_EMAIL_CONFIG_CUSTOMER_FROM     = "cliente_remitente";
    // Request params
    // /////////////////////////
    public static final String PARAM_LANG                               = "lang";
    public static final String PARAM_NRBE                               = "NRBE";
    // /////////////////////////
    // General
    // /////////////////////////
    public static final String ENVIRONMENT                              = "env";

    public enum SimulatorLoanCategory
    {
        AGRICOLA, AUTOMOVIL, ESTUDIOS, FINANZAS, VIVIENDA, JOVEN, PERSONAL, SANIDAD
    }

    public enum SimulatorMortgageCategory
    {
        HIPOTECA
    }

    public enum SimulatorType
    {
        PERSONAL, MORTGAGE
    }

    public enum Environment
    {
        DESA, TEST, PREP, PROD
    }
}
