package com.rsi.rvia.rest.endpoint.simulators;

import org.json.JSONObject;
import org.json.JSONException;

public class CardObject extends WSObject {
		
	//private String strNRBE;
	
	private String strLinea;
	
	private String strGrProducto;
	
	private String strProducto;
	
	private String strTarifa;
	
	private String strTarjeta;
	
	//private String strSecIP;

	/*public String getStrNRBE() {
		return strNRBE;
	}

	public void setStrNRBE(String strNRBE) {
		this.strNRBE = strNRBE;
	}*/

	public String getStrLinea() {
		return strLinea;
	}

	public void setStrLinea(String strLinea) {
		this.strLinea = strLinea;
	}

	public String getStrGrProducto() {
		return strGrProducto;
	}

	public void setStrGrProducto(String strGrProducto) {
		this.strGrProducto = strGrProducto;
	}

	public String getStrProducto() {
		return strProducto;
	}

	public void setStrProducto(String strProducto) {
		this.strProducto = strProducto;
	}

	public String getStrTarifa() {
		return strTarifa;
	}

	public void setStrTarifa(String strTarifa) {
		this.strTarifa = strTarifa;
	}

	public String getStrTarjeta() {
		return strTarjeta;
	}

	public void setStrTarjeta(String strTarjeta) {
		this.strTarjeta = strTarjeta;
	}

	/*public String getStrSecIP() {
		return strSecIP;
	}

	public void setStrSecIP(String strSecIP) {
		this.strSecIP = strSecIP;
	}*/

	public JSONObject toJSON() {
		try 
		{
			String strJSON = this.toJSONString();  
			return new JSONObject(strJSON);
		}
		catch (JSONException ex)
		{
			return null;
		}
	}
	
	public String toJSONString() {
		return "{ \"codigoEntidad\" : \"" + this.getStrCODSecEnt() + "\",\"codigoLinea\" : \"" + this.strLinea + "\",\"grupoProductos\" : \"" + this.strGrProducto + "\",\"producto\" : \"" + this.strProducto + "\",\"tarifa\" : \""+ this.strTarifa + "\" }"; 
	}
}
