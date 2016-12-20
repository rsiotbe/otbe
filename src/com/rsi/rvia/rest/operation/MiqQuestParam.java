package com.rsi.rvia.rest.operation;

public class MiqQuestParam
{
    int    nId;
    String strName;
    String strValue;
    String strDesc;
    String strType;

    public MiqQuestParam(int nId, String strName, String strValue, String strDesc, String strType,
            String strHeaderName, String strAlias)
    {
        super();
        this.nId = nId;
        this.strName = strName;
        this.strValue = strValue;
        this.strDesc = strDesc;
        this.strType = strType;
        this.strHeaderName = strHeaderName;
        this.strAlias = strAlias;
    }

    public int getnId()
    {
        return nId;
    }

    public void setnId(int nId)
    {
        this.nId = nId;
    }

    public String getStrName()
    {
        return strName;
    }

    public void setStrName(String strName)
    {
        this.strName = strName;
    }

    public String getStrValue()
    {
        return strValue;
    }

    public void setStrValue(String strValue)
    {
        this.strValue = strValue;
    }

    public String getStrDesc()
    {
        return strDesc;
    }

    public void setStrDesc(String strDesc)
    {
        this.strDesc = strDesc;
    }

    public String getStrType()
    {
        return strType;
    }

    public void setStrType(String strType)
    {
        this.strType = strType;
    }

    public String getStrHeaderName()
    {
        return strHeaderName;
    }

    public void setStrHeaderName(String strHeaderName)
    {
        this.strHeaderName = strHeaderName;
    }

    public String getStrAlias()
    {
        return strAlias;
    }

    public void setStrAlias(String strAlias)
    {
        this.strAlias = strAlias;
    }

    String strHeaderName;
    String strAlias;
}
