package com.rsi.rvia.rest.response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RviaRestResponse
{
    private JSONObject    pJson;
    private int           nHttpCode;
    private static String ROOT_NODE     = "response";
    private static String DATA_NODE     = "data";
    private static String STATUS_NODE   = "status";
    private static String MESSAGES_LIST = "messages";
    private static String LEVEL_NODE    = "level";

    public enum Type
    {
        ERROR, WARNING, OK
    }

    public RviaRestResponse(Type pType, int nHttpCode, String strJsonData, RviaRestResponseErrorItem pErrorItem)
            throws JSONException
    {
        JSONObject pAux;
        JSONObject pStatus;
        JSONArray pMessageArray;
        this.nHttpCode = nHttpCode;
        pAux = new JSONObject();
        pStatus = new JSONObject();
        pMessageArray = new JSONArray();
        if (strJsonData == null || strJsonData.trim().isEmpty())
            strJsonData = "{}";
        pAux.put(DATA_NODE, new JSONObject(strJsonData));
        pStatus.put(LEVEL_NODE, pType.name());
        if (pErrorItem != null)
            pMessageArray.put(pErrorItem.getJsonObject());
        pStatus.put(MESSAGES_LIST, pMessageArray);
        pAux.put(STATUS_NODE, pStatus);
        pJson = new JSONObject();
        pJson.put(ROOT_NODE, pAux);
    }

    public RviaRestResponse(Type pType, String strJsonData, RviaRestResponseErrorItem pErrorItem) throws JSONException
    {
        JSONObject pAux;
        JSONObject pStatus;
        JSONArray pMessageArray;
        switch (pType)
        {
            case ERROR:
                this.nHttpCode = 500;
                break;
            default:
                this.nHttpCode = 200;
                break;
        }
        pAux = new JSONObject();
        pStatus = new JSONObject();
        pMessageArray = new JSONArray();
        if (strJsonData == null || strJsonData.trim().isEmpty())
            strJsonData = "{}";
        pAux.put(DATA_NODE, new JSONObject(strJsonData));
        pStatus.put(LEVEL_NODE, pType.name());
        if (pErrorItem != null)
            pMessageArray.put(pErrorItem.getJsonObject());
        pStatus.put(MESSAGES_LIST, pMessageArray);
        pAux.put(STATUS_NODE, pStatus);
        pJson = new JSONObject();
        pJson.put(ROOT_NODE, pAux);
    }

    public RviaRestResponse(Type pType, JSONObject pData, RviaRestResponseErrorItem pErrorItem) throws JSONException
    {
        this(pType, pData.toString(), pErrorItem);
    }

    public RviaRestResponse(Type pType, String strJsonData) throws JSONException
    {
        this(pType, strJsonData, null);
    }

    public RviaRestResponse(Type pType, JSONObject pData) throws JSONException
    {
        this(pType, pData.toString(), null);
    }

    public RviaRestResponse(Type pType, int nHttpCode, JSONObject pData, RviaRestResponseErrorItem pErrorItem)
            throws JSONException
    {
        this(pType, nHttpCode, pData.toString(), pErrorItem);
    }

    public RviaRestResponse(Type pType, int nHttpCode, String strJsonData) throws JSONException
    {
        this(pType, nHttpCode, strJsonData, null);
    }

    public RviaRestResponse(Type pType, int nHttpCode, JSONObject pData) throws JSONException
    {
        this(pType, nHttpCode, pData.toString(), null);
    }

    public String toJsonString()
    {
        return pJson.toString();
    }

    public int getHttpCode()
    {
        return nHttpCode;
    }

    public JSONObject getJsonObject()
    {
        return pJson;
    }

    public String toString()
    {
        try
        {
            return pJson.toString(3);
        }
        catch (JSONException e)
        {
            return pJson.toString();
        }
    }
}
