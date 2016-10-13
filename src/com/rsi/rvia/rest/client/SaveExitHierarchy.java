package com.rsi.rvia.rest.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;

public class SaveExitHierarchy
{
	private static Logger		pLog	= LoggerFactory.getLogger(SaveExitHierarchy.class);
	private static int			_nIdMiq;
	private static JSONObject	secResponse;
	private static String		_strMethod;

	SaveExitHierarchy()
	{
	}

	/** Inicia el proceso de censo de los campos de salida a partir de la sección response del objeto json de salida
	 * 
	 * @param pJsonData
	 *           Objeto json
	 * @param nIdMiq
	 *           Identidicador del servicio RESTFul
	 * @paarm strMethod Verbo de solicitud
	 * @throws Exception */
	public static void process(JSONObject pJsonData, int nIdMiq, String strMethod) throws Exception
	{
		_strMethod = strMethod;
		_nIdMiq = nIdMiq;
		secResponse = pJsonData.getJSONObject("response");
		analisisRecursivo(secResponse, "");
	}

	/** Realiza un recorrido recursivo sobre un objeto json
	 * 
	 * @param secResponse
	 *           Objeto json
	 * @param toPath
	 *           Nombre totalmente cualificado del campo de salida
	 * @throws Exception */
	private static void analisisRecursivo(JSONObject secResponse, String toPath) throws Exception
	{
		int i;
		String key;
		String strThisPath = toPath;
		Iterator<String> iterator = secResponse.keys();
		while (iterator.hasNext())
		{
			key = (String) iterator.next();
			if (secResponse.optJSONArray(key) != null)
			{ // Es un objeto array de objetos
				toPath = toPath + "." + key;
				for (i = 0; i < secResponse.optJSONArray(key).length(); i++)
				{
					analisisRecursivo(secResponse.optJSONArray(key).getJSONObject(i), toPath);
				}
			}
			else if (secResponse.optJSONObject(key) != null)
			{ // Es un objeto json
				toPath = toPath + "." + key;
				analisisRecursivo(secResponse.optJSONObject(key), toPath);
			}
			else
			{ // Es un campo del objeto
				pLog.info(key + " ------- " + strThisPath);
				save(key, toPath);
			}
		}
	}

	/** Intenta grabar el campo de salida caso de no existir
	 * 
	 * @param key
	 *           Nombre de campo de salida
	 * @param toPath
	 *           Nombre totalmente cualificado del campo de salida */
	private static void save(String key, String toPath)
	{
		pLog.info(toPath + "." + key);
		String strPath = toPath + "." + key;
		String strParamName = key;
		if (!strParamName.trim().isEmpty())
		{
			boolean fExistConfig = existExitFieldsConfigInDDBB(_nIdMiq, strParamName);
			/* si no existe la relación se procede a crarla */
			if (!fExistConfig)
			{
				pLog.trace("Se procede a insertar el campo de salida " + strParamName
						+ " para su censo en la operativa con id " + _nIdMiq);
				/*
				 * si el campo de salida no existe, se comprueba si está definido como campo de salida de culaquier otra
				 * opertativa
				 */
				Integer nIdMiqParam = existExitFieldInDDBB(strParamName);
				if (nIdMiqParam == null)
				{
					/* el campo de salida no está definido en DDBB, se procede a darlo de alta */
					nIdMiqParam = getNextExitFieldId();
					insertNewExitField(nIdMiqParam, strParamName, strPath);
				}
				/* se añade la realación entre el campo de salida y la operación */
				createRelationExitFieldAndOperation(_nIdMiq, nIdMiqParam, strParamName);
			}
		}
	}

