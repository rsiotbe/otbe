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
		OracleBanca, MySql, OracleCIP;
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
				case OracleBanca:
					pReturn = BancaOracleDDBB.getInstance();
					break;
				case OracleCIP:
					pReturn = CIPOracleDDBB.getInstance();
					break;
				case MySql:
					pReturn = MySqlDDBB.getInstance();
					break;
			}
		}
		return pReturn;
	}
}
