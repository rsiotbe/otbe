package com.rsi.rvia.rest.conector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory;
import com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider;
import com.rsi.rvia.rest.client.RviaRestHttpClient;
import com.rsi.rvia.rest.error.ErrorManager;
import com.rsi.rvia.rest.error.exceptions.LogicalErrorException;
import com.rsi.rvia.rest.error.exceptions.RestConnectorException;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.operation.info.InterrogateRvia;
import com.rsi.rvia.rest.session.RequestConfigRvia;

/** Clase que gestiona la conexión y comunicaciñon con el proveedor de datos (Ruralvia o WS) */
public class RestRviaConnector
{
   private static Logger pLog = LoggerFactory.getLogger(RestRviaConnector.class);

   /**
    * Realiza la comunicación con RUralvia para obtener los datos necesarios de la operación
    * 
    * @param pRequest
    *           petición del cliente
    * @param pMiqQuests
    *           Objeto MiqQuests con la información de la operativa
	 * @param pRequestConfigRvia
    *           datos de la petición recibida desde ruralvia
    * @param strData
    *           datos a enviar al proveedor
    * @return Respuesta del proveedor de datos
    * @throws Exception
    */
	public static Response doConnection(HttpServletRequest pRequest, MiqQuests pMiqQuests,
			RequestConfigRvia pRequestConfigRvia, String strData) throws RestConnectorException
   {
      WebTarget pTarget;
      Response pReturn;
      try
      {
         MultivaluedMap<String, String> pSessionFields = new MultivaluedHashMap<String, String>();
			String strSesId = pRequestConfigRvia.getRviaSessionId();
			String strHost = pRequestConfigRvia.getUriRvia().toString();
         String strClavePagina = pMiqQuests.getEndPoint();
         String strUrl = strHost + "/portal_rvia/ServletDirectorPortal;RVIASESION=" + strSesId + "?clavePagina="
               + strClavePagina;
         pLog.trace("Se compone la url a invocar a ruralvia: " + strUrl);
         Client pClient = RviaRestHttpClient.getClient();
         org.w3c.dom.Document pXmlDoc = InterrogateRvia.getXmlDatAndUserInfo(pRequest, strClavePagina);
         pLog.trace("Se obtiene el xml de configuración desde ruralvia y se procede a evaluar su contenido");
         proccessInformationFromRviaXML(pXmlDoc, pMiqQuests, pSessionFields);
         pLog.trace("Se añade la información reciida en la propia peticón");
         addDatatoSessionFields(strClavePagina, strData, pSessionFields);
         pLog.info("Se procede a invocar a ruralvia utilizando la url y los campos obtenidos desde sesión del usuario y por la propia petición.");
         pTarget = pClient.target(UriBuilder.fromUri(strUrl).build());
         pReturn = pTarget.request().post(Entity.form(pSessionFields));
         pLog.trace("Respuesra obtenida desde ruralvia: " + pReturn);
      }
      catch (Exception ex)
      {
         pLog.error("Se detecta un error en la ejecucón general de obtener datos desde ruralvia. Error:" + ex);
         pLog.info("Se genera la exceción adecuada para ser tratada en la respuesta al cliente");
         throw new RestConnectorException(500, 999999, "Error al conectar con RVIA", "Se ha producido un error en la conexión con ruralvia", ex);
      }
      return pReturn;
   }

   private static void proccessInformationFromRviaXML(org.w3c.dom.Document pXmlDoc, MiqQuests pMiqQuests,
         MultivaluedMap<String, String> pSessionFields) throws Exception
   {
      NodeList pNodos = pXmlDoc.getElementsByTagName("field");
      Vector<String> pSessionParamNames = new Vector<String>();
      /* Se leen los datos de sesión recibidos en la configuración de la operativa de ruralvia para este usuario */
      for (int i = 0; i < pNodos.getLength(); i++)
      {
         Element pElement = (Element) pNodos.item(i);
         String strValue = pElement.getAttribute("value");
         if (!strValue.isEmpty())
         {
            pLog.info("campo " + pElement.getAttribute("name").toString() + "\t:\t" + strValue.toString());
            pSessionFields.add(pElement.getAttribute("name"), strValue.toString());
            pSessionParamNames.add(pElement.getAttribute("name"));
         }
         else
         {
            pLog.info("campo " + pElement.getAttribute("name").toString() + "\t:\t<<NO INFORMADO>>");
         }
      }
      pLog.trace("Se procede a censar los nombres del los campos de la opereativa");
      saveDDBBSenssionVarNames(pMiqQuests.getIdMiq(), pSessionParamNames);
   }

