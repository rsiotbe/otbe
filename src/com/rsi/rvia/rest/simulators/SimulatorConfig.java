package com.rsi.rvia.rest.simulators;

import java.util.Hashtable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.rsi.Constants;

/**
 * Objeto que representa toda la información y configuración que contiene un simulador
 */
public class SimulatorConfig
{
    private int                       nId;
    private String                    strNRBE;
    private String                    strNRBEName;
    private String                    strCategory;
    private String                    strSimpleName;
    private String                    strComercialName;
    private String                    strCustomerSupportEmail;
    private String                    strCustomerSupportTelephone;
    private String                    strReceivingOfficeEmail;
    private boolean                   fIsActive;
    private boolean                   fAllowBooking;
    private boolean                   fAllowUserEmail;
    private boolean                   fAllowUserTelephone;
    private String                    strDisclaimer;
    private String                    strContractConditions;
    private String                    strDescription;
    private String                    strLOPD;
    private Hashtable<String, Object> pConfigParams;
    private LoanType                  pLoanType;

    /**
     * Enumeración de tipos de calculo diferentes
     */
    private enum LoanType
    {
        FRENCHLOAN, MORTGAGE, MORTGAGECHANGE
    }

    /**
     * Enumeración de tipos de interes
     */
    private enum InterestType
    {
        FIX, REFERENCE
    }

    /**
     * Constructor
     * 
     * @param nId
     *            Id del simulador
     * @param strNRBE
     *            Entidad propietaria del simulador
     * @param strNRBEName
     *            Nombre de la entidad propietaria del simulador
     * @param strEntityName
     *            NOmbre de la entidad
     * @param strCategory
     *            Tipo de prestamo
     * @param strSimpleName
     *            Nombre comerciual del prestamo
     * @param strComercialName
     *            Nombre comerciual del prestamo
     * @param strLoanType
     *            Tipo de calculo que contiene
     * @param fIsActive
     *            indicador de si está activo el prestamo
     * @param fAllowBooking
     *            Indica si permite contratar online
     * @param fAllowUserEmail
     *            Permite la ingresión de email de contacto con el cliente
     * @param fAllowUserTelephone
     *            Permite la ingresión de telefono de contacto con el cliente
     */
    public SimulatorConfig(int nId, String strNRBE, String strNRBEName, String strCategory, String strSimpleName,
            String strComercialName, String strLoanType, boolean fIsActive, boolean fAllowBooking,
            boolean fAllowUserEmail, boolean fAllowUserTelephone, String strCustomerSupportEmail,
            String strCustomerSupportTelephone, String strReceivingOfficeEmail, String strLOPD, String strDisclaimer,
            String strContractConditions, String strDescription)
    {
        this.nId = nId;
        this.strNRBE = strNRBE;
        this.strNRBEName = strNRBEName;
        this.strCustomerSupportEmail = strCustomerSupportEmail;
        this.strCustomerSupportTelephone = strCustomerSupportTelephone;
        this.strReceivingOfficeEmail = strReceivingOfficeEmail;
        this.strCategory = strCategory;
        this.strSimpleName = strSimpleName;
        this.strComercialName = strComercialName;
        this.pLoanType = LoanType.valueOf(strLoanType.toUpperCase());
        this.fIsActive = fIsActive;
        this.fAllowBooking = fAllowBooking;
        this.fAllowUserEmail = fAllowUserEmail;
        this.fAllowUserTelephone = fAllowUserTelephone;
        this.strLOPD = strLOPD;
        this.strDisclaimer = strDisclaimer;
        this.strContractConditions = strContractConditions;
        this.strDescription = strDescription;
        this.pConfigParams = new Hashtable<String, Object>();
    }

    public void addConfigParam(String strKey, Object objValue)
    {
        this.pConfigParams.put(strKey, objValue);
    }

