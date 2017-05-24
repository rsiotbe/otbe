package com.rsi.rvia.rest.simulators;

/**
 * Objeto que representa toda la información y configuración que contiene un simulador
 */
public class SimulatorEmailConfig
{
    private int    nId;
    private String strNRBE;
    private String strNrbeName;
    private String strSimpleName;
    private String strComercialName;
    private String strOfficeTo;
    private String strOfficeClaimTemplate;
    private String strOfficeClaimSubject;
    private String strOfficeClaimFrom;
    private String strOfficeDraftTemplate;
    private String strOfficeDraftSubject;
    private String strOfficeDraftFrom;
    private String strCustomerDraftTemplate;
    private String strCustomerDraftSubject;
    private String strCustomerDraftFrom;
    private String strCustomerSupportTelephone;
    private String strCustomerSupportEmail;

    public String getStrNRBE()
    {
        return strNRBE;
    }

    public String getNRBEName()
    {
        return strNrbeName;
    }

    public String getSimpleName()
    {
        return strSimpleName;
    }

    public String getComercialName()
    {
        return strComercialName;
    }

    public int getId()
    {
        return nId;
    }

    public String getOfficeTo()
    {
        return strOfficeTo;
    }

    public String getOfficeClaimTemplate()
    {
        return strOfficeClaimTemplate;
    }

    public String getOfficeClaimSubject()
    {
        return strOfficeClaimSubject;
    }

    public String getOfficeClaimFrom()
    {
        return strOfficeClaimFrom;
    }

    public String getOfficeDraftTemplate()
    {
        return strOfficeDraftTemplate;
    }

    public String getOfficeDraftSubject()
    {
        return strOfficeDraftSubject;
    }

    public String getOfficeDraftFrom()
    {
        return strOfficeDraftFrom;
    }

    public String getCustomerDraftTemplate()
    {
        return strCustomerDraftTemplate;
    }

    public String getCustomerDraftSubject()
    {
        return strCustomerDraftSubject;
    }

    public String getCustomerDraftFrom()
    {
        return strCustomerDraftFrom;
    }

    public String getCustomerSupportTelephone()
    {
        return strCustomerSupportTelephone;
    }

    public String getCustomerSupportEmail()
    {
        return strCustomerSupportEmail;
    }

    /**
     * @param nId
     *            Id del simulador
     * @param strNRBE
     *            NRBE de la entidad propietaria del simulador
     * @param strNRBEName
     *            Nombre de la entidad propietaria del simulador
     * @param strSimpleName
     *            Nombre simple del simulador
     * @param strComercialName
     *            Nombre comercial del simulador
     * @param strOfficeTo
     *            Direccion de destino del correo a la sucursal
     * @param strOfficeClaimTemplate
     *            Plantilla para generar el email a la sucursal
     * @param strOfficeClaimSubject
     *            Asunto del email dirigido a la sucursal
     * @param strOfficeClaimFrom
     *            Remitente del email dirigido a la sucursal
     * @param strOfficeDraftTemplate
     *            Plantilla para generar el email dirigido a la sucursal con el pdf generado por el cliente
     * @param strOfficeDraftSubject
     *            Asunto del email dirigido a la sucursal con el pdf generado por el cliente
     * @param strOfficeDraftFrom
     *            Remitente del email dirigido a la sucursal con el pdf generado por el cliente
     * @param strCustomerDraftFrom
     *            Remitente del email con el pdf generado por el cliente
     * @param strCustomerDraftSubject
     *            Plantilla para generar el email con el pdf generado por el cliente
     * @param strCustomerDraftTemplate
     *            Remitente del email dirigido con el pdf generado por el cliente
     */
    public SimulatorEmailConfig(int nId, String strNRBE, String strNRBEName, String strSimpleName,
            String strComercialName, String strOfficeTo, String strOfficeClaimTemplate, String strOfficeClaimSubject,
            String strOfficeClaimFrom, String strOfficeDraftTemplate, String strOfficeDraftSubject,
            String strOfficeDraftFrom, String strCustomerDraftTemplate, String strCustomerDraftSubject,
            String strCustomerDraftFrom, String strCustomerSupportTelephone, String strCustomerSupportEmail)
    {
        this.nId = nId;
        this.strNRBE = strNRBE;
        this.strNrbeName = strNRBEName;
        this.strSimpleName = strSimpleName;
        this.strComercialName = strComercialName;
        this.strOfficeTo = strOfficeTo;
        this.strOfficeClaimTemplate = strOfficeClaimTemplate;
        this.strOfficeClaimSubject = strOfficeClaimSubject;
        this.strOfficeClaimFrom = strOfficeClaimFrom;
        this.strOfficeDraftTemplate = strOfficeDraftTemplate;
        this.strOfficeDraftSubject = strOfficeDraftSubject;
        this.strOfficeDraftFrom = strOfficeDraftFrom;
        this.strCustomerDraftTemplate = strCustomerDraftTemplate;
        this.strCustomerDraftSubject = strCustomerDraftSubject;
        this.strCustomerDraftFrom = strCustomerDraftFrom;
        this.strCustomerSupportTelephone = strCustomerSupportTelephone;
        this.strCustomerSupportEmail = strCustomerSupportEmail;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuilder pSb = new StringBuilder();
        pSb.append("Id              : " + nId + "\n");
        pSb.append("NRBE            : " + strNRBE + "\n");
        pSb.append("NrbeName        : " + strNrbeName + "\n");
        pSb.append("ComercialName   : " + strComercialName + "\n");
        pSb.append("OfficeTo        : " + strOfficeTo + "\n");
        pSb.append("strOfficeClaimTemplate     : " + strOfficeClaimTemplate + "\n");
        pSb.append("strOfficeClaimSubject      : " + strOfficeClaimSubject + "\n");
        pSb.append("strOfficeClaimFrom         : " + strOfficeClaimFrom + "\n");
        pSb.append("strOfficeDraftTemplate     : " + strOfficeDraftTemplate + "\n");
        pSb.append("strOfficeDraftSubject      : " + strOfficeDraftSubject + "\n");
        pSb.append("strOfficeDraftFrom         : " + strOfficeDraftFrom + "\n");
        pSb.append("strCustomerDraftTemplate   : " + strCustomerDraftTemplate + "\n");
        pSb.append("strCustomerDraftSubject    : " + strCustomerDraftSubject + "\n");
        pSb.append("strCustomerDraftFrom       : " + strCustomerDraftFrom + "\n");
        pSb.append("strCustomerSupportTelephone: " + strCustomerSupportTelephone + "\n");
        pSb.append("strCustomerSupportEmail    : " + strCustomerSupportEmail + "\n");
        return pSb.toString();
    }
}
