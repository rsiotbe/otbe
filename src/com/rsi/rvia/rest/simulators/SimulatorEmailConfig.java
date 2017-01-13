package com.rsi.rvia.rest.simulators;

/**
 * Objeto que representa toda la información y configuración que contiene un simulador
 */
public class SimulatorEmailConfig
{
	private int		nId;
	private String	strOfficeTemplate;
	private String	strOfficeSubject;
	private String	strOfficeFrom;
	private String	strCustomerTemplate;
	private String	strCustomerSubject;
	private String	strCustomerFrom;

	/**
	 * Constructor
	 * 
	 * @param nId
	 *           Id del simulador
	 * @param strOfficeTemplate
	 *           Plantilla para generar el email a la sucursal
	 * @param strOfficeSubject
	 *           Asunto del email dirigido a la sucursal
	 * @param strOfficeFrom
	 *           Remitente del email dirigido a la sucursal
	 * @param strCustomerTemplate
	 *           Plantilla para generar el email al cliente
	 * @param strCustomerSubject
	 *           Asunto del email dirigido al cliente
	 * @param strCustomerFrom
	 *           Remitente del email dirigido al cliente
	 */
	public SimulatorEmailConfig(int nId, String strOfficeTemplate, String strOfficeSubject, String strOfficeFrom,
			String strCustomerTemplate, String strCustomerSubject, String strCustomerFrom)
	{
		this.nId = nId;
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
		pSb.append("Id                 : " + nId + "\n");
		pSb.append("strOfficeTemplate  : " + strOfficeTemplate + "\n");
		pSb.append("strOfficeSubject   : " + strOfficeSubject + "\n");
		pSb.append("strOfficeFrom      : " + strOfficeFrom + "\n");
		pSb.append("strCustomerTemplate: " + strCustomerTemplate + "\n");
		pSb.append("strCustomerSubject : " + strCustomerSubject + "\n");
		pSb.append("strCustomerFrom    : " + strCustomerFrom + "\n");
		return pSb.toString();
	}
}