    /**
     * Coonviete el objeto a un JSON
     * 
     * @return Objeto JSON
     * @throws JSONException
     */
    public JSONObject toJson() throws JSONException
    {
        JSONObject pReturn = new JSONObject();
        JSONObject pConfig = new JSONObject();
        JSONObject pTerms;
        pReturn.put(Constants.SIMULADOR_NRBE, strNRBE);
        pReturn.put(Constants.SIMULADOR_NRBE_NAME, strNRBEName);
        pReturn.put(Constants.SIMULADOR_SIMPLE_NAME, strSimpleName);
        pReturn.put(Constants.SIMULADOR_COMERCIAL_NAME, strComercialName);
        pReturn.put(Constants.SIMULADOR_SAC_EMAIL, strCustomerSupportEmail);
        pReturn.put(Constants.SIMULADOR_SAC_TELEPHONE, strCustomerSupportTelephone);
        pReturn.put(Constants.SIMULADOR_OFICINA_EMAIL, strReceivingOfficeEmail);
        pReturn.put(Constants.SIMULADOR_CATEGORY, strCategory);
        pReturn.put(Constants.SIMULADOR_CALC_TYPE, pLoanType.name());
        pReturn.put(Constants.SIMULADOR_ACTIVE, fIsActive);
        pReturn.put(Constants.SIMULADOR_ALLOW_BOOKING, fAllowBooking);
        pReturn.put(Constants.SIMULADOR_ALLOW_USER_EMAIL, fAllowUserEmail);
        pReturn.put(Constants.SIMULADOR_ALLOW_USER_TELEPHONE, fAllowUserTelephone);
        pReturn.put(Constants.SIMULADOR_LOPD, strLOPD);
        pReturn.put(Constants.SIMULADOR_DISCLAIMER, strDisclaimer);
        pReturn.put(Constants.SIMULADOR_CONTRACT_CONDITIONS, strContractConditions);
        pReturn.put(Constants.SIMULADOR_DESCRIPTION, strDescription);
        /* se realiza la comprobación si tiene comisión de apertuyra para rellenar los datos */
        pConfig.put("amount", getAmountJson());
        pConfig.put("fee", getFees());
        pTerms = getTerms();
        switch (pLoanType)
        {
            case FRENCHLOAN:
                pConfig.put("interest", getInterestOneRange());
                pConfig.put("term", pTerms);
                break;
            case MORTGAGE:
                pConfig.put("interest", getInterestOneRange());
                /* se añade el parámetro especial de plazos por año */
                pTerms.put("deadlinePerYear", Integer.parseInt((String) pConfigParams.get("deadlinePerYear")));
                pConfig.put("term", pTerms);
                /* porcentage maximo hipotecable de la vivienda */
                pConfig.put("loanPercentMax", Double.parseDouble((String) pConfigParams.get("loanAmountPercentMax")));
                pConfig.put("newLoanPercentMax", Double.parseDouble((String) pConfigParams.get("newLoanPercentMax")));
                pConfig.put("oldLoanPercentMax", Double.parseDouble((String) pConfigParams.get("oldLoanPercentMax")));
                break;
            case MORTGAGECHANGE:
                pConfig.put("range", getInterestAndTermsTwoRanges());
                /* se añade el parámetro especial de plazos por año */
                pTerms.put("deadlinePerYear", Integer.parseInt((String) pConfigParams.get("deadlinePerYear")));
                pConfig.put("term", pTerms);
                /* porcentage maximo hipotecable de la vivienda */
                pConfig.put("loanPercentMax", Double.parseDouble((String) pConfigParams.get("loanAmountPercentMax")));
                pConfig.put("newLoanPercentMax", Double.parseDouble((String) pConfigParams.get("newLoanPercentMax")));
                pConfig.put("oldLoanPercentMax", Double.parseDouble((String) pConfigParams.get("oldLoanPercentMax")));
                break;
        }
        pReturn.put("config", pConfig);
        return pReturn;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        StringBuilder pSb = new StringBuilder();
        pSb.append("Id                  : " + nId + "\n");
        pSb.append("NRBE                : " + strNRBE + "\n");
        pSb.append("Nombre NRBE         : " + strNRBEName + "\n");
        pSb.append("SimpleName          : " + strSimpleName + "\n");
        pSb.append("ComercialName       : " + strComercialName + "\n");
        pSb.append("Type                : " + pLoanType.name() + "\n");
        pSb.append("IsActive            : " + fIsActive + "\n");
        pSb.append("CustomerSupportEmail: " + strCustomerSupportEmail + "\n");
        pSb.append("CustomerSupportTfn  : " + strCustomerSupportTelephone + "\n");
        pSb.append("AllowBooking        : " + fAllowBooking + "\n");
        pSb.append("AllowUserEmail      : " + fAllowUserEmail + "\n");
        pSb.append("AllowUserTelephone  : " + fAllowUserTelephone + "\n");
        pSb.append("Description         : " + strDescription + "\n");
        pSb.append("Disclaimer          : " + strDisclaimer + "\n");
        pSb.append("LOPD                : " + strLOPD + "\n");
        pSb.append("ContractConditions  : " + strContractConditions + "\n");
        pSb.append("ConfigParams        : " + pConfigParams);
        return pSb.toString();
    }

    private JSONObject getAmountJson() throws JSONException
    {
        JSONObject pReturn;
        double dbAmountMax;
        double dbAmountMin;
        double dbAmountDefault;
        dbAmountMax = Double.parseDouble((String) pConfigParams.get("amountMax"));
        dbAmountMin = Double.parseDouble((String) pConfigParams.get("amountMin"));
        dbAmountDefault = Double.parseDouble((String) pConfigParams.get("amountDefault"));
        pReturn = new JSONObject();
        pReturn.put("max", dbAmountMax);
        pReturn.put("min", dbAmountMin);
        pReturn.put("default", dbAmountDefault);
        return pReturn;
    }

