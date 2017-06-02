package com.rsi.rvia.rest.endpoint.common;

import org.json.JSONException;
import org.json.JSONObject;
import com.rsi.rvia.rest.error.exceptions.ApplicationException;

public class SignExtractor
{
    public static JSONObject extraerCoordenada(String pHtml) throws ApplicationException, JSONException
    {
        String strCoord = "";
        int inicio = pHtml.indexOf("COORDENADA");
        if (inicio == -1)
        {
            throw new ApplicationException(500, 99999, "Coordenada no localizada", "Coordenada no localizada", null);
        }
        strCoord = pHtml.substring(inicio, inicio + 23);
        strCoord = strCoord.replaceAll(".*value='([a-zA-Z]\\d+)'.*", "$1");
        if (!strCoord.replaceAll("[a-zA-Z]\\d+", "").equals(""))
        {
            throw new ApplicationException(500, 99999, "Coordenada incorrecta", "Formato de coordenada no válido", null);
        }
        // TODO: Pendiente de extraer los datos de tipo de firma directamente de tabla, si aplica.
        String tiopcf = "A";
        String tipope = "142";
        JSONObject pJson = new JSONObject();
        pJson.put("type", "coordenada");
        pJson.put("value", strCoord);
        pJson.put("TIOPCF", tiopcf);
        pJson.put("TIPOPE", tipope);
        return pJson;
    }
}
