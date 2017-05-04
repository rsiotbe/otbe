package com.rsi.rvia.rest.client.DDBB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;

import com.rsi.Constants.Language;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.security.IdentityProviderFactory;
import com.rsi.rvia.rest.tool.Utils;
import com.rsi.rvia.rest.response.RviaRestResponseErrorItem;
import com.rsi.rvia.rest.response.RviaRestResponse;
import com.rsi.rvia.rest.response.RviaRestResponse.Type;
import com.rsi.rvia.rest.endpoint.simulators.CardObject;

public class CardsDDBB {

    private static Logger pLog = LoggerFactory.getLogger(CardsDDBB.class);
    private static final String APLICACION = "BDP";
    private static final String CANAL = "10";

	public static RviaRestResponse getAllCards(String strIdNRBE, Language pLanguage, String strNRBE) throws Exception 
	{
		JSONObject jsonCards = null;
		String strJson = "";
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		RviaRestResponse pRviaRestResponse = null;
		try
		{
			String strQuery = "SELECT DISTINCT NOMB_TRFA_PDV, ID_TARJETA FROM BEL.BDPTB283_TARJETAS " + 
							"WHERE COD_NRBE_EN = ? AND FECHA_COMRCLCN < ? " + 
							"ORDER BY ID_TARJETA";
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pPreparedStatement.setString(1, strIdNRBE);
            pPreparedStatement.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            pResultSet = pPreparedStatement.executeQuery();
            while (pResultSet.next())
            {
            	if (strJson.length() > 0)
            		strJson += ",";
            	strJson += "{\"name\" : \"" + pResultSet.getString(1) + "\",";
            	strJson += "\"id\" : \"" + pResultSet.getString(2) + "\"}";
            }
            strJson = "{\"nrbe\" : \"" + strIdNRBE + "\", \"nrbeName\" : \""+ strNRBE + "\", \"active\" : true, \"tarjetas\" : [" + strJson + "]}";
            pLog.info("json respuesta:" + strJson);
            jsonCards = new JSONObject(strJson);
            pRviaRestResponse = new RviaRestResponse(Type.OK, jsonCards, null);
		}
		catch (SQLException ex)
		{
            pLog.error("Error al realizar la consulta a la BBDD. Trace: \n\n\t" + ex.getMessage());
            /*RviaRestResponseErrorItem pRviaRestResponseErrorItem = new RviaRestResponseErrorItem("999", ex.getMessage());
            pRviaRestResponse = new RviaRestResponse(RviaRestResponse.Type.ERROR, null, pRviaRestResponseErrorItem);*/
            throw ex;
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
			
		}
		return pRviaRestResponse;		
	}
	
	public static CardObject getSimulatorParameters(String strIdCard) throws Exception
	{
		Connection pConnection = null;
		PreparedStatement pPreparedStatement = null;
		ResultSet pResultSet = null;
		RviaRestResponse pRviaRestResponse = null;
		CardObject oCard = new CardObject();
		
		try
		{
			oCard.setStrTarjeta(strIdCard);
			oCard.setStrCODApl(APLICACION);
			oCard.setStrCODCanal(CANAL);
			oCard.setStrCODTerminal("");
			oCard.setStrCODSecTrans("");
			oCard.setStrCODSecUser("");
			String strQuery = "SELECT COD_NRBE_EN, LPAD(COD_LINEA, 2, '0'), ID_GRP_PD, ID_PDV, ID_TRFA_PDV " +
			                  "FROM BEL.BDPTB283_TARJETAS " + 
							  "WHERE ID_TARJETA = ? ";
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pPreparedStatement.setString(1, oCard.getStrTarjeta());
            pResultSet = pPreparedStatement.executeQuery();
            while (pResultSet.next())
            {
            	//oCard.setStrNRBE(pResultSet.getString(1));
            	oCard.setStrCODSecEnt(pResultSet.getString(1));
            	oCard.setStrLinea(pResultSet.getString(2));
            	oCard.setStrGrProducto(pResultSet.getString(3));
            	oCard.setStrProducto(pResultSet.getString(4));
            	oCard.setStrTarifa(pResultSet.getString(5));
            	/**/
            	//oCard.setStrLinea("01");
            	/**/
            }
            pLog.info("respuesta:" + oCard.toString());
            strQuery = "SELECT VALOR FROM BEL.BELTS067 WHERE CLAVE = ? AND SECCION = 'IP_ENTIDAD'";
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pPreparedStatement.setString(1, oCard.getStrCODSecEnt());
            pResultSet = pPreparedStatement.executeQuery();
            while (pResultSet.next())
            	oCard.setStrCODSecIp(pResultSet.getString(1));
            pLog.info("respuesta:" + oCard.toString());
		}
		catch (SQLException ex)
		{
            pLog.error("Error al realizar la consulta a la BBDD. Trace: \n\n\t" + ex.getMessage());
            /*RviaRestResponseErrorItem pRviaRestResponseErrorItem = new RviaRestResponseErrorItem("999", ex.getMessage());
            pRviaRestResponse = new RviaRestResponse(RviaRestResponse.Type.ERROR, null, pRviaRestResponseErrorItem);*/
            throw ex;
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
			
		}
		return oCard;		
	
	}
}
