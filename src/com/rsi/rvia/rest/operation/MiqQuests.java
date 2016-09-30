package com.rsi.rvia.rest.operation;

import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import javax.ws.rs.core.UriBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;

/** Objeto que representa una operativa o operación definida en la aplicación */
public class MiqQuests
{
	private static Logger								pLog			= LoggerFactory.getLogger(MiqQuests.class);
	private int												nIdMiq;
	private String											strPathRest;
	private String											strComponentType;
	private String											strEndPoint;
	private String											strTemplate;
	public static Hashtable<Integer, MiqQuests>	htCacheData	= new Hashtable<Integer, MiqQuests>();

	/**
	 * Devuelve el tamaño de la cache
	 * 
	 * @return int con el tamaño de la cache
	 */
	public static int getSizeCache()
	{
		int nReturn = 0;
		if (htCacheData != null)
		{
			nReturn = htCacheData.size();
		}
		return nReturn;
	}

	/**
	 * Reinicia la Cache
	 */
	public static void restartCache()
	{
		if (htCacheData != null)
		{
			htCacheData = new Hashtable<Integer, MiqQuests>();
		}
	}

	public int getIdMiq()
	{
		return nIdMiq;
	}

	public void setIdMiq(int nIdMiq)
	{
		this.nIdMiq = nIdMiq;
	}

	public String getPathRest()
	{
		return strPathRest;
	}

	public void setPathRest(String strPathRest)
	{
		this.strPathRest = strPathRest;
	}

	public String getComponentType()
	{
		return strComponentType;
	}

	public void setComponentType(String strComponentType)
	{
		this.strComponentType = strComponentType;
	}

	public String getEndPoint()
	{
		return strEndPoint;
	}

	public void setEndPoint(String strEndPoint)
	{
		this.strEndPoint = strEndPoint;
	}

	public String getTemplate()
	{
		return strTemplate;
	}

	public void setTemplate(String strTemplate)
	{
		this.strTemplate = strTemplate;
	}

	/**
	 * Obtiene un objeto URI a del valor de EndPoint
	 * 
	 * @param strEndPoint
	 *           String que contiene la uri
	 * @return Objeto URI
	 */
	public URI getBaseWSEndPoint()
	{
		return UriBuilder.fromUri(this.strEndPoint).build();
	}

	/** Contructor generico */
	public MiqQuests()
	{
	}

	/**
	 * Contructor con parámetros
	 * 
	 * @param nIdMiq
	 *           Identificador de operación
	 * @param strComponentType
	 *           Tipo de componente
	 * @param strEndPoint
	 *           Dirección endPoint
	 * @param strTemplate
	 *           Plantilla asociada
	 */
	public MiqQuests(int nIdMiq, String strPathRest, String strComponentType, String strEndPoint, String strTemplate)
	{
		this.nIdMiq = nIdMiq;
		this.strPathRest = strPathRest;
		this.strComponentType = strComponentType;
		this.strEndPoint = strEndPoint;
		this.strTemplate = strTemplate;
	}

	/**
	 * Funcion que carga la cache desde base de datos
	 * 
	 * @throws Exception
	 */
	private static void loadDDBBCache() throws Exception
	{
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		try
		{
			String strQuery = "SELECT * from bel.bdptb222_miq_quests";
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pResultSet = pPreparedStatement.executeQuery();
			while (pResultSet.next())
			{
				MiqQuests pMiqQuests = new MiqQuests(pResultSet.getInt("id_miq"), pResultSet.getString("path_rest"), pResultSet.getString("component_type"), pResultSet.getString("end_point"), pResultSet.getString("miq_out_template"));
				if (!htCacheData.containsKey(pResultSet.getInt("id_miq")))
					htCacheData.put(pResultSet.getInt("id_miq"), pMiqQuests);
			}
			pLog.debug("Se carga la cache de MiqQuest con " + getSizeCache() + " elementos");
		}
		catch (Exception ex)
		{
			pLog.error("Error al realizar la consulta a la BBDD.");
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuilder pSb = new StringBuilder();
		pSb.append("IdMiq         :" + nIdMiq + "\n");
		pSb.append("PathRest :" + strPathRest + "\n");
		pSb.append("ComponentType :" + strComponentType + "\n");
		pSb.append("EndPoint      :" + strEndPoint + "\n");
		pSb.append("Template      :" + strTemplate);
		return pSb.toString();
	}

	/**
	 * Realiza una conexión a la BBDD para obtener los datos necesarios para crear un objeto MiqQuests y darlo como
	 * respuesta.
	 * 
	 * @param strPath
	 *           String path primario para la clausula where de la consulta
	 * @return MiqQuests con el id:miq, el component_type, el end_point y el template.
	 * @throws Exception
	 */
	public static MiqQuests getMiqQuests(String strPath) throws Exception
	{
		MiqQuests pMiqQuests = null;
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		/*
		 * en caso de contener el path la coletilla /help, se elilina para obtener el path original sobre el cual se
		 * pregunta la ayuda
		 */
		strPath = strPath.replace("/help", "");
		try
		{
			String strQuery = "select * from bdptb222_miq_quests where trim(path_rest) =?";
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setString(1, strPath);
			pResultSet = pPreparedStatement.executeQuery();
			while (pResultSet.next())
			{
				pMiqQuests = new MiqQuests(pResultSet.getInt("id_miq"), pResultSet.getString("path_rest"), pResultSet.getString("component_type"), pResultSet.getString("end_point"), pResultSet.getString("miq_out_template"));
			}
		}
		catch (Exception ex)
		{
			pLog.error("error al obtener la informacion de MiqQuest con path: " + strPath, ex);
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
		}
		return pMiqQuests;
	}

	/**
	 * Realiza una conexión a la BBDD para obtener los datos necesarios para crear un objeto MiqQuests y darlo como
	 * respuesta.
	 * 
	 * @param nMiqQuestId
	 *           identificador de la operación
	 * @return MiqQuests con el id:miq, el component_type, el end_point y el template.
	 * @throws Exception
	 */
	public static MiqQuests getMiqQuests(int nMiqQuestId) throws Exception
	{
		MiqQuests pMiqQuests = null;
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		try
		{
			String strQuery = "select * from bdptb222_miq_quests where id_miq = ?";
			pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
			pPreparedStatement = pConnection.prepareStatement(strQuery);
			pPreparedStatement.setInt(1, nMiqQuestId);
			pResultSet = pPreparedStatement.executeQuery();
			while (pResultSet.next())
			{
				pMiqQuests = new MiqQuests(pResultSet.getInt("id_miq"), pResultSet.getString("path_rest"), pResultSet.getString("component_type"), pResultSet.getString("end_point"), pResultSet.getString("miq_out_template"));
			}
		}
		catch (Exception ex)
		{
			pLog.error("error al obtener la informacion de MiqQuest con id: " + nMiqQuestId, ex);
		}
		finally
		{
			DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
		}
		return pMiqQuests;
	}
}
