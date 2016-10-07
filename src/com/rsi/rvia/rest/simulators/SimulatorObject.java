package com.rsi.rvia.rest.simulators;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

public class SimulatorObject
{
	public int								nId;
	public String							strNRBE;
	public String							strEntityName;
	public String							strCategory;
	public String							strComercialName;
	public String							strType;
	public boolean							fIsActive;
	public boolean							fAllowBooking;
	public boolean							fContactEmail;
	public boolean							fContactTelephone;
	public Hashtable<String, Object>	pConfigParams;

	public SimulatorObject(int nId, String strNRBE, String strEntityName, String strCategory, String strComercialName,
			String strType, boolean fIsActive, boolean fAllowBooking, boolean fContactEmail, boolean fContactTelephone)
	{
		this.nId = nId;
		this.strNRBE = strNRBE;
		this.strEntityName = strEntityName;
		this.strCategory = strCategory;
		this.strComercialName = strComercialName;
		this.strType = strType;
		this.fIsActive = fIsActive;
		this.fAllowBooking = fAllowBooking;
		this.fContactEmail = fContactEmail;
		this.fContactTelephone = fContactTelephone;
		this.pConfigParams = new Hashtable<String, Object>();
	}

	public JSONObject toJson() throws JSONException
	{
		JSONObject pReturn = new JSONObject();
		JSONObject pConfig = new JSONObject();
		pReturn.put("nrbe", strNRBE);
		pReturn.put("nrbeName", strEntityName);
		pReturn.put("comercialName", strComercialName);
		pReturn.put("category", strCategory);
		pReturn.put("type", strType);
		pReturn.put("active", fIsActive);
		pReturn.put("allowBooking", fAllowBooking);
		pReturn.put("contactEmail", fContactEmail);
		pReturn.put("contactTelephone", fContactTelephone);
		Set<String> keys = pConfigParams.keySet();
		Iterator<String> itr = keys.iterator();
		while (itr.hasNext())
		{
			String strKey = itr.next();
			pConfig.put(strKey, pConfigParams.get(strKey));
		}
		pReturn.put("config", pConfig);
		return pReturn;
	}

	public String toString()
	{
		StringBuilder pSb = new StringBuilder();
		pSb.append("Id              :" + nId + "\n");
		pSb.append("NRBE            :" + strNRBE + "\n");
		pSb.append("NRBEName        :" + strEntityName + "\n");
		pSb.append("ComercialName   :" + strComercialName + "\n");
		pSb.append("Type            :" + strType + "\n");
		pSb.append("IsActive        :" + fIsActive + "\n");
		pSb.append("AllowBooking    :" + fAllowBooking + "\n");
		pSb.append("ContactEmail    :" + fContactEmail + "\n");
		pSb.append("ContactTelephone:" + fContactTelephone + "\n");
		pSb.append("ConfigParams    :" + pConfigParams);
		return pSb.toString();
	}
}
