package com.rsi.rvia.rest.operation;

import java.net.URI;
import javax.ws.rs.core.UriBuilder;

/** Objeto que representa una operativa o operación definida en la aplicación */
public class MiqQuests
{
	private int		nIdMiq;
	private String	strComponentType;
	private String	strEndPoint;
	private String	strTemplate;

	public int getIdMiq()
	{
		return nIdMiq;
	}

	public void setIdMiq(int nIdMiq)
	{
		this.nIdMiq = nIdMiq;
	}

	public String getComponentType()
	{
		return strComponentType;
	}

	public void setComponentType(String strComponentType)
	{
		this.strComponentType = strComponentType;
	}

	public String getEndPoint()
	{
		return strEndPoint;
	}

	public void setEndPoint(String strEndPoint)
	{
		this.strEndPoint = strEndPoint;
	}

	public String getTemplate()
	{
		return strTemplate;
	}

	public void setTemplate(String strTemplate)
	{
		this.strTemplate = strTemplate;
	}
	
	/** Obtiene un objeto URI a del valor de EndPoint
	 * 
	 * @param strEndPoint
	 *           String que contiene la uri
	 * @return Objeto URI */
	public URI getBaseWSEndPoint()
	{
		return UriBuilder.fromUri(this.strEndPoint).build();
	}

	/** Contructor generico */
	public MiqQuests()
	{
	}

	/** Contructor con parámetros
	 * 
	 * @param nIdMiq
	 *           Identificador de operación
	 * @param strComponentType
	 *           Tipo de componente
	 * @param strEndPoint
	 *           Dirección endPoint
	 * @param strTemplate
	 *           Plantilla asociada */
	public MiqQuests(int nIdMiq, String strComponentType, String strEndPoint, String strTemplate)
	{
		this.nIdMiq = nIdMiq;
		this.strComponentType = strComponentType;
		this.strEndPoint = strEndPoint;
		this.strTemplate = strTemplate;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuilder pSb = new StringBuilder();
		pSb.append("IdMiq         :" + nIdMiq + "\n");
		pSb.append("ComponentType :" + strComponentType + "\n");
		pSb.append("EndPoint      :" + strEndPoint + "\n");
		pSb.append("Template      :" + strTemplate + "\n");
		return pSb.toString();
	}
}