   private static void addDatatoSessionFields(String strClavePagina, String strData,
         MultivaluedMap<String, String> pSessionFields)
   {
      pSessionFields.add("clavePagina", strClavePagina);
      // Se evaluan los datos que llegan en la parte de datos.
      String[] pArr = strData.split("&");
      if (!strData.trim().isEmpty())
      {
         for (int i = 0; i < pArr.length; i++)
         {
            if (pArr[i].trim().isEmpty())
               continue;
            String[] pArr2 = pArr[i].split("=");
            if (pArr2.length < 2)
               continue;
            if (pArr2[0].trim().isEmpty() || pArr2[1].trim().isEmpty())
               continue;
            pSessionFields.add(pArr2[0], pArr2[1]);
         }
      }
   }

   /**
    * Guarda los parámetros necesarios, si todavia no lo están, para una invoación a ruralvia en base de datos
    * 
    * @param nIdMiq
    *           Identificador de operativa interno
    * @param aParamNames
    *           Array con los nombres de parámetros
    * @throws Exception
    */
   private static synchronized void saveDDBBSenssionVarNames(int nIdMiq, Vector<String> aParamNames) throws Exception
   {
      for (int i = 0; i < aParamNames.size(); i++)
      {
         String strParamName = aParamNames.get(i);
         if (!strParamName.trim().isEmpty())
         {
            pLog.trace("Se evalua el campo " + strParamName + " para su censo en la operativa con id " + nIdMiq);
            boolean fExistConfig = existConfigInDDBB(nIdMiq, strParamName);
            /* si no existe la relación se procede a crarla */
            if (!fExistConfig)
            {
               /*
                * si el parámetro no existe, se comprueba si está definido como parámetro de culaquier otra opertativa
                */
               Integer nIdMiqParam = existParamInDDBB(strParamName);
               if (nIdMiqParam == null)
               {
                  /* el parámetro no está definido en DDBB, se procede a darlo de alta */
                  nIdMiqParam = getNextParamId();
                  insertNewParam(nIdMiqParam, strParamName);
               }
               /* se añade la realación entre el parámetro y la operación */
               createRelationParamAndOperation(nIdMiqParam, strParamName);
            }
         }
      }
   }

   /**
    * Comprueba si un parámetro ya está relacionado con una operativa
    * 
    * @param nIdMiq
    *           identificador de eoperativa
    * @param strParamName
    *           nombre del parámetro
    * @return Id del parametro en caso de existir
    */
   private static boolean existConfigInDDBB(int nIdMiq, String strParamName)
   {
      Connection pConnection = null;
      ;
      PreparedStatement pPreparedStatement = null;
      ResultSet pResultSet = null;
      boolean fReturn = false;
      String strQuery = "select a.id_miq from  BEL.BDPTB222_MIQ_QUESTS a, "
            + "BEL.BDPTB226_MIQ_QUEST_RL_SESSION b, BEL.BDPTB225_MIQ_SESSION_PARAMS c "
            + "where a.id_miq=b.id_miq and b.ID_MIQ_PARAM=c.ID_MIQ_PARAM and a.id_miq=? " + "and c.PARAMNAME=?";
      try
      {
         pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
         pPreparedStatement = pConnection.prepareStatement(strQuery);
         pPreparedStatement.setInt(1, nIdMiq);
         pPreparedStatement.setString(2, strParamName);
         pResultSet = pPreparedStatement.executeQuery();
         if (pResultSet.next())
         {
            if (!fReturn)
            {
               /* el parametro existe y está relacionado, se pasa al siguiente parámetro */
               pLog.trace("El campo " + strParamName + " ya se encuentra censado para esta operativa");
               fReturn = true;
            }
         }
      }
      catch (Exception ex)
      {
         pLog.error("Error al obtener la información de la configuración de MiqQuest", ex);
      }
      finally
      {
         DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
      }
      return fReturn;
   }

   /**
    * Comprueba si el parámetro existe ya dado de alta en las tablas de MiqQuest
    * 
    * @param strParamName
    *           Nombre del parámetro
    * @return Identificador del parámetro
    */
   private static Integer existParamInDDBB(String strParamName)
   {
      Connection pConnection = null;
      PreparedStatement pPreparedStatement = null;
      ResultSet pResultSet = null;
      Integer nReturn = null;
      String strQuery = "select a.ID_MIQ_PARAM from BEL.BDPTB225_MIQ_SESSION_PARAMS a where a.PARAMNAME = ?";
      try
      {
         pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
         pPreparedStatement = pConnection.prepareStatement(strQuery);
         pPreparedStatement.setString(1, strParamName);
         pResultSet = pPreparedStatement.executeQuery();
         if (pResultSet.next())
         {
            nReturn = (Integer) pResultSet.getInt("ID_MIQ_PARAM");
         }
      }
      catch (Exception ex)
      {
         pLog.error("Error al obtener el identificador de un parámetro de MiqQuest", ex);
      }
      finally
      {
         DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
      }
      return nReturn;
   }

