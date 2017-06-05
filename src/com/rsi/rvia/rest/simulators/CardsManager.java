package com.rsi.rvia.rest.simulators;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MultivaluedMap;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants;
import com.rsi.Constants.Language;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.endpoint.simulators.CardObject;
import com.rsi.rvia.rest.session.RequestConfig;
import com.rsi.rvia.rest.tool.BUSHeader;

public class CardsManager
{
    private static Logger      pLog            = LoggerFactory.getLogger(CardsManager.class);
    public static final String KEY_ID          = "id";
    public static final String KEY_NAME        = "name";
    public static final String KEY_NRBE        = "nrbe";
    public static final String KEY_NRBENAME    = "nrbeName";
    public static final String KEY_ACTIVE      = "active";
    public static final String KEY_TARJETAS    = "tarjetas";
    public static final String SERVICE_PUBLIC  = "SimulacionLiquidacionTarjetaPublico";
    public static final String SERVICE_PRIVATE = "SimulacionLiquidacionTarjetaPrivado";

    public static JSONObject getAllCards(String strIdNRBE, Language pLanguage, String strNRBE) throws Exception
    {
        JSONObject jsonCards = new JSONObject();
        JSONArray jsonList = new JSONArray();
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        JSONObject card = null;
        try
        {
            String strQuery = "SELECT DISTINCT NOMB_TRFA_PDV, ID_TARJETA FROM BEL.BDPTB283_TARJETAS "
                    + "WHERE COD_NRBE_EN = ? AND FECHA_COMRCLCN < ? " + "ORDER BY ID_TARJETA";
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pPreparedStatement.setString(1, strIdNRBE);
            pPreparedStatement.setDate(2, new java.sql.Date(System.currentTimeMillis()));
            pResultSet = pPreparedStatement.executeQuery();
            while (pResultSet.next())
            {
                card = new JSONObject();
                card.put(KEY_NAME, pResultSet.getString(1));
                card.put(KEY_ID, pResultSet.getString(2));
                jsonList.put(card);
            }
            jsonCards.put(KEY_NRBE, strIdNRBE);
            jsonCards.put(KEY_NRBENAME, strNRBE);
            jsonCards.put(KEY_ACTIVE, Boolean.TRUE);
            jsonCards.put(KEY_TARJETAS, jsonList);
            pLog.info("json respuesta:" + jsonCards.toString(2));
        }
        catch (SQLException ex)
        {
            pLog.error("Error al realizar la consulta a la BBDD. Trace: \n\n\t" + ex.getMessage());
            throw ex;
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
        }
        return jsonCards;
    }

    public static JSONObject getCardDetails(HttpServletRequest pRequest) throws Exception
    {
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        JSONObject pCardDetails = null;
        CardObject pCard = new CardObject();
        String strIdCard = (String) pRequest.getParameter(Constants.PARAM_CARD_ID);
        // JSONObject pOptions = new JSONObject(
        // URLDecoder.decode(pRequest.getParameter(Constants.PARAM_OPTIONS), "UTF-8"));
        try
        {
            pCard.setStrTarjeta(strIdCard);
            String strQuery = "SELECT COD_NRBE_EN, LPAD(COD_LINEA, 2, '0'), ID_GRP_PD, ID_PDV, ID_TRFA_PDV "
                    + "FROM BEL.BDPTB283_TARJETAS " + "WHERE ID_TARJETA = ? ";
            pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
            pPreparedStatement = pConnection.prepareStatement(strQuery);
            pPreparedStatement.setString(1, pCard.getStrTarjeta());
            pResultSet = pPreparedStatement.executeQuery();
            while (pResultSet.next())
            {
                pCard.setStrCodEntidad(pResultSet.getString(1));
                pCard.setStrLinea(pResultSet.getString(2));
                pCard.setStrGrProducto(pResultSet.getString(3));
                pCard.setStrProducto(pResultSet.getString(4));
                pCard.setStrTarifa(pResultSet.getString(5));
            }
            // Servicio REST
            String srtJson = getServiceDetails(pRequest, pCard);
            if (srtJson != null)
            {
                JSONObject dataProcess = new JSONObject(srtJson);
                pCardDetails = dataProcess.getJSONObject("EE_O_SimulacionLiquidacionTarjetaPublico").getJSONObject(
                        "Respuesta");
                pLog.info("Respuesta servicio:" + pCardDetails);
            }
        }
        catch (SQLException ex)
        {
            pLog.error("Error al realizar la consulta a la BBDD. Trace: \n\n\t" + ex.getMessage());
            throw ex;
        }
        finally
        {
            DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
        }
        return pCardDetails;
    }

    private static String getServiceDetails(HttpServletRequest pRequest, CardObject pCard) throws Exception
    {
        String strResponse = null;
        // Recuperamos la URL del servicio.
        Properties pProperties = new Properties();
        pProperties.load(CardsManager.class.getResourceAsStream("/services.properties"));
        String strServiceUrl = (String) pProperties.get(SERVICE_PUBLIC);
        // Creamos la URL con parámetros.
        URIBuilder builder = new URIBuilder(strServiceUrl);
        builder.setParameter(CardObject.ATTR_CODIGO_ENTIDAD, pCard.getStrCodEntidad()).setParameter(
                CardObject.ATTR_CODIGO_LINEA, pCard.getStrLinea()).setParameter(CardObject.ATTR_GRUPO_PRODUCTO,
                        pCard.getStrGrProducto()).setParameter(CardObject.ATTR_PRODUCTO,
                                pCard.getStrProducto()).setParameter(CardObject.ATTR_TARIFA, pCard.getStrTarifa());
        HttpGet httpGet = new HttpGet(builder.build());
        HttpClient httpClient = HttpClientBuilder.create().build();
        // Creamos el RequestConfig necesario para atacar al Bus.
        JSONObject pDataRequestConfig = new JSONObject();
        pDataRequestConfig.put(Constants.PARAM_NRBE, pCard.getStrCodEntidad());
        RequestConfig pRequestConfig = new RequestConfig(pRequest, pDataRequestConfig.toString());
        // Se recuperan los headers necesarios para el Bus.
        MultivaluedMap<String, Object> headers = BUSHeader.getHeaders(pRequest, pRequestConfig);
        Iterator<String> it = headers.keySet().iterator();
        while (it.hasNext())
        {
            String key = (String) it.next();
            httpGet.addHeader(key, headers.getFirst(key).toString());
        }
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (entity != null)
        {
            strResponse = EntityUtils.toString(entity);
        }
        return strResponse;
    }
}
