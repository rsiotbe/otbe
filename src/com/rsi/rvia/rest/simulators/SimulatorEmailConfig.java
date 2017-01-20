package com.rsi.rvia.rest.simulators;

/**
 * Objeto que representa toda la información y configuración que contiene un simulador
 */
public class SimulatorEmailConfig
{
	private int		nId;
	private String	strNRBE;
	private String	strNrbeName;
	private String	strSimpleName;
	private String	strComercialName;
	private String	strOfficeTo;
	private String	strOfficeTemplate;
	private String	strOfficeSubject;
	private String	strOfficeFrom;
	private String	strCustomerTemplate;
	private String	strCustomerSubject;
	private String	strCustomerFrom;

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

	public String getOfficeTemplate()
	{
		return strOfficeTemplate;
	}

	public String getOfficeSubject()
	{
		return strOfficeSubject;
	}

	public String getOfficeFrom()
	{
		return strOfficeFrom;
	}

	public String getCustomerTemplate()
	{
		return strCustomerTemplate;
	}

	public String getCustomerSubject()
	{
		return strCustomerSubject;
	}

	public String getCustomerFrom()
	{
		return strCustomerFrom;
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
	 * @param strOfficeTemplate
	 *            Plantilla para generar el email a la sucursal
	 * @param strOfficeSubject
	 *            Asunto del email dirigido a la sucursal
	 * @param strOfficeFrom
	 *            Remitente del email dirigido a la sucursal
	 * @param strCustomerTemplate
	 *            Plantilla para generar el email al cliente
	 * @param strCustomerSubject
	 *            Asunto del email dirigido al cliente
	 * @param strCustomerFrom
	 *            Remitente del email dirigido al cliente
	 */
	public SimulatorEmailConfig(int nId, String strNRBE, String strNRBEName, String strSimpleName,
			String strComercialName, String strOfficeTo, String strOfficeTemplate, String strOfficeSubject,
			String strOfficeFrom, String strCustomerTemplate, String strCustomerSubject, String strCustomerFrom)
	{
		this.nId = nId;
		this.strNRBE = strNRBE;
		this.strNrbeName = strNRBEName;
		this.strSimpleName = strSimpleName;
		this.strComercialName = strComercialName;
		this.strOfficeTo = strOfficeTo;
		this.strOfficeTemplate = strOfficeTemplate;
		this.strOfficeSubject = strOfficeSubject;
		this.strOfficeFrom = strOfficeFrom;
		this.strCustomerTemplate = strCustomerTemplate;
		this.strCustomerSubject = strCustomerSubject;
		this.strCustomerFrom = strCustomerFrom;
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
		pSb.append("NrbeName        : " + strNrbeName + "\n");
		pSb.append("ComercialName   : " + strComercialName + "\n");
		pSb.append("OfficeTo        : " + strOfficeTo + "\n");
		pSb.append("OfficeTemplate  : " + strOfficeTemplate + "\n");
		pSb.append("OfficeSubject   : " + strOfficeSubject + "\n");
		pSb.append("OfficeFrom      : " + strOfficeFrom + "\n");
		pSb.append("CustomerTemplate: " + strCustomerTemplate + "\n");
		pSb.append("CustomerSubject : " + strCustomerSubject + "\n");
		pSb.append("CustomerFrom    : " + strCustomerFrom + "\n");
		return pSb.toString();
	}
}
