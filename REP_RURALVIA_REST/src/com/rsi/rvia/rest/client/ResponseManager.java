package com.rsi.rvia.rest.client;

import java.util.Iterator;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.error.ErrorManager;
/**
 * Clase para manejar la respuesta del RestWSConnector. Mira si es un error o si no lo es y compone una respuesta JSON 
 * Siguiendo la siguiente estructura:
 * {
 * 	"error": 0, ó 1 (si tiene o si no tiene)
 * 	"response" : {...} (respuesta en JSON, puede ser el error o la respuesta ya formada)
 * }
 *
 */
public class ResponseManager
{
	private static Logger	pLog	= LoggerFactory.getLogger(ResponseManager.class);
	
	public static String processResponse(String strEntity, int nStatus) throws JSONException{
		String strReturn = "{}";
		JSONObject pJson = new JSONObject();
		if((nStatus != 200) || (ErrorManager.isWebError(strEntity))){
			pLog.info("Respuesta con errores.");
			strReturn = ErrorManager.processError(strEntity, nStatus);
		}else{
			/* Respuesta sin errores */
			pLog.info("Respuesta sin errores.");
			if(!strEntity.trim().isEmpty()){
				JSONObject pJsonReader = new JSONObject(strEntity);

				String strPrimaryKey = "";
				Iterator<String> pKeys = pJsonReader.keys();
				if(pKeys.hasNext() ){
				   strPrimaryKey = (String)pKeys.next();
				}
				
				if(!strPrimaryKey.trim().isEmpty()){
					if("response".equals(strPrimaryKey)){
						strReturn = strEntity;
					}else{
						JSONObject pJsonContent = new JSONObject();
						pJsonContent = pJsonReader.getJSONObject(strPrimaryKey);
						String strStatusResponse = (String) pJsonContent.get("codigoRetorno");
						if("1".equals(strStatusResponse)){
							pJson.put("response", pJsonContent.get("Respuesta"));
							strReturn = pJson.toString();
						}else{
							strReturn = ErrorManager.processError(strEntity, nStatus);
						}
					}
					
					
				}
			}
			
		}
		return strReturn;
	}
}
