package com.rsi.rvia.rest.endpoint.common;

import org.json.JSONException;
import org.json.JSONObject;
import com.rsi.rvia.rest.error.exceptions.ApplicationException;

public class SignExtractor
{
    public enum SignType
    {
        coordenada, posiciones;
    }

    public static JSONObject extraerCoordenada(String strHtml) throws ApplicationException, JSONException
    {
        String strCoord = "";
        SignType pSignType;
        int inicio = strHtml.indexOf("COORDENADA");
        if (inicio == -1)
        {
            throw new ApplicationException(500, 99999, "Coordenada no localizada", "Coordenada no localizada", null);
        }
        strCoord = strHtml.substring(inicio, inicio + 23);
        strCoord = strCoord.replaceAll(".*value='([a-zA-Z]\\d+)'.*", "$1");
        if (!strCoord.replaceAll("[a-zA-Z]\\d+", "").equals(""))
        {
            throw new ApplicationException(500, 99999, "Coordenada incorrecta", "Formato de coordenada no válido",
                    null);
        }
        /* se comprueba que tipo de firma es, coordenada o posiciones */
        pSignType = (strHtml.contains("/cajafirma/bootstrap.css")) ? SignType.coordenada : SignType.posiciones;
        String tiopcf = "A";
        String tipope = "142";
        JSONObject pJson = new JSONObject();
        pJson.put("type", pSignType.name());
        pJson.put("value", strCoord);
        pJson.put("TIOPCF", tiopcf);
        pJson.put("TIPOPE", tipope);
        return pJson;
    }
}