    private JSONObject getInterestOneRange() throws JSONException
    {
        JSONObject pReturn = new JSONObject();
        double dbInterest;
        double dbInterestBase;
        double dbInterestDelta;
        InterestType pInterestType;
        pInterestType = InterestType.valueOf(((String) pConfigParams.get("interestType")).toUpperCase());
        dbInterest = Double.parseDouble((String) pConfigParams.get("interest"));
        pReturn.put("interestType", pInterestType.name());
        if (pInterestType == InterestType.FIX)
        {
            pReturn.put("value", dbInterest);
        }
        else if (pInterestType == InterestType.REFERENCE)
        {
            dbInterestBase = Double.parseDouble((String) pConfigParams.get("interestBase"));
            dbInterestDelta = Double.parseDouble((String) pConfigParams.get("interestDelta"));
            dbInterest = dbInterestBase + dbInterestDelta;
            pReturn.put("value", dbInterest);
            pReturn.put("base", dbInterestBase);
            pReturn.put("baseName", pConfigParams.get("interestBaseName"));
            pReturn.put("delta", dbInterestDelta);
        }
        return pReturn;
    }

    private JSONArray getInterestAndTermsTwoRanges() throws JSONException
    {
        JSONArray pReturn = new JSONArray();
        String strRangeTemplate = "range_";
        for (int i = 1; i < 5; i++)
        {
            String strPattern = strRangeTemplate + i + "_";
            /* si existe configuración para este intervalo */
            if (pConfigParams.get(strPattern + "interestType") != null)
            {
                JSONObject pResult = new JSONObject();
                JSONObject pInterest = new JSONObject();
                /* se calcula el objeto interes */
                InterestType pInterestType = InterestType.valueOf(
                        ((String) pConfigParams.get(strPattern + "interestType")).toUpperCase());
                pInterest.put("interestType", pInterestType.name());
                if (pInterestType == InterestType.FIX)
                {
                    double dbInterest = Double.parseDouble((String) pConfigParams.get(strPattern + "interest"));
                    pInterest.put("value", dbInterest);
                }
                else if (pInterestType == InterestType.REFERENCE)
                {
                    double dbInterestBase = Double.parseDouble((String) pConfigParams.get(strPattern + "interestBase"));
                    double dbInterestDelta = Double.parseDouble(
                            (String) pConfigParams.get(strPattern + "interestDelta"));
                    double dbInterest = dbInterestBase + dbInterestDelta;
                    pInterest.put("interest", dbInterest);
                    pInterest.put("base", dbInterestBase);
                    pInterest.put("baseName", pConfigParams.get(strPattern + "interestBaseName"));
                    pInterest.put("delta", dbInterestDelta);
                }
                /* se calcula el objeto plazo */
                if (pConfigParams.get(strPattern + "term") != null)
                {
                    pResult.put("term", Integer.parseInt((String) pConfigParams.get(strPattern + "term")));
                }
                pResult.put("interest", pInterest);
                pResult.put("order", i);
                pReturn.put(pResult);
            }
            else
            {
                /* si ya no hay más plazos configurados */
                break;
            }
        }
        return pReturn;
    }

    private JSONObject getTerms() throws JSONException
    {
        int nTermMax;
        int nTermMin;
        int nTermDefault;
        JSONObject pReturn = new JSONObject();
        nTermMax = Integer.parseInt((String) pConfigParams.get("termMax"));
        nTermMin = Integer.parseInt((String) pConfigParams.get("termMin"));
        nTermDefault = Integer.parseInt((String) pConfigParams.get("termDefault"));
        pReturn.put("max", nTermMax);
        pReturn.put("min", nTermMin);
        pReturn.put("default", nTermDefault);
        return pReturn;
    }

    private JSONObject getFees() throws JSONException
    {
        JSONObject pReturn = new JSONObject();
        JSONObject pOpeningFee = new JSONObject();
        JSONObject pOperatingFee = new JSONObject();
        double dbOpeningFix;
        double dbOpeningPercent;
        double dbOpeningMax;
        double dbOpeningMin;
        double dbOperatingFix;
        double dbOperatingPercent;
        double dbOperatingMax;
        double dbOperatingMin;
        dbOpeningFix = Double.parseDouble((String) pConfigParams.get("openingFeeFix"));
        dbOpeningPercent = Double.parseDouble((String) pConfigParams.get("openingFeePercent"));
        dbOpeningMax = Double.parseDouble((String) pConfigParams.get("openingFeeMax"));
        dbOpeningMin = Double.parseDouble((String) pConfigParams.get("openingFeeMin"));
        dbOperatingFix = Double.parseDouble((String) pConfigParams.get("operatingFeeFix"));
        dbOperatingPercent = Double.parseDouble((String) pConfigParams.get("operatingFeePercent"));
        dbOperatingMax = Double.parseDouble((String) pConfigParams.get("operatingFeeMax"));
        dbOperatingMin = Double.parseDouble((String) pConfigParams.get("operatingFeeMin"));
        pOpeningFee.put("fix", dbOpeningFix);
        pOpeningFee.put("percent", dbOpeningPercent);
        pOpeningFee.put("max", dbOpeningMax);
        pOpeningFee.put("min", dbOpeningMin);
        pReturn.put("opening", pOpeningFee);
        pOperatingFee.put("fix", dbOperatingFix);
        pOperatingFee.put("percent", dbOperatingPercent);
        pOperatingFee.put("max", dbOperatingMax);
        pOperatingFee.put("min", dbOperatingMin);
        pReturn.put("operation", pOperatingFee);
        return pReturn;
    }
}
