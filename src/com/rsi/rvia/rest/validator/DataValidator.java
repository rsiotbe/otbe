package com.rsi.rvia.rest.validator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;

/** Clase destinada a las validaciones de datos censados en la tabla bdptb228 */
public class DataValidator
{
	private static Logger	pLog	= LoggerFactory.getLogger(DataValidator.class);

	public static ArrayList<MiqValidation> getDDBBValidations(String strPathRest) throws Exception
	{
		ArrayList<MiqValidation> alReturn = new ArrayList<MiqValidation>();
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		try
		{
			String strQuery = "select * from " + " bel.bdptb222_miq_quests z," + " bel.bdptb226_miq_quest_rl_session x,"
					+ " bel.bdptb225_miq_session_params a," + " BEL.BDPTB228_MIQ_PARAM_VALIDATION b"
					+ " where z.path_rest=? and z.id_miq=x.id_miq" + " and x.id_miq_param= a.id_miq_param"
					+ " and a.id_miq_param=b.id_miq_param";
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setString(1, strPathRest);
			pResultSet = pPreparedStatement.executeQuery();
			while (pResultSet.next())
			{
				MiqValidation pMiqValidation = new MiqValidation(pResultSet.getInt("paramlong"), pResultSet.getString("parammin"), pResultSet.getString("parammax"), pResultSet.getString("parammask"), pResultSet.getString("paramdatatype"), pResultSet.getString("paramname"), pResultSet.getString("aliasname"));
				alReturn.add(pMiqValidation);
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al realizar la consulta a la BBDD.");
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
		}
		return alReturn;
	}

	/**
	 * Realiza una validación de los datos que recibe en un Hashtable, siguiendo el un criterio censado en la tabla
	 * bdptb228_miq_param_validation y el pathRest - Para añadir nuevas validaciones crear un nuevo metodo
	 * validate"BLaBLa"() y añadirlo como caso en la función validation.
	 * 
	 * @param strPathRest
	 * @param htParams
	 * @return
	 * @throws Exception
	 */
	public static String validation(String strPathRest, Hashtable<String, String> htParams) throws Exception
	{
		boolean fCheck = true;
		String strReturn = "{}";
		ArrayList<MiqValidation> alMiqValidations = getDDBBValidations(strPathRest);
		JSONObject pJsonError = new JSONObject();
		for (MiqValidation pMiqValidation : alMiqValidations)
		{
			pJsonError.put(pMiqValidation.getParamName(), pMiqValidation.getJson());
			if (fCheck)
			{
				switch (pMiqValidation.getParamDataType())
				{
					case "Date":
						String strValue = htParams.get(pMiqValidation.getParamName());
						if (strValue == null)
						{
							strValue = htParams.get(pMiqValidation.getParamAliasName());
						}
						pLog.info("Validan Date: " + strValue);
						if (!validateDate(strValue, pMiqValidation.getParamMask()))
						{
							fCheck = false;
						}
						else
						{
							pLog.trace("Validacion Date Correcta.");
						}
						break;
					case "Integer":
						strValue = htParams.get(pMiqValidation.getParamName());
						if (strValue == null)
						{
							strValue = htParams.get(pMiqValidation.getParamAliasName());
						}
						pLog.info("Validan Integer: " + strValue);
						if (!validateInteger(strValue, pMiqValidation.getParamMin(), pMiqValidation.getParamMax()))
						{
							fCheck = false;
						}
						else
						{
							pLog.trace("Validacion Integer Correcta.");
						}
						break;
					case "Entidad":
						strValue = htParams.get(pMiqValidation.getParamName());
						if (strValue == null)
						{
							strValue = htParams.get(pMiqValidation.getParamAliasName());
						}
						pLog.info("Validan Entida: " + strValue);
						if (!validateDDBBEntidad(strValue))
						{
							fCheck = false;
						}
						else
						{
							pLog.trace("Validacion Entidad Correcta.");
						}
						break;
					case "String":
						strValue = htParams.get(pMiqValidation.getParamName());
						if (strValue == null)
						{
							strValue = htParams.get(pMiqValidation.getParamAliasName());
						}
						pLog.info("Validan String: " + strValue);
						if (!validateString(strValue, pMiqValidation.getParamMin(), pMiqValidation.getParamMax(), pMiqValidation.getParamLong()))
						{
							fCheck = false;
						}
						else
						{
							pLog.trace("Validacion String Correcta.");
						}
						break;
					default:
						break;
				}
			}
		}
		// Si fCheck es falso ha dado un error en algun lado.
		if (!fCheck)
		{
			pLog.info("Validacion fallida. Devolviendo JSON con informaci�n de los campos.");
			strReturn = pJsonError.toString();
		}
		return strReturn;
	}

	/**
	 * Validación de una fecha dato una mascara
	 * 
	 * @param strValue
	 *           Fecha
	 * @param strMask
	 *           Formato en forma de mascara
	 * @return True si la validación es positiva, false si es negativa
	 */
	private static boolean validateDate(String strValue, String strMask)
	{
		boolean fReturn = true;
		SimpleDateFormat pDateFormat = new SimpleDateFormat(strMask);
		if ((strValue == null) || (strValue.trim().isEmpty()))
		{
			fReturn = false;
		}
		try
		{
			pDateFormat.parse(strValue);
		}
		catch (Exception ex)
		{
			fReturn = false;
		}
		return fReturn;
	}

	/**
	 * Validación de un entero segun su longitud, máximo y mínimo.
	 * 
	 * @param strValue
	 *           entero en formato de String
	 * @param nMin
	 *           mínimo posible para el entero.
	 * @param nMax
	 *           máximo posible para el entero.
	 * @param nLong
	 *           longitud máxima para el entero
	 * @return True si la validación es positiva, false si es negativa
	 */
	private static boolean validateInteger(String strValue, int nMin, int nMax)
	{
		boolean fReturn = true;
		try
		{
			int nValue = Integer.parseInt(strValue);
			if ((nMin != -1))
			{
				if (nValue <= nMin)
				{
					fReturn = false;
				}
			}
			if (nMax > 0)
			{
				if (nValue >= nMax)
				{
					fReturn = false;
				}
			}
		}
		catch (Exception ex)
		{
			fReturn = false;
		}
		return fReturn;
	}

	/**
	 * Valida si una entidad existe. Para ello ejecuta una query contra el CIP-
	 * 
	 * @param strValue
	 *           entidad a consultar
	 * @return True si la validación es positiva, false si es negativa
	 * @throws Exception
	 */
	private static boolean validateDDBBEntidad(String strValue) throws Exception
	{
		boolean fReturn = true;
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		try
		{
			String strQuery = "select cod_nrbe_en from prox01.sx_entidad " + " where cod_nrbe_en=?";
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleCIP);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setString(1, strValue);
			pResultSet = pPreparedStatement.executeQuery();
			if (!pResultSet.next())
			{
				fReturn = false;
			}
		}
		catch (Exception ex)
		{
			fReturn = false;
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
		}
		return fReturn;
	}

	/**
	 * Validación de un string segun su longitud, máximo y mínimo.
	 * 
	 * @param strValue
	 *           string entrante
	 * @param nMin
	 *           mínimo número de caracteres
	 * @param nMax
	 *           máximo número de caracteres
	 * @param nLong
	 *           número fijo de caracteres
	 * @return True si la validación es positiva, false si es negativa
	 */
	private static boolean validateString(String strValue, int nMin, int nMax, int nLong)
	{
		boolean fReturn = true;
		try
		{
			if (nLong != 0)
			{
				if ((strValue.length() <= nMin) || (strValue.length() >= nMax) || (strValue.length() != nLong))
				{
					fReturn = false;
				}
			}
		}
		catch (Exception ex)
		{
			fReturn = false;
			return fReturn;
		}
		return fReturn;
	}
}
