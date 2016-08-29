package com.rsi.rvia.rest.tool;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

public class GettersRequestParams
{
	public static String getCODSecEnt(HttpServletRequest pRequest){
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
		if(strReturn == null){
			strReturn = "3008";
		}
		return strReturn;
	}
	
	public static String getCODSecUser(HttpServletRequest pRequest){
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
		if(strReturn == null){
			strReturn = "";
		}
		return strReturn;
	}
	
	public static String getCODSecTrans(HttpServletRequest pRequest){
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
		if(strReturn == null){
			strReturn = "";
		}
		return strReturn;
	}
	
	public static String getCODTerminal(HttpServletRequest pRequest){
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
		if(strReturn == null){
			strReturn = "18";
		}
		return strReturn;
	}
	
	public static String getCODApl(HttpServletRequest pRequest){
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
		if(strReturn == null){
			strReturn = "BDP";
		}
		return strReturn;
	}
	
	public static String getCODCanal(HttpServletRequest pRequest){
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
		if(strReturn == null){
			strReturn = "18";
		}
		return strReturn;
	}
	
	public static String getCODSecIp(HttpServletRequest pRequest){
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
		if(strReturn == null){
			strReturn = "10.1.245.2";
		}
		return strReturn;
	}
	
	
}