   /**
    * Obtiene el siguiente id libre en la tabla de configuración de parámetros MiqQuest
    * 
    * @return Identificador a utilizar
    */
   private static Integer getNextParamId()
   {
      Connection pConnection = null;
      PreparedStatement pPreparedStatement = null;
      ResultSet pResultSet = null;
      Integer nReturn = null;
      String strQuery = "select (select * from (select ID_MIQ_PARAM from BEL.BDPTB225_MIQ_SESSION_PARAMS order by ID_MIQ_PARAM desc)	where rownum = 1) + 1 ID_MIQ_PARAM from dual;";
      try
      {
         pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
         pPreparedStatement = pConnection.prepareStatement(strQuery);
         pResultSet = pPreparedStatement.executeQuery();
         if (pResultSet.next())
         {
            nReturn = (Integer) pResultSet.getInt("ID_MIQ_PARAM");
         }
      }
      catch (Exception ex)
      {
         pLog.error("No se ha podido generar un id de secuencia para el campo ID_MIQ_PARAM de la tabla BEL.BDPTB225_MIQ_SESSION_PARAMS", ex);
      }
      finally
      {
         DDBBPoolFactory.closeDDBBObjects(pLog, pResultSet, pPreparedStatement, pConnection);
      }
      return nReturn;
   }

   /**
    * Se crear un nuevo registro de parámetros de tipo MiqQuest
    * 
    * @param nIdMiqParam
    * @param strParamName
    */
   private static void insertNewParam(int nIdMiqParam, String strParamName)
   {
      Connection pConnection = null;
      PreparedStatement pPreparedStatement = null;
      String strQuery = "insert into BEL.BDPTB225_MIQ_SESSION_PARAMS values (?, ? ,'','','RVIASESSION','','')";
      try
      {
         pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
         pPreparedStatement = pConnection.prepareStatement(strQuery);
         pPreparedStatement.setInt(1, nIdMiqParam);
         pPreparedStatement.setString(2, strParamName);
         pPreparedStatement.executeUpdate();
      }
      catch (Exception ex)
      {
         pLog.error("No se ha podido insertar el parámetro " + strParamName + " con id " + nIdMiqParam, ex);
      }
      finally
      {
         DDBBPoolFactory.closeDDBBObjects(pLog, null, pPreparedStatement, pConnection);
      }
   }

