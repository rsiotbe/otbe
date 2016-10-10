package com.rsi.rvia.rest.conector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.client.RviaRestHttpClient;
import com.rsi.rvia.rest.error.exceptions.LogicalErrorException;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.operation.info.InterrogateRvia;
import com.rsi.rvia.rest.session.SessionRviaData;
import com.rsi.rvia.rest.tool.GettersRequestParams;
import com.rsi.rvia.rest.tool.Utils;

/** Clase que gestiona la conexión y comunicaciñon con el proveedor de datos (Ruralvia o WS) */
public class RestWSConnector
{
   private static Logger pLog = LoggerFactory.getLogger(RestWSConnector.class);

   /**
    * Realiza una petición de tipo get restFull al proveedor de datos (Ruralvia o WS dependiendo de la configuración)
    * 
    * @param pRequest
    *           petición del cliente
    * @param strPathRest
    *           path de la petición
    * @param pMiqQuests
    *           Objeto MiqQuests con la información de la operativa
    * @param pSessionRvia
    *           datos de la petición recibida desde ruralvia
    * @param strJsonData
    *           Datos a enviar
    * @param strEndPoint
    *           Endpoint del proveedor de datos
    * @param pPathParams
    *           Parámetros asociados al path
    * @return Respuesta del proveedor de datos
    * @throws Exception
    */
   public static Response get(HttpServletRequest pRequest, MiqQuests pMiqQuests,
         MultivaluedMap<String, String> pPathParams, HashMap<String, String> pParamsToInject) throws Exception
   {
      Client pClient = RviaRestHttpClient.getClient();
      String strQueryParams = pRequest.getQueryString();
      /* se obtienen lso header necesarios para realizar la petición al WS */
      String strCODSecEnt = GettersRequestParams.getCODSecEnt(pRequest);
      String strCODSecUser = GettersRequestParams.getCODSecUser(pRequest);
      String strCODSecTrans = GettersRequestParams.getCODSecTrans(pRequest);
      String strCODTerminal = GettersRequestParams.getCODTerminal(pRequest);
      String strCODApl = GettersRequestParams.getCODApl(pRequest);
      String strCODCanal = GettersRequestParams.getCODCanal(pRequest);
      String strCODSecIp = GettersRequestParams.getCODSecIp(pRequest);
      String pathQueryParams = "";
      pathQueryParams = Utils.multiValuedMapToQueryString(pPathParams) + Utils.hashMapToQueryString(pParamsToInject);
      String urlQueryString = ((strQueryParams == null) ? "" : strQueryParams) + "&idMiq=" + pMiqQuests.getIdMiq()
            + pathQueryParams;
      String strUrlTotal = pMiqQuests.getBaseWSEndPoint(pRequest) + "?" + urlQueryString;
      WebTarget pTarget = pClient.target(strUrlTotal);
      pLog.info("END_POINT:" + pMiqQuests.getEndPoint());
      Response pReturn = pTarget.request().header("CODSecEnt", strCODSecEnt).header("CODSecUser", strCODSecUser).header("CODSecTrans", strCODSecTrans).header("CODTerminal", strCODTerminal).header("CODApl", strCODApl).header("CODCanal", strCODCanal).header("CODSecIp", strCODSecIp).accept(MediaType.APPLICATION_JSON).get();
      pLog.info("GET: " + pReturn.toString());
      return pReturn;
   }

