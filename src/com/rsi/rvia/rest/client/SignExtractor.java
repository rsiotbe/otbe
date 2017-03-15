package com.rsi.rvia.rest.client;

import com.rsi.rvia.rest.error.exceptions.ApplicationException;

public class SignExtractor
{
    public static String extraerCoordenada(String pHtml) throws ApplicationException
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
            throw new ApplicationException(500, 99999, "Coordenada incorrecta", "Formato de coordenda no válido", null);
        }
        // TODO: Pendiente de extraer los datos de tipo de firma directamente de tabla, si aplica.
        String tiopcf = "A";
        String tipope = "142";
        strCoord = "{\"type\":\"coordenada\",\"value\":\"" + strCoord + "\",\"TIOPCF\":\"" + tiopcf
                + "\",\"TIPOPE\":\"" + tipope + "\"}";
        return strCoord;
    }
}
