package com.rsi;

/**
 * Constantes usadas en toda la aplicación.
 * 
 * @author RSI
 */
public class Constantes
{
	// /////////////////////////
	// Default Values
	// /////////////////////////
	public static final String	CODIGO_BANCO_COOPERATIVO_ESPANOL	= "0198";
	public static final String	DEFAULT_LANGUAGE						= "es_ES";
	// /////////////////////////
	// Campos JSON Simulador
	// /////////////////////////
	public static final String	SIMULADOR_NRBE							= "nrbe";
	public static final String	SIMULADOR_NRBE_NAME					= "nrbeName";
	public static final String	SIMULADOR_SIMPLE_NAME				= "simpleName";
	public static final String	SIMULADOR_COMERCIAL_NAME			= "comercialName";
	public static final String	SIMULADOR_SAC_EMAIL					= "sacEmail";
	public static final String	SIMULADOR_SAC_TELEPHONE				= "sacTelephone";
	public static final String	SIMULADOR_OFICINA_EMAIL				= "oficinaEmail";
	public static final String	SIMULADOR_CATEGORY					= "category";
	public static final String	SIMULADOR_CALC_TYPE					= "calcType";
	public static final String	SIMULADOR_ACTIVE						= "fIsActive";
	public static final String	SIMULADOR_ALLOW_BOOKING				= "allowBooking";
	public static final String	SIMULADOR_ALLOW_USER_EMAIL			= "allowUserEmail";
	public static final String	SIMULADOR_ALLOW_USER_TELEPHONE	= "allowUserTelephone";
	public static final String	SIMULADOR_LOPD							= "lopd";
	public static final String	SIMULADOR_DISCLAIMER					= "disclaimer";
	public static final String	SIMULADOR_CONTRACT_CONDITIONS		= "contractConditions";
	public static final String	SIMULADOR_DESCRIPTION				= "description";
	public static final String	SIMULADOR_LANGUAGE					= "language";
	public static final String	SIMULADOR_TYPE							= "simulatorType";
	// /////////////////////////
	// Request params
	// /////////////////////////
	public static final String	PARAM_LANG								= "lang";
	public static final String	PARAM_NRBE								= "NRBE";

	public enum SimulatorLoanCategory
	{
		AGRICOLA, AUTOMOVIL, ESTUDIOS, FINANZAS, VIVIENDA, JOVEN, PERSONAL, SANIDAD
	}

	public enum SimulatorMortgageCategory
	{
		HIPOTECA
	}

	public enum SimulatorType
	{
		PERSONAL, MORTGAGE
	}
}
