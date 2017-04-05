package com.rsi.rvia.rest.response;

import org.json.JSONException;
import org.json.JSONObject;
import com.rsi.rvia.rest.response.ruralvia.TranslateRviaJsonObject;

public class RviaRestResponseErrorItem
{
    private JSONObject pJson;

    public RviaRestResponseErrorItem(String strCode, String strText, String strDescription) throws JSONException
    {
        pJson = new JSONObject();
        pJson.put("code", strCode);
        pJson.put("text", strText);
        pJson.put("description", strText);
    }

    public RviaRestResponseErrorItem(String strCode, String strText) throws JSONException
    {
        pJson = new JSONObject();
        pJson.put("code", strCode);
        pJson.put("text", strText);
    }

    public RviaRestResponseErrorItem(TranslateRviaJsonObject errorObj) throws JSONException
    {
        pJson = new JSONObject();
        pJson.put("code", errorObj.getCode());
        pJson.put("text", errorObj.getTexterror());
        pJson.put("description", errorObj.getDescription());
    }

    public JSONObject getJsonObject()
    {
        return pJson;
    }

    public String toJsonString()
    {
        return pJson.toString();
    }

    public String toString()
    {
        try
        {
            return pJson.toString(3);
        }
        catch (JSONException e)
        {
            return "Error parsing: " + e.toString();
        }
    }
}
