package com.rsi.rvia.rest.DDBB;

/**
 * Clase que gestiona la solicitud de una instancia de base de datos
 *
 */
public class DDBBFactory
{
	/**
	 * Enumeraci�nq ue contiene los diferentes tipos de BBDD implementados en la aplicaci�n
	 *
	 */
	public enum DDBBProvider
	{
		Oracle, MySql, OracleBDES, OracleBTEST, OracleCIP;
	}

	/**
	 * Obtiene la clase que gestiona la conexi�n con base de datos
	 * @param pDDBBProvider Tipo de base de datos a instanciar
	 * @return Conexi�n con la base de datos seleccionar
	 */
	public static DDBBConnection getDDBB(DDBBProvider pDDBBProvider)
	{
		DDBBConnection pReturn = null;
		if (pDDBBProvider != null)
		{
			switch (pDDBBProvider)
			{
				case Oracle:
					pReturn = OracleDDBB.getInstance();
					break;
				case OracleBDES:
					pReturn = OracleDDBBBDES.getInstance();
					break;
				case OracleBTEST:
					pReturn = OracleDDBBBTEST.getInstance();
					break;
				case OracleCIP:
					pReturn = OracleDDBBCIP.getInstance();
					break;
				case MySql:
					pReturn = MySqlDDBB.getInstance();
					break;
			}
		}
		return pReturn;
	}
	public static DDBBConnection getDDBB(DDBBProvider pDDBBProvider, String prefix)
	{
		DDBBConnection pReturn = null;
		if (pDDBBProvider != null)
		{
			switch (pDDBBProvider)
			{
				case Oracle:
					pReturn = OracleDDBB.getInstance(prefix);
					break;
				case OracleBDES:
					pReturn = OracleDDBBBDES.getInstance(prefix);
					break;
				case OracleBTEST:
					pReturn = OracleDDBBBTEST.getInstance(prefix);
					break;
				case OracleCIP:
					pReturn = OracleDDBBCIP.getInstance(prefix);
					break;
				case MySql:
					pReturn = MySqlDDBB.getInstance(prefix);
					break;
			}
		}
		return pReturn;
	}		
}