	/** Comprueba si un campo de salida ya está realacionado con una operativa
	 * 
	 * @param nIdMiq
	 *           identificador de eoperativa
	 * @param strExitFieldName
	 *           nombre del campo de salida
	 * @return Id del parametro en caso de existir */
	private static boolean existExitFieldsConfigInDDBB(int nIdMiq, String strParamName)
	{
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		boolean fReturn = false;
		String strQuery = "select a.id_miq from  BEL.BDPTB222_MIQ_QUESTS a, "
				+ "BEL.BDPTB233_MIQ_QUEST_RL_EXITS b, BEL.BDPTB232_MIQ_EXITS c "
				+ "where a.id_miq=b.id_miq and b.ID_MIQ_EXIT=c.ID_MIQ_EXIT and a.id_miq=? " + "and c.EXITNAME=?";
		try
		{
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setInt(1, nIdMiq);
			pPreparedStatement.setString(2, strParamName);
			pResultSet = pPreparedStatement.executeQuery();
			if (pResultSet.next())
			{
				if (!fReturn)
				{
					/* el campo de salida existe y está relacionado, se pasa al siguiente campo de salida */
					fReturn = true;
				}
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al obtener la información de la configuración de MiqQuest", ex);
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
		}
		return fReturn;
	}

	/** Comprueba si el campo de salida existe ya dado de alta en las tablas de MiqQuest
	 * 
	 * @param strExitFieldName
	 *           Nombre del campo de salida
	 * @return Identificador del campo de salida */
	private static Integer existExitFieldInDDBB(String strExitFieldName)
	{
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		Integer nReturn = null;
		String strQuery = "select a.ID_MIQ_EXIT from BEL.BDPTB232_MIQ_EXITS a where a.EXITNAME = ?";
		try
		{
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setString(1, strExitFieldName);
			pResultSet = pPreparedStatement.executeQuery();
			if (pResultSet.next())
			{
				nReturn = (Integer) pResultSet.getInt("ID_MIQ_EXIT");
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al obtener el identificador de un campo de salida de MiqQuest", ex);
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
		}
		return nReturn;
	}

	/** Obtiene el siguiente id libre en la tabla de configuración de campo de salidas MiqQuest
	 * 
	 * @return Identificador a utilizar */
	private static Integer getNextExitFieldId()
	{
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		Integer nReturn = null;
		String strQuery = "select nvl((select * from (select ID_MIQ_EXIT from BEL.BDPTB232_MIQ_EXITS order by ID_MIQ_EXIT desc)	where rownum = 1),0) + 1 ID_MIQ_EXIT from dual";
		try
		{
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pResultSet = pPreparedStatement.executeQuery();
			if (pResultSet.next())
			{
				nReturn = (Integer) pResultSet.getInt("ID_MIQ_EXIT");
			}
		}
		catch (Exception ex)
		{
			pLog.error("No se ha podido generar un id de secuencia para el campo ID_MIQ_EXIT de la tabla BEL.BDPTB232_MIQ_EXITS", ex);
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
		}
		return nReturn;
	}

	/** Se crear un nuevo registro de campo de salida de tipo MiqQuest
	 * 
	 * @param nIdMiqExitField
	 * @param strExitFieldName */
	private static void insertNewExitField(int nIdMiqExitField, String strExitFieldName, String strPath)
	{
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		String strQuery = "insert into BEL.BDPTB232_MIQ_EXITS values (?, ?, '', ?)";
		try
		{
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setInt(1, nIdMiqExitField);
			pPreparedStatement.setString(2, strExitFieldName);
			pPreparedStatement.setString(3, "response" + strPath);
			pPreparedStatement.executeUpdate();
		}
		catch (Exception ex)
		{
			pLog.error("No se ha podido insertar el campo de salida " + strExitFieldName + " con id " + nIdMiqExitField, ex);
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, null, pPreparedStatement, pConnection);
		}
	}

	/** Se crear un nuevo registro de relación campo de salida - operativa de tipo MiqQuest
	 * 
	 * @param nIdMiqExitField
	 *           Identificador de la operativa MiqQuest
	 * @param strExitFieldName
	 *           Nombre del campo de salida */
	private static void createRelationExitFieldAndOperation(int nIdMiq, int nIdMiqExitField, String strExitFieldName)
	{
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		String strQuery = "insert into BEL.BDPTB233_MIQ_QUEST_RL_EXITS values(?, ?, ?, ' ')";
		try
		{
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setInt(1, nIdMiq);
			pPreparedStatement.setInt(2, nIdMiqExitField);
			pPreparedStatement.setString(3, _strMethod);
			pPreparedStatement.executeUpdate();
		}
		catch (Exception ex)
		{
			pLog.error("No se ha podido insertar la relación del campo de salida " + strExitFieldName
					+ " con la operativa " + nIdMiqExitField, ex);
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, null, pPreparedStatement, pConnection);
		}
	}
}
