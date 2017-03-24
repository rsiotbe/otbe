package com.rsi.rvia.rest.response;

import org.json.JSONException;
import org.json.JSONObject;

public class RviaRestResponseErrorItem
{
    private JSONObject pJson;

    public RviaRestResponseErrorItem(String strCode, String strText) throws JSONException
    {
        pJson = new JSONObject();
        pJson.put("code", strCode);
        pJson.put("text", strText);
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
