package com.rsi.rvia.rest.response;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import com.rsi.Constants;
import com.rsi.rvia.rest.response.ruralvia.TranslateRviaJsonObject;
import com.rsi.rvia.rest.tool.Utils;

public class RviaRestResponseErrorItem
{
    private JSONObject          pJson;
    private static final String KEY_CODE        = "code";
    private static final String KEY_TEXT        = "text";
    private static final String KEY_DESCRIPTION = "description";

    public RviaRestResponseErrorItem(String strCode, String strText, String strDescription) throws JSONException
    {
        pJson = new JSONObject();
        pJson.put(KEY_CODE, strCode);
        pJson.put(KEY_TEXT, strText);
        pJson.put(KEY_DESCRIPTION, strDescription);
    }

    public RviaRestResponseErrorItem(String strCode, String strText) throws JSONException
    {
        pJson = new JSONObject();
        pJson.put(KEY_CODE, strCode);
        pJson.put(KEY_TEXT, strText);
    }

    public RviaRestResponseErrorItem(TranslateRviaJsonObject errorObj) throws JSONException
    {
        pJson = new JSONObject();
        pJson.put(KEY_CODE, errorObj.getCode());
        pJson.put(KEY_TEXT, errorObj.getTexterror());
        pJson.put(KEY_DESCRIPTION, errorObj.getDescription());
    }

    public RviaRestResponseErrorItem(TranslateRviaJsonObject errorObj, JSONObject pJsonData) throws JSONException
    {
        String errorCode = errorObj.getCode();
        pJson = new JSONObject();
        pJson.put(KEY_CODE, errorCode);
        pJson.put(KEY_TEXT, errorObj.getTexterror());
        pJson.put(KEY_DESCRIPTION, errorObj.getDescription());
        formatError(errorCode, pJsonData);
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

    /**
     * Se parsean parámetros en el String segú el tipo de error.
     * 
     * @param errorCode
     * @param pJsonData
     * @throws JSONException
     */
    private void formatError(String errorCode, JSONObject pJsonData) throws JSONException
    {
        JSONObject data = pJsonData.getJSONObject("ruralvia").getJSONObject("data");
        JSONObject userData = data.getJSONObject("userData");
        JSONObject nrbeData = data.getJSONObject("nrbeData");
        // En caso de crecer los errores a tratar, se usará un enum y un switch
        if (errorCode.equals(Constants.ERROR_SIGN_BLOCKED))
        {
            List<String> paramsText = new ArrayList<String>();
            paramsText.add(userData.getString("user"));
            String textFormatted = Utils.formatString(pJson.getString(KEY_TEXT), paramsText);
            pJson.put(KEY_TEXT, textFormatted);
            List<String> paramsDesc = new ArrayList<String>();
            paramsDesc.add(userData.getString("telephone"));
            paramsDesc.add(userData.getString("email"));
            String descriptionFormatted = Utils.formatString(pJson.getString(KEY_DESCRIPTION), paramsDesc);
            pJson.put(KEY_DESCRIPTION, descriptionFormatted);
        }
        else if (errorCode.equals(Constants.ERROR_EMPTY_LIST))
        {
            List<String> params = new ArrayList<String>();
            params.add(userData.getString("telephone"));
            params.add(userData.getString("email"));
            String descriptionFormatted = Utils.formatString(pJson.getString(KEY_DESCRIPTION), params);
            pJson.put(KEY_DESCRIPTION, descriptionFormatted);
        }
    }
}
