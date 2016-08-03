package com.rsi.rvia.rest.DDBB;

/**
 * Clase que gestiona la solicitud de una instancia de base de datos
 *
 */
public class DDBBFactory
{
	/**
	 * Enumeraciónq ue contiene los diferentes tipos de BBDD implementados en la aplicación
	 *
	 */
	public enum DDBBProvider
	{
		Oracle, MySql;
	}

	/**
	 * Obtiene la clase que gestiona la conexión con base de datos
	 * @param pDDBBProvider Tipo de base de datos a instanciar
	 * @return Conexión con la base de datos seleccionar
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
				case MySql:
					pReturn = MySqlDDBB.getInstance();
					break;
			}
		}
		return pReturn;
	}
}
