package com.rsi.rvia.rest.simulators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import com.rsi.Constantes;
import com.rsi.TestBase;

public class SimulatorsManagerTest extends TestBase
{
   @Before
   public void setUp() throws Exception
   {
      super.setUp();
   }

   @Test
   public void testGetSimulatorsDataByName() throws Exception
   {
      final String strNRBE = Constantes.CODIGO_BANCO_COOPERATIVO_ESPANOL;
      final String strSimulatorName = "Coche";
      final String strLanguage = Constantes.DEFAULT_LANGUAGE;
      SimulatorObjectArray list = SimulatorsManager.getSimulatorsData(strNRBE, strSimulatorName, strLanguage);
      assertNotNull("La lista de simuladores es null", list);
      assertTrue("La lista de simuladores está vacía", !list.isEmpty());
      JSONObject json = list.toJson();
      JSONArray simuladores = json.getJSONArray("loans");
      assertEquals("El nombre comercial del simulador no es correcto", strSimulatorName, ((JSONObject) simuladores.get(0)).optString("comercialName"));
   }

   @Test
   public void testGetNRBEFromBankName() throws Exception
   {
      final String name = "bancocooperativo";
      final String nrbe = SimulatorsManager.getNRBEFromBankName(name);
      assertEquals("El NRBE del banco no es correcto", Constantes.CODIGO_BANCO_COOPERATIVO_ESPANOL, nrbe);
   }
}