   /**
    * Realiza una petición de tipo post restFull al proveedor de datos (Ruralvia o WS dependiendo de la configuración)
    * 
    * @param pRequest
    *           petición del cliente
    * @param strPathRest
    *           path de la petición
    * @param pSessionRvia
    *           Datos de la petición recibida desde ruralvia
    * @param strJsonData
    *           Datos a enviar
    * @param pMiqQuests
    *           Objeto MiqQuests con la información de la operativa
    * @param pPathParams
    *           Parámetros asociados al path
    * @return Respuesta del proveedor de datos
    * @throws Exception
    */
   public static Response post(@Context HttpServletRequest pRequest, MiqQuests pMiqQuests, SessionRviaData pSessionRvia,
         String strJsonData, MultivaluedMap<String, String> pPathParams, HashMap<String, String> pParamsToInject)
         throws Exception
   {
      Hashtable<String, String> htDatesParameters = new Hashtable<String, String>();
      Client pClient = RviaRestHttpClient.getClient();
      // Headers
      String strCODSecEnt = GettersRequestParams.getCODSecEnt(pRequest);
      String strCODSecUser = GettersRequestParams.getCODSecUser(pRequest);
      String strCODSecTrans = GettersRequestParams.getCODSecTrans(pRequest);
      String strCODTerminal = GettersRequestParams.getCODTerminal(pRequest);
      String strCODApl = GettersRequestParams.getCODApl(pRequest);
      String strCODCanal = GettersRequestParams.getCODCanal(pRequest);
      String strCODSecIp = GettersRequestParams.getCODSecIp(pRequest);
      String strParameters = getDDBBOperationParameters(pMiqQuests.getPathRest(), "paramname");
      pLog.info("Query Params: " + strParameters);
      if (!strParameters.isEmpty())
      {
         htDatesParameters = InterrogateRvia.getParameterFromSession(strParameters, pSessionRvia);
         htDatesParameters = checkSessionValues(pRequest, htDatesParameters);
      }
      ObjectMapper pMapper = new ObjectMapper();
      ObjectNode pJson = (ObjectNode) pMapper.readTree(strJsonData);
      Enumeration<String> pEnum = htDatesParameters.keys();
      while (pEnum.hasMoreElements())
      {
         String strTableKey = (String) pEnum.nextElement();
         pJson.put(strTableKey, (String) htDatesParameters.get(strTableKey).toString());
      }
      Iterator<String> pIterator = pPathParams.keySet().iterator();
      while (pIterator.hasNext())
      {
         String strKey = (String) pIterator.next();
         pJson.put(strKey, (String) pPathParams.get(strKey).toString());
      }
      pIterator = pParamsToInject.keySet().iterator();
      while (pIterator.hasNext())
      {
         String strKey = (String) pIterator.next();
         pJson.put(strKey, (String) pPathParams.get(strKey).toString());
      }
      pJson.put("idMiq", pMiqQuests.getIdMiq());
      strJsonData = pJson.toString();
      WebTarget pTarget = pClient.target(pMiqQuests.getBaseWSEndPoint(pRequest));
      Response pReturn = pTarget.request().header("CODSecEnt", strCODSecEnt).header("CODSecUser", strCODSecUser).header("CODSecTrans", strCODSecTrans).header("CODTerminal", strCODTerminal).header("CODApl", strCODApl).header("CODCanal", strCODCanal).header("CODSecIp", strCODSecIp).post(Entity.json(strJsonData));
      pLog.info("Respose POST: " + pReturn.toString());
      return pReturn;
   }

   /**
    * Realiza una petición de tipo put restFull al proveedor de datos (Ruralvia o WS dependiendo de la configuración)
    * 
    * @param pRequest
    *           petición del cliente
    * @param strPathRest
    *           path de la petición
    * @param pSessionRvia
    *           Datos de la petición recibida desde ruralvia
    * @param strJsonData
    *           Datos a enviar
    * @param pMiqQuests
    *           Objeto MiqQuests con la información de la operativa
    * @param pPathParams
    *           Parámetros asociados al path
    * @return Respuesta del proveedor de datos
    * @throws Exception
    */
   public static Response put(@Context HttpServletRequest pRequest, MiqQuests pMiqQuests, SessionRviaData pSessionRvia,
         String strJsonData, MultivaluedMap<String, String> pPathParams, HashMap<String, String> pParamsToInject)
         throws Exception
   {
      /*
       * se reutiliza la petición post puesto que es similar, en caso de una implementación diferente, es necesario
       * definir este método desde cero
       */
      pLog.warn("Se recibe un método PUT, pero se trata como si fuera un POST");
      return post(pRequest, pMiqQuests, pSessionRvia, strJsonData, pPathParams, pParamsToInject);
   }

   /**
    * Realiza una petición de tipo delete restFull al proveedor de datos (Ruralvia o WS dependiendo de la configuración)
    * 
    * @param pRequest
    *           petición del cliente
    * @return Respuesta del proveedor de datos
    * @throws Exception
    */
   public static Response delete(@Context HttpServletRequest pRequest) throws Exception
   {
      // /??? falta por implementar el método delete
      pLog.error("El método delete no está implementado");
      throw new Exception("Se ha recibido una petición de tipo DELETE y no existe ningún método que implemente este tipo de peticiones");
   }

   /**
    * Obtiene los parámetros necesarios para poder ejecutar una operación. Los datos se leen desde la base de datos de
    * configuración.
    * 
    * @param strPathRest
    *           Path rest de la opreación a realizar
    * @param strCampo
    *           campo a consultar de la base de datos
    * @return Cadena que contiene los campos separados por el carácter ';'
    * @throws Exception
    */
   private static String getDDBBOperationParameters(String strPathRest, String strCampo) throws Exception
   {
      String strReturn = "";
      Connection pConnection = null;
      PreparedStatement pPreparedStatement = null;
      ResultSet pResultSet = null;
      try
      {
         String strQuery = "select c." + strCampo + " campo from " + " BEL.BDPTB222_MIQ_QUESTS a, "
               + " BEL.BDPTB226_MIQ_QUEST_RL_SESSION b, " + " BEL.BDPTB225_MIQ_SESSION_PARAMS c "
               + " where a.id_miq=b.id_miq " + " and b.ID_MIQ_PARAM=c.ID_MIQ_PARAM " + " and a.path_rest='"
               + strPathRest + "' order by c.ID_MIQ_PARAM";
         pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
         pPreparedStatement = pConnection.prepareStatement(strQuery);
         pResultSet = pPreparedStatement.executeQuery();
         pLog.trace("Query BBDD Params bien ejecutada");
         while (pResultSet.next())
         {
            String strInputName = (String) pResultSet.getString("campo");
            if (!strReturn.isEmpty())
            {
               strReturn += ";";
            }
            strReturn += strInputName;
         }
      }
      catch (Exception ex)
      {
         pLog.error("Error al recuperar los nombres de parametros Path_Rest(" + strPathRest + "): " + ex);
         strReturn = "";
      }
      finally
      {
         pResultSet.close();
         pPreparedStatement.close();
         pConnection.close();
      }
      return strReturn;
   }

