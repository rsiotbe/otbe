package com.rsi.rvia.rest.endpoint.simulators;

import org.json.JSONException;
import org.json.JSONObject;

public class CardObject
{
    public static final String ATTR_CODIGO_ENTIDAD = "codigoEntidad";
    public static final String ATTR_CODIGO_LINEA   = "codigoLinea";
    public static final String ATTR_GRUPO_PRODUCTO = "grupoProductos";
    public static final String ATTR_PRODUCTO       = "producto";
    public static final String ATTR_TARIFA         = "tarifa";
    private String             strLinea;
    private String             strGrProducto;
    private String             strProducto;
    private String             strTarifa;
    private String             strTarjeta;
    private String             strCodEntidad;
    private String             strCondCuota;
    private String             strCondPorc;

    public String getStrLinea()
    {
        return strLinea;
    }

    public void setStrLinea(String strLinea)
    {
        this.strLinea = strLinea;
    }

    public String getStrGrProducto()
    {
        return strGrProducto;
    }

    public void setStrGrProducto(String strGrProducto)
    {
        this.strGrProducto = strGrProducto;
    }

    public String getStrProducto()
    {
        return strProducto;
    }

    public void setStrProducto(String strProducto)
    {
        this.strProducto = strProducto;
    }

    public String getStrTarifa()
    {
        return strTarifa;
    }

    public void setStrTarifa(String strTarifa)
    {
        this.strTarifa = strTarifa;
    }

    public String getStrTarjeta()
    {
        return strTarjeta;
    }

    public void setStrTarjeta(String strTarjeta)
    {
        this.strTarjeta = strTarjeta;
    }

    public String getStrCodEntidad()
    {
        return strCodEntidad;
    }

    public void setStrCodEntidad(String strCodEntidad)
    {
        this.strCodEntidad = strCodEntidad;
    }

    public void setStrCondicionesCuota(String strCondCuota)
    {
        this.strCondCuota = strCondCuota;
    }

    public String getStrCondicionesCuota()
    {
        return strCondCuota;
    }

    public String getStrCondicionesPorcentaje()
    {
        return strCondPorc;
    }

    public void setStrCondicionesPorcentaje(String strCondPorc)
    {
        this.strCondPorc = strCondPorc;
    }

    public JSONObject toJSON()
    {
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

    public String toJSONString()
    {
        return "{ \"" + ATTR_CODIGO_ENTIDAD + "\" : \"" + this.strCodEntidad + "\",\"" + ATTR_CODIGO_LINEA + "\" : \""
                + this.strLinea + "\",\"" + ATTR_GRUPO_PRODUCTO + "\" : \"" + this.strGrProducto + "\",\""
                + ATTR_PRODUCTO + "\" : \"" + this.strProducto + "\",\"" + ATTR_TARIFA + "\" : \"" + this.strTarifa
                + "\" }";
    }
}
