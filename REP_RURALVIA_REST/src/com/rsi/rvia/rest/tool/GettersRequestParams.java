package com.rsi.rvia.rest.tool;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

/** Clase destinada getters de parametos en el request. Busca un request determinado por funcion. */
public class GettersRequestParams
{
	/** Busca dentro del pRequest con diferentes convinaciones de letras y palabras el parametro CODSecEnt. Si no lo
	 * encuentra devuelve uno por defecto.
	 * 
	 * @param pRequest
	 * @return String con parametro o el parametro por defecto. */
	public static String getCODSecEnt(HttpServletRequest pRequest)
	{
		String strReturn = null;
		List<String> listPosibilities = new ArrayList();
		listPosibilities.add("codEntidad");
		listPosibilities.add("codigoEntidad");
		listPosibilities.add("CodigoEntidad");
		listPosibilities.add("CODSecEnt");
		listPosibilities.add("codigo_entidad");
		listPosibilities.add("Codigo_Entidad");
		listPosibilities.add("Codigo_entidad");
		listPosibilities.add("CodEntidad");
		for (String strPosibility : listPosibilities)
		{
			String strValue = (String) pRequest.getParameter(strPosibility);
			if (strValue != null)
			{
				strReturn = strValue;
				break;
			}
		}
		if (strReturn == null)
		{
			strReturn = "3008";
		}
		return strReturn;
	}

	/** Busca dentro del pRequest con diferentes convinaciones de letras y palabras el parametro CODSecUser. Si no lo
	 * encuentra devuelve uno por defecto.
	 * 
	 * @param pRequest
	 * @return String con parametro o el parametro por defecto. */
	public static String getCODSecUser(HttpServletRequest pRequest)
	{
		String strReturn = null;
		List<String> listPosibilities = new ArrayList();
		listPosibilities.add("CODSecUser");
		listPosibilities.add("codigoUsuario");
		listPosibilities.add("userCode");
		listPosibilities.add("codUser");
		listPosibilities.add("SecCode");
		for (String strPosibility : listPosibilities)
		{
			String strValue = (String) pRequest.getParameter(strPosibility);
			if (strValue != null)
			{
				strReturn = strValue;
				break;
			}
		}
		if (strReturn == null)
		{
			strReturn = "";
		}
		return strReturn;
	}

	/** Busca dentro del pRequest con diferentes convinaciones de letras y palabras el parametro CODSecTrans. Si no lo
	 * encuentra devuelve uno por defecto.
	 * 
	 * @param pRequest
	 * @return String con parametro o el parametro por defecto. */
	public static String getCODSecTrans(HttpServletRequest pRequest)
	{
		String strReturn = null;
		List<String> listPosibilities = new ArrayList();
		listPosibilities.add("CODSecTrans");
		listPosibilities.add("codigoTransaccion");
		listPosibilities.add("CodTransaccion");
		listPosibilities.add("TransactionCode");
		listPosibilities.add("codigoTransaction");
		for (String strPosibility : listPosibilities)
		{
			String strValue = (String) pRequest.getParameter(strPosibility);
			if (strValue != null)
			{
				strReturn = strValue;
				break;
			}
		}
		if (strReturn == null)
		{
			strReturn = "";
		}
		return strReturn;
	}

	/** Busca dentro del pRequest con diferentes convinaciones de letras y palabras el parametro CODTerminal. Si no lo
	 * encuentra devuelve uno por defecto.
	 * 
	 * @param pRequest
	 * @return String con parametro o el parametro por defecto. */
	public static String getCODTerminal(HttpServletRequest pRequest)
	{
		String strReturn = null;
		List<String> listPosibilities = new ArrayList();
		listPosibilities.add("CODTerminal");
		listPosibilities.add("codigoTerminal");
		listPosibilities.add("codTerminal");
		listPosibilities.add("TerminalCode");
		listPosibilities.add("codigo_Terminal");
		for (String strPosibility : listPosibilities)
		{
			String strValue = (String) pRequest.getParameter(strPosibility);
			if (strValue != null)
			{
				strReturn = strValue;
				break;
			}
		}
		if (strReturn == null)
		{
			strReturn = "18";
		}
		return strReturn;
	}

	/** Busca dentro del pRequest con diferentes convinaciones de letras y palabras el parametro CODApl. Si no lo
	 * encuentra devuelve uno por defecto.
	 * 
	 * @param pRequest
	 * @return String con parametro o el parametro por defecto. */
	public static String getCODApl(HttpServletRequest pRequest)
	{
		String strReturn = null;
		List<String> listPosibilities = new ArrayList();
		listPosibilities.add("CODApl");
		listPosibilities.add("codigoApl");
		listPosibilities.add("CodigoAplicacion");
		listPosibilities.add("codApl");
		for (String strPosibility : listPosibilities)
		{
			String strValue = (String) pRequest.getParameter(strPosibility);
			if (strValue != null)
			{
				strReturn = strValue;
				break;
			}
		}
		if (strReturn == null)
		{
			strReturn = "BDP";
		}
		return strReturn;
	}

	/** Busca dentro del pRequest con diferentes convinaciones de letras y palabras el parametro CODCanal. Si no lo
	 * encuentra devuelve uno por defecto.
	 * 
	 * @param pRequest
	 * @return String con parametro o el parametro por defecto. */
	public static String getCODCanal(HttpServletRequest pRequest)
	{
		String strReturn = null;
		List<String> listPosibilities = new ArrayList();
		listPosibilities.add("CODCanal");
		listPosibilities.add("canal");
		listPosibilities.add("codigoCanal");
		listPosibilities.add("codCanal");
		listPosibilities.add("channelCode");
		listPosibilities.add("channel");
		for (String strPosibility : listPosibilities)
		{
			String strValue = (String) pRequest.getParameter(strPosibility);
			if (strValue != null)
			{
				strReturn = strValue;
				break;
			}
		}
		if (strReturn == null)
		{
			strReturn = "18";
		}
		return strReturn;
	}

	/** Busca dentro del pRequest con diferentes convinaciones de letras y palabras el parametro CODSecIp. Si no lo
	 * encuentra devuelve uno por defecto.
	 * 
	 * @param pRequest
	 * @return String con parametro o el parametro por defecto. */
	public static String getCODSecIp(HttpServletRequest pRequest)
	{
		String strReturn = null;
		List<String> listPosibilities = new ArrayList();
		listPosibilities.add("CODSecIp");
		listPosibilities.add("codigoIP");
		listPosibilities.add("codigoIp");
		listPosibilities.add("direccionIP");
		listPosibilities.add("codigoSecIp");
		listPosibilities.add("ip");
		for (String strPosibility : listPosibilities)
		{
			String strValue = (String) pRequest.getParameter(strPosibility);
			if (strValue != null)
			{
				strReturn = strValue;
				break;
			}
		}
		if (strReturn == null)
		{
			strReturn = "10.1.245.2";
		}
		return strReturn;
	}
}
