package com.rsi.rvia.rest.endpoint.simulators;

import org.json.JSONException;
import org.json.JSONObject;

import com.rsi.rvia.rest.tool.GettersRequestParams;

public abstract class WSObject {
	private String strCODSecEnt;
    private String strCODSecUser;
    private String strCODSecTrans;
    private String strCODTerminal;
    private String strCODApl;
    private String strCODCanal;
    private String strCODSecIp;
	
    public String getStrCODSecEnt() {
		return strCODSecEnt;
	}
	public void setStrCODSecEnt(String strCODSecEnt) {
		this.strCODSecEnt = strCODSecEnt;
	}
	public String getStrCODSecUser() {
		return strCODSecUser;
	}
	public void setStrCODSecUser(String strCODSecUser) {
		this.strCODSecUser = strCODSecUser;
	}
	public String getStrCODSecTrans() {
		return strCODSecTrans;
	}
	public void setStrCODSecTrans(String strCODSecTrans) {
		this.strCODSecTrans = strCODSecTrans;
	}
	public String getStrCODTerminal() {
		return strCODTerminal;
	}
	public void setStrCODTerminal(String strCODTerminal) {
		this.strCODTerminal = strCODTerminal;
	}
	public String getStrCODApl() {
		return strCODApl;
	}
	public void setStrCODApl(String strCODApl) {
		this.strCODApl = strCODApl;
	}
	public String getStrCODCanal() {
		return strCODCanal;
	}
	public void setStrCODCanal(String strCODCanal) {
		this.strCODCanal = strCODCanal;
	}
	public String getStrCODSecIp() {
		return strCODSecIp;
	}
	public void setStrCODSecIp(String strCODSecIp) {
		this.strCODSecIp = strCODSecIp;
	} 
	public abstract JSONObject toJSON();
	
	public abstract String toJSONString();
}
