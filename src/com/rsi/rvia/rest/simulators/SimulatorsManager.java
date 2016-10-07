package com.rsi.rvia.rest.simulators;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.error.exceptions.ApplicationException;
import com.rsi.rvia.rest.error.exceptions.LogicalErrorException;
import com.rsi.rvia.rest.tool.Utils;

public class SimulatorsManager
{
	private static Logger		pLog	= LoggerFactory.getLogger(SimulatorsManager.class);
	private static JSONObject	pPropNRBENames;

	/**
	 * Obtiene la información de simuladores de la entidad
	 * 
	 * @param strSimulatorName
	 *           Nombre del siumulador especifico que se queire recuperar, si se pasa null se obtieen todos los
	 *           disponibles para la entidad
	 * @param strNRBE
	 *           Codio de entidad con 4 digitos
	 * @return Datos de la configuración
	 * @throws Exception
	 */
	public static SimulatorObjectArray getSimulatorsFromDDBB(String strNRBE, String strEntityName,
			String strSimulatorName) throws Exception
	{
		SimulatorObjectArray alReturn = new SimulatorObjectArray();
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		try
		{
			String strQuery = "select * from BDPTB235_SIMULADORES s, BDPTB236_PARAM_SIMULADORES p where s.entidad = ? and s.activo = '1' and s.id_simulador=p.id_simulador order by s.id_simulador";
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setString(1, strNRBE);
			pResultSet = pPreparedStatement.executeQuery();
			/* Recupera los parametros de configuración */
			int nSimulatorIdRef = -1;
			int nSimulatorId;
			SimulatorObject pSimulatorObject = null;
			while (pResultSet.next())
			{
				nSimulatorId = pResultSet.getInt("ID_SIMULADOR");
				/* se comprueba si el registo pertenece a un simulador no evaluado todavia */
				if (nSimulatorIdRef != nSimulatorId)
				{
					nSimulatorIdRef = nSimulatorId;
					/*
					 * si el obtejo simulador esta vacio siginifa que el la primera iteración, si no lo esta es un objeto
					 * anterior y es necsario guardarlo en el arraylist de resultado
					 */
					if (pSimulatorObject != null)
					{
						alReturn.addSimulator(pSimulatorObject);
					}
					/* se crea el nuevo sinulador y posteriormente se añaden sus campos */
					pSimulatorObject = new SimulatorObject(nSimulatorId, pResultSet.getString("ENTIDAD"), strEntityName, pResultSet.getString("CATEGORIA"), pResultSet.getString("NOMBRE_COMERCIAL"), pResultSet.getString("TIPO_CALCULO"), pResultSet.getBoolean("ACTIVO"), pResultSet.getBoolean("CONTRATAR"), pResultSet.getBoolean("CONTACTO_EMAIL"), pResultSet.getBoolean("CONTACTO_TELEF"));
				}
				pSimulatorObject.pConfigParams.put(pResultSet.getString("CLAVE"), pResultSet.getString("VALOR"));
			}
			/* se añade el último elemento si existe al menos uno */
			if (pSimulatorObject != null)
			{
				alReturn.addSimulator(pSimulatorObject);
			}
		}
		catch (Exception ex)
		{
			pLog.error("Error al cargar la configuración de algoritmos para la entidad " + strNRBE);
			throw ex;
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
		}
		// if (alReturn.isEmpty())
		// throw new LogicalErrorException(400, 99988, "Error al procesar la petición",
		// "No se ha encontrado configuración de simuladores para esta entidad", null);
		return alReturn;
	}

	public static String getNRBEFromBankName(String strBankName) throws Exception
	{
		String strReturn;
		/* se carga el fichero de propiedades que contien la resolución de nombres */
		if (pPropNRBENames == null)
		{
			String strJsonContent;
			try
			{
				InputStream pInputStream = (SimulatorsManager.class.getResourceAsStream("/NRBE.properties"));
				strJsonContent = Utils.getStringFromInputStream(pInputStream);
				pPropNRBENames = new JSONObject(strJsonContent);
			}
			catch (Exception ex)
			{
				throw new ApplicationException(500, 9999, "Se ha producido un error interno de la aplicación", "No ha sido posible recuperar la información necesaria para la entidad", ex);
			}
		}
		/* se obtiene el codigo de entidad para el nombre dado */
		JSONObject pDataObject = pPropNRBENames.getJSONObject(strBankName);
		if (pDataObject == null)
		{
			throw new LogicalErrorException(400, 9998, "No se ha encontrado información para esta entidad", "No ha sido posible recuperar la información necesaria para esta entidad", null);
		}
		strReturn = pDataObject.getString("NRBE");
		if (strReturn == null || strReturn.trim().isEmpty())
		{
			throw new LogicalErrorException(400, 9997, "No se ha encontrado información para esta entidad", "No ha sido posible recuperar la información necesaria para esta entidad", null);
		}
		while (strReturn.length() < 4)
			strReturn = "0" + strReturn;
		return strReturn;
	}
}
