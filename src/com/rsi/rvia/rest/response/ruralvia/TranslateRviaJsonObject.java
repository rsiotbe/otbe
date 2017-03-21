package com.rsi.rvia.rest.response.ruralvia;

import com.rsi.rvia.rest.response.RviaRestResponse;

/**
 * The Class TranslateJsonObject.
 */
public class TranslateRviaJsonObject
{
    /** The str code. */
    private String                strCode;
    /** The str tipo. */
    private RviaRestResponse.Type pType;
    /** The str desc. */
    private String                strTextError;
    /** The nIdMiq */
    private int                   nIdMiq;
    /** The descripcion. */
    private String                strDescription;

    /**
     * Gets the descripcion.
     * 
     * @return the descripcion
     */
    public String getDescription()
    {
        return strDescription;
    }

    /**
     * Sets the descripcion.
     * 
     * @param strDescription
     *            the new descripcion
     */
    public void setDescription(String strDescription)
    {
        this.strDescription = strDescription;
    }

    /**
     * Gets the str code.
     * 
     * @return the str code
     */
    public String getCode()
    {
        return strCode;
    }

    /**
     * Sets the str code.
     * 
     * @param strCode
     *            the new str code
     */
    public void setCode(String strCode)
    {
        this.strCode = strCode;
    }

    /**
     * Gets the ClavePagina.
     * 
     * @return the ClavePagina
     */
    public String getTexterror()
    {
        return strTextError;
    }

    /**
     * Sets the ClavePagina.
     * 
     * @param strDesc
     *            the new ClavePagina
     */
    public void setIdMiq(int nIdMiq)
    {
        this.nIdMiq = nIdMiq;
    }

    /**
     * Gets the str desc.
     * 
     * @return the str desc
     */
    public String ClavePagina()
    {
        return strTextError;
    }

    /**
     * Sets the str desc.
     * 
     * @param strDesc
     *            the new str desc
     */
    public void setTexterror(String strTextError)
    {
        this.strTextError = strTextError;
    }

    /**
     * Gets the str tipo.
     * 
     * @return the str tipo
     */
    public RviaRestResponse.Type getTipo()
    {
        return pType;
    }

    /**
     * Sets the str tipo.
     * 
     * @param strTipo
     *            the new str tipo
     */
    public void setType(RviaRestResponse.Type pType)
    {
        this.pType = pType;
    }

    public String toString()
    {
        return strCode + "::" + strTextError + "::" + pType + "::" + nIdMiq + "::" + strDescription;
    }
}
