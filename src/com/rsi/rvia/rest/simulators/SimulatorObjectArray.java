package com.rsi.rvia.rest.simulators;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SimulatorObjectArray
{
	private ArrayList<SimulatorObject>	alSimulators;

	/**
	 * Constructor
	 * 
	 * @param alSimulators
	 */
	public SimulatorObjectArray()
	{
		this.alSimulators = new ArrayList<SimulatorObject>();
	}

	/**
	 * añade un nuevo simulador al array
	 * 
	 * @param pSimulatorObject
	 */
	public void addSimulator(SimulatorObject pSimulatorObject)
	{
		alSimulators.add(pSimulatorObject);
	}

	/**
	 * Indica si el array de simuladores está vacío
	 * 
	 * @return
	 */
	public boolean isEmpty()
	{
		return (alSimulators.size() == 0);
	}

	/**
	 * Genera una objeto JSON a aprtir del array de simuladores
	 * 
	 * @return Objeto JSONObject
	 * @throws JSONException
	 */
	public JSONObject toJson() throws JSONException
	{
		JSONObject pReturn = new JSONObject();
		JSONArray pArray = new JSONArray();
		/* se recoren los simuladores para formar un array */
		for (int i = 0; i < alSimulators.size(); i++)
		{
			pArray.put(alSimulators.get(i).toJson());
		}
		pReturn.put("loans", pArray);
		return pReturn;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		return alSimulators.toString();
	}
}