   /**
    * Se crear un nuevo registro de relación parámetro - operativa de tipo MiqQuest
    * 
    * @param nIdMiqParam
    *           Identificador de la operativa MiqQuest
    * @param strParamName
    *           Nombre del parámetro
    */
   private static void createRelationParamAndOperation(int nIdMiqParam, String strParamName)
   {
      Connection pConnection = null;
      PreparedStatement pPreparedStatement = null;
      String strQuery = "insert into BEL.BDPTB226_MIQ_QUEST_RL_SESSION values(?, ?, '')";
      try
      {
         pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleBanca);
         pPreparedStatement = pConnection.prepareStatement(strQuery);
         pPreparedStatement.setInt(1, nIdMiqParam);
         pPreparedStatement.setString(2, strParamName);
         pPreparedStatement.executeUpdate();
      }
      catch (Exception ex)
      {
         pLog.error("No se ha podido insertar la relación del parámetro " + strParamName + " con la operativa "
               + nIdMiqParam, ex);
      }
      finally
      {
         DDBBPoolFactory.closeDDBBObjects(pLog, null, pPreparedStatement, pConnection);
      }
   }

   /**
    * Comprueba si el texto recibido es una pagina de error generada por ruralvia *
    * 
    * @param strHtml
    *           Datos recibidos (html)
    * @return
    */
   public static boolean isRuralviaWebError(String strHtml)
   {
      boolean fReturn = false;
      Document pHtmlDoc;
      try
      {
         pHtmlDoc = Jsoup.parse(strHtml);
         if ((pHtmlDoc.getElementsByClass("txtaviso") != null) && (pHtmlDoc.getElementsByClass("txtaviso").size() > 0))
            fReturn = true;
      }
      catch (Exception ex)
      {
         pLog.warn("El contenido recibido como html parece no serlo.");
      }
      return fReturn;
   }

   /**
    * Comprueba si el texto recibido es una pagina de cierre de sesión generada por ruralvia *
    * 
    * @param strHtml
    *           Datos recibidos (html)
    * @return
    */
   public static boolean isRuralviaSessionTimeoutError(String strHtml)
   {
      boolean fReturn = false;
      Document pHtmlDoc;
      try
      {
         pHtmlDoc = Jsoup.parse(strHtml);
         if ((pHtmlDoc.getElementsByClass("txtaviso") != null) && (pHtmlDoc.getElementsByClass("txtaviso").size() > 0))
            fReturn = true;
      }
      catch (Exception ex)
      {
         pLog.warn("El contenido recibido como html parece no serlo.");
      }
      return fReturn;
   }

   /**
    * Genera una exceción de tipo logico a partir de los datos que devuelve ruralvia en la pagina de error
    * 
    * @param strHtml
    *           Pagina de error de ruralvia
    * @return Excepción de tipo logico con los datos del esrror
    * @throws JSONException
    */
   public static LogicalErrorException generateLogicalErrorException(String strHtml) throws JSONException
   {
      LogicalErrorException pReturn;
      String strInnerErrorCode;
      String strTextError = null;
      String strErrorCodeStart = "<!-- CODIGO DE ERROR ORIGINAL ";
      String strErrorTextStart = "<!-- TEXTO DE ERROR ORIGINAL ";
      String strErrorEnd = " -->";
      int lStartToken;
      int lEndToken;
      int nInnerErrorCode = 999999;
      try
      {
         /* se obtiene el código de error */
         lStartToken = strHtml.indexOf(strErrorCodeStart) + strErrorCodeStart.length();
         lEndToken = strHtml.indexOf(strErrorEnd, lStartToken);
         strInnerErrorCode = strHtml.substring(lStartToken, lEndToken);
         nInnerErrorCode = Integer.getInteger(strInnerErrorCode);
         /* se obtiene el texto de error */
         lStartToken = strHtml.indexOf(strErrorTextStart) + strErrorTextStart.length();
         lEndToken = strHtml.indexOf(strErrorEnd, lStartToken);
         strTextError = strHtml.substring(lStartToken, lEndToken);
      }
      catch (Exception ex)
      {
         pLog.error("Se ha producido un error al obtener el error devuelto por ruralvia", ex);
         nInnerErrorCode = 999999;
         strTextError = "Error en la petición a ruralvía";
      }
      finally
      {
         pReturn = new LogicalErrorException(400, nInnerErrorCode, strTextError, null, null);
      }
      return pReturn;
   }

   /**
    * Comprueba si el contenido del JSON es un error generado por ruralvia WS
    * 
    * @param pJsonData
    *           Objeto que contiene la información JSON
    * @return
    */
   public static boolean isRVIAError(JSONObject pJsonData)
   {
      boolean fReturn = false;
      String strInnerCode;
      try
      {
         strInnerCode = pJsonData.getString("CODERRR");
         fReturn = (strInnerCode != null) && (!strInnerCode.trim().isEmpty());
      }
      catch (Exception ex)
      {
         pLog.error("No es un error de RVIA");
         fReturn = false;
      }
      return fReturn;
   }

   /**
    * @param pSessionRviaData
    *           Datos de sesión del usuario en ruralvia
    * @param pRestConnector
    *           Conector al origen de los datos
    * @param pJsonData
    *           Objeto que contiene la información JSON
    * @return Indica si se ha llegado ha lanzar una excepción de error
    * @throws LogicalErrorException
    */
	public static boolean throwRVIAError(RequestConfigRvia pSessionRviaData, MiqQuests pMiqQuests, JSONObject pJsonData)
         throws LogicalErrorException
   {
      boolean fReturn = false;
      String strInnerCode;
      Integer nCode = null;
      String strMessage = null;
      String strDescription = null;
      Integer nHttpErrorCode = 400;
      boolean fProcessed = false;
      try
      {
         strInnerCode = pJsonData.getString("CODERRR");
         nCode = Integer.parseInt(strInnerCode);
         strDescription = pJsonData.getString("TXTERRR");
         strMessage = ErrorManager.getFriendlyErrorFromRuralvia(strInnerCode, pSessionRviaData, pMiqQuests);
         fProcessed = true;
      }
      catch (Exception ex)
      {
         pLog.error("Error al obtener el cuerpo del mensaje de error de una respuesta RVIA", ex);
      }
      if (fProcessed)
         throw new LogicalErrorException(nHttpErrorCode, nCode, strMessage, strDescription, null);
      return fReturn;
   }
}
