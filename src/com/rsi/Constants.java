package com.rsi;

/**
 * Constantes usadas en toda la aplicación.
 * 
 * @author RSI
 */
public class Constants
{
    public enum Language
    {
        ca_ES, de_DE, en_UK, es_ES, eu_ES, fr_FR, gl_ES, it_IT, pt_PT, va_ES;
        public String getJavaCode()
        {
            return this.name();
        }

        public String getWindowsCode()
        {
            return this.name().replace("_", "-");
        }

        /**
         * Devuelve el tipo de la enumeración independientemente el valor Java o Windows que se le pase
         * 
         * @param strValue
         *            Varlo a evaluar
         * @return Valor de la enumración encontrado
         */
        public static Language getEnumValue(String strValue)
        {
            for (Language v : values())
            {
                if (v.getJavaCode().equalsIgnoreCase(strValue) || v.getWindowsCode().equalsIgnoreCase(strValue))
                    return v;
            }
            throw new IllegalArgumentException();
        }
    }

    // /////////////////////////
    // Default Values
    // /////////////////////////
    public static final String   CODIGO_BANCO_COOPERATIVO_ESPANOL               = "0198";
    public static final String   CODIGO_ENTIDAD_FORMACION                       = "9997";
    public static final Language DEFAULT_LANGUAGE                               = Language.es_ES;
    // /////////////////////////
    // Campos JSON Simulador
    // /////////////////////////
    public static final String   SIMULADOR_ID                                   = "id";
    public static final String   SIMULADOR_NRBE                                 = "nrbe";
    public static final String   SIMULADOR_NRBE_NAME                            = "nrbeName";
    public static final String   SIMULADOR_SIMPLE_NAME                          = "simpleName";
    public static final String   SIMULADOR_COMERCIAL_NAME                       = "comercialName";
    public static final String   SIMULADOR_SAC_EMAIL                            = "sacEmail";
    public static final String   SIMULADOR_SAC_TELEPHONE                        = "sacTelephone";
    public static final String   SIMULADOR_OFICINA_EMAIL                        = "oficinaEmail";
    public static final String   SIMULADOR_CATEGORY                             = "category";
    public static final String   SIMULADOR_CALC_TYPE                            = "calcType";
    public static final String   SIMULADOR_ACTIVE                               = "fIsActive";
    public static final String   SIMULADOR_ALLOW_BOOKING                        = "allowBooking";
    public static final String   SIMULADOR_ALLOW_USER_EMAIL                     = "allowUserEmail";
    public static final String   SIMULADOR_ALLOW_USER_TELEPHONE                 = "allowUserTelephone";
    public static final String   SIMULADOR_LOPD                                 = "lopd";
    public static final String   SIMULADOR_DISCLAIMER                           = "disclaimer";
    public static final String   SIMULADOR_CONTRACT_CONDITIONS                  = "contractConditions";
    public static final String   SIMULADOR_DESCRIPTION                          = "description";
    public static final String   SIMULADOR_LANGUAGE                             = "language";
    public static final String   SIMULADOR_TYPE                                 = "simulatorType";
    public static final String   SIMULADOR_DOWNLOAD_BY_FORM                     = "downloadByForm";
    // /////////////////////////
    // Campos JSON GNERACION PDF
    // /////////////////////////
    public static final String   SIMULADOR_PDF_CONF                             = "pdf";
    // /////////////////////////
    // Campos JSON Envio Email Simulador
    // /////////////////////////
    public static final String   SIMULADOR_EMAIL_USER_NAME                      = "usuarioNombre";
    public static final String   SIMULADOR_EMAIL_USER_EMAIL                     = "usuarioEmail";
    public static final String   SIMULADOR_EMAIL_USER_TELEFONO                  = "usuarioTelefono";
    public static final String   SIMULADOR_EMAIL_USER_COMMENT                   = "usuarioComentario";
    public static final String   SIMULADOR_EMAIL_USER_NIF                       = "usuarioNif";
    public static final String   SIMULADOR_EMAIL_USER_IS_CUSTOMER               = "usuarioEsCliente";
    // /////////////////////////
    // Campos JSON traducciones
    // /////////////////////////
    public static final String   TRANSLATE_APPNAME                              = "appName";
    public static final String   TRANSLATE_LANG                                 = "lang";
    // /////////////////////////
    // Campos utilizados para parsear la configuración de email desde BBDD
    // /////////////////////////
    public static final String   SIMULADOR_EMAIL_CONFIG_OFFICE_CLAIM_TEMPLATE   = "solicitud_sucursal_plantilla";
    public static final String   SIMULADOR_EMAIL_CONFIG_OFFICE_CLAIM_SUBJECT    = "solicitud_sucursal_asunto";
    public static final String   SIMULADOR_EMAIL_CONFIG_OFFICE_CLAIM_FROM       = "solicitud_sucursal_remitente";
    public static final String   SIMULADOR_EMAIL_CONFIG_OFFICE_DRAFT_TEMPLATE   = "descarga_sucursal_plantilla";
    public static final String   SIMULADOR_EMAIL_CONFIG_OFFICE_DRAFT_SUBJECT    = "descarga_sucursal_asunto";
    public static final String   SIMULADOR_EMAIL_CONFIG_OFFICE_DRAFT_FROM       = "descarga_sucursal_remitente";
    public static final String   SIMULADOR_EMAIL_CONFIG_CUSTOMER_DRAFT_TEMPLATE = "descarga_cliente_plantilla";
    public static final String   SIMULADOR_EMAIL_CONFIG_CUSTOMER_DRAFT_SUBJECT  = "descarga_cliente_asunto";
    public static final String   SIMULADOR_EMAIL_CONFIG_CUSTOMER_DRAFT_FROM     = "descarga_cliente_remitente";
    // Request params
    // /////////////////////////
    public static final String   PARAM_LANG                                     = "lang";
    public static final String   PARAM_NRBE                                     = "NRBE";
    public static final String   PARAM_CARD_ID                                  = "cardId";
    public static final String   PARAM_OPTIONS                                  = "options";
    public static final String   PARAM_SERVICE                                  = "service";
    public static final String   PARAM_ID_MIQ                                   = "idMiq";
    // /////////////////////////
    // General
    // /////////////////////////
    public static final String   ENVIRONMENT                                    = "env";
    public static final String   TARGET_MOCK_DIRECTORY                          = "targetMockRootDir";
    public static final String   TEMPLATE_BY_HTTP                               = "templateByHttp";
    public static final String   TEMPLATE_URL                                   = "templateUrlHostToStaticRepository";
    public static final String   TEMPLATE_PATH_DISK                             = "templatePathDiskToStaticRepository";
    // /////////////////////////
    // Errors
    // /////////////////////////
    public static final String   ERROR_SIGN_BLOCKED                             = "808";
    public static final String   ERROR_EMPTY_LIST                               = "779";
    // /////////////////////////
    // JSON keys
    // /////////////////////////
    public static final String   KEY_ERROR_CODE                                 = "CODERR";
    public static final String   KEY_ERROR_TEXT                                 = "TXTERR";
    public static final String   KEY_LIST_NAME                                  = "NOMBRELISTACUENTA";
    // //////////////////////////
    // Parámetros cabeceras de llamadas al bus
    // /////////////////////////
    public static final String   BUS_HEADER_COD_SEC_ENT                         = "CODSecEnt";
    public static final String   BUS_HEADER_COD_SEC_TRANS                       = "CODSecTrans";
    public static final String   BUS_HEADER_COD_SEC_USER                        = "CODSecUser";
    public static final String   BUS_HEADER_COD_APL                             = "CODApl";
    public static final String   BUS_HEADER_COD_TERMINAL                        = "CODTerminal";
    public static final String   BUS_HEADER_COD_CANAL                           = "CODCanal";
    public static final String   BUS_HEADER_COD_SEC_IP                          = "CODSecIp";
    // //////////////////////////
    // Cabeceras http
    // /////////////////////////
    public static final String   HTTP_HEADER_AUTORIZATION                       = "Authorization";
    public static final String   HTTP_HEADER_MEDIATYPE_PDF                      = "application/pdf";
    public static final String   UTF8                                           = "UTF-8";

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
        PERSONAL, MORTGAGE, CARD
    }

    public enum Environment
    {
        DESA, TEST, PREP, PROD
    }

    /**
     * Enumeración de canal aix recibido desde la parte front de ruralvia, en ruralvia se denomina canalAix
     */
    public enum CanalFront
    {
        WEB(1), TABLET(6), MOVIL(7);
        private final int value;

        CanalFront(int newValue)
        {
            value = newValue;
        }

        public int getValue()
        {
            return value;
        }
    }

    /**
     * Enumeración de canal host recibido desde ruralvia, en ruralvia se denomina canal
     */
    public enum CanalHost
    {
        VALORES_BANCA_INTERNET(1),
        VALORES_BANCA_TELEFONICA(2),
        BANCA_INTERNET(3),
        BANCA_TELEFONICA(4),
        ABOGADOS(5),
        ABOGADOS_TELEFONICA(6),
        TPV_VIRTUAL(7),
        SEGUROS(8),
        OFICINA(9),
        TPV_VIRTUAL_TELEFONICA(10),
        BANCA_MOVIL(11),
        BANCA_TABLET(13),
        BANCA_TABLET_CAU(14);
        private final int value;

        CanalHost(int newValue)
        {
            value = newValue;
        }

        public int getValue()
        {
            return value;
        }
    }
}
