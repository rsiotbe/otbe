package com.rsi.rvia.rest.simulators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import com.rsi.Constants;
import com.rsi.TestBase;

public class SimulatorsManagerTest extends TestBase
{
	@Test
	public void testGetSimulatorsDataByName() throws Exception
	{
		final String NRBE = Constants.CODIGO_BANCO_COOPERATIVO_ESPANOL;
		final String NRBE_NAME = "Entidad";
		final String SIMULATOR_NAME = "coche";
		final String LANGUAGE = Constants.DEFAULT_LANGUAGE;
		SimulatorConfigObjectArray list = SimulatorsManager.getSimulatorsData(null, NRBE, NRBE_NAME, SIMULATOR_NAME, LANGUAGE);
		assertNotNull("La lista de simuladores es null", list);
		assertTrue("La lista de simuladores está vacía", !list.isEmpty());
		JSONObject json = list.toJson();
		JSONArray simuladores = json.getJSONArray("loans");
		assertEquals("El nombre simple del simulador no es correcto", SIMULATOR_NAME, ((JSONObject) simuladores.get(0)).optString("simpleName"));
	}

	@Test
	public void testGetNRBEFromBankName() throws Exception
	{
		final String NAME = "bancocooperativo";
		final String NRBE = SimulatorsManager.getNRBEFromName(NAME);
		assertEquals("El NRBE del banco no es correcto", Constants.CODIGO_BANCO_COOPERATIVO_ESPANOL, NRBE);
	}
}
