package com.rsi;

/**
 * Constantes usadas en toda la aplicación.
 * 
 * @author RSI
 */
public class Constants
{
    public enum Languages
    {
        ca_ES, de_DE, en_UK, es_ES, eu_ES, fr_FR, gl_ES, it_IT, pt_PT, va_ES
    }

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
    // Campos JSON traducciones
    // /////////////////////////
    public static final String TRANSLATE_APPNAME                        = "appName";
    public static final String TRANSLATE_LANG                           = "lang";
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
    public static final String TARGET_MOCK_DIRECTORY                    = "targetMockRootDir";
    public static final String TEMPLATE_BY_HTTP                         = "templateByHttp";
    public static final String TEMPLATE_URL                             = "templateUrlHostToStaticRepository";
    public static final String TEMPLATE_PATH_DISK                       = "templatePathDiskToStaticRepository";
    // /////////////////////////
    // Errors
    // /////////////////////////
    public static final String ERROR_SIGN_BLOCKED                       = "808";
    public static final String ERROR_EMPTY_LIST                         = "779";
    // /////////////////////////
    // JSON keys
    // /////////////////////////
    public static final String KEY_ERROR_CODE                           = "CODERR";
    public static final String KEY_ERROR_TEXT                           = "TXTERR";
    public static final String KEY_LIST_NAME                            = "NOMBRELISTACUENTA";

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