   /**
    * Comprueba si el contenido del JSON es de tipo WS
    * 
    * @param pJsonData
    *           Objeto que contiene la información JSON
    * @return
    */
   public static boolean isWSJson(JSONObject pJsonData)
   {
      boolean fReturn = false;
      String strPrimaryKey = "";
      try
      {
         if (pJsonData.keys().hasNext())
         {
            strPrimaryKey = (String) pJsonData.keys().next();
         }
         if (!strPrimaryKey.trim().isEmpty())
         {
            String strStatusResponse = pJsonData.getJSONObject(strPrimaryKey).getString("codigoRetorno");
            if (strStatusResponse != null && strStatusResponse.trim().length() > 0)
            {
               fReturn = true;
            }
         }
      }
      catch (Exception ex)
      {
         pLog.warn("No es un JSON de WS");
         fReturn = false;
      }
      return fReturn;
   }

   /**
    * Comprueba si el contenido del JSON es un error generado por WS
    * 
    * @param pJsonData
    *           Objeto que contiene la información JSON
    * @return
    */
   public static boolean isWSError(JSONObject pJsonData)
   {
      boolean fReturn = false;
      String strPrimaryKey = "";
      try
      {
         if (pJsonData.keys().hasNext())
         {
            strPrimaryKey = (String) pJsonData.keys().next();
         }
         if (!strPrimaryKey.trim().isEmpty())
         {
            if (pJsonData.getJSONObject(strPrimaryKey).has("codigoRetorno"))
            {
               String strStatusResponse = (String) pJsonData.getJSONObject(strPrimaryKey).getString("codigoRetorno");
               if ("0".equals(strStatusResponse))
               {
                  fReturn = true;
               }
            }
         }
      }
      catch (Exception ex)
      {
         pLog.warn("No es un error de WS");
         fReturn = false;
      }
      return fReturn;
   }

   /**
    * genera unaexceción de tipo lógico a partir del mensaje de error de una respuesta WS
    * 
    * @param nHttpErrorCode
    *           Codigo de error obtenido en la cabecera de la respuesta
    * @param pJsonData
    *           Objeto que contiene la información JSON
    * @return Objeto JSON que contiene el cuerpo
    * @throws LogicalErrorException
    */
   public static boolean throwWSError(Integer nHttpErrorCode, JSONObject pJsonData) throws LogicalErrorException
   {
      Integer nCode = null;
      String strMessage = null;
      String strDescription = null;
      boolean fProcessed = false;
      try
      {
         String strPrimaryKey = "";
         if (pJsonData.keys().hasNext())
         {
            strPrimaryKey = (String) pJsonData.keys().next();
         }
         if (!strPrimaryKey.trim().isEmpty())
         {
            JSONObject pJsonContent = pJsonData.getJSONObject(strPrimaryKey).getJSONObject("Errores");
            if (pJsonContent == null)
               pLog.error("No se ha encontrado el nodo 'Errores' dentro del contenido del JSON devuelto por el WS");
            else
            {
               nCode = Integer.parseInt(pJsonContent.getString("codigoMostrar"));
               strMessage = pJsonContent.getString("mensajeMostrar");
               strDescription = pJsonContent.getString("solucion");
               fProcessed = true;
            }
         }
      }
      catch (Exception ex)
      {
         pLog.error("Error al obtener el cuerpo del mensaje de error de una respuesta WS", ex);
      }
      if (fProcessed)
         throw new LogicalErrorException(nHttpErrorCode, nCode, strMessage, strDescription, null);
      return true;
   }

   /**
    * Revisa si algun parametro recuperado esta en sesion, si lo está coge el de sesion, sino lo añade a esta
    * 
    * @param pRequest
    * @param pParameters
    * @return
    */
   public static Hashtable<String, String> checkSessionValues(@Context HttpServletRequest pRequest,
         Hashtable<String, String> pParameters)
   {
      if (pParameters != null)
      {
         HttpSession pSession = pRequest.getSession(false);
         Iterator<String> pIterator = pParameters.keySet().iterator();
         while (pIterator.hasNext())
         {
            String strKey = (String) pIterator.next();
            String strSessionValue = (String) pSession.getAttribute(strKey);
            if (strSessionValue != null)
            {
               pParameters.remove(strKey);
               pParameters.put(strKey, strSessionValue);
            }
            else
            {
               pSession.setAttribute(strKey, pParameters.get(strKey));
            }
         }
      }
      return pParameters;
   }
}
