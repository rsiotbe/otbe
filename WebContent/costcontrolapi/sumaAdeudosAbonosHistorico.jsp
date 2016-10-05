<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="java.sql.Connection,
         com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
         com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider,
         com.rsi.rvia.rest.tool.Utils, 
         com.rsi.rvia.rest.validator.DataValidator,
         com.rsi.rvia.rest.client.QueryCustomizer,
         java.sql.PreparedStatement,
         java.sql.ResultSet,
         org.json.JSONArray,
         org.json.JSONObject,
         org.slf4j.Logger,
         org.slf4j.LoggerFactory,
        java.util.Date,
        java.util.Hashtable,
        java.text.ParseException,
        java.text.SimpleDateFormat
"
%>
<%

    // Validación de forma de número de acuerdo
    QueryCustomizer qCustomizer=new QueryCustomizer();
    String strJsonError = "{}";
    String strPathRest = "/costcontrol/contracts/{idContract}";
    String strContrato = request.getParameter("idContract");  
    String strLinea = request.getParameter("codLinea");
    String strIdInternoPe = request.getParameter("idInternoPe");
    String strEntidad = request.getParameter("codEntidad").toString();
    String strDateIni = request.getParameter("mesInicio").toString();
    String strDateFin = request.getParameter("mesFin");
    
    strDateIni = strDateIni + "-01";


    
/*  
    Hashtable<String,String> htParams = new Hashtable<String,String>();
    
    htParams.put("idContract", strContrato);
    htParams.put("codEntidad", strEntidad);
    htParams.put("fechaInicio", strDateIni);
    try{
        strJsonError = DataValidator.validation(strPathRest, htParams);
    }catch(Exception ex){       
        strJsonError = "{\"Error\":\""+ ex.getMessage() +"\"}";
        ex.printStackTrace();
    }

*/


    String strResponse = "{}";
    if("{}".equals(strJsonError)) {
    // Query si todo en orden
    // FIXME: añadir hint full t1
        String strQuery =
                " select" +
                "    to_char(fecha_oprcn_dif,'YYYY-MM')  \"mes\"" +
                "   ,sgn  \"signo\"" +
                "   ,sum(imp_apnte) \"importe\"" +
                " from rdwc01.mi_do_apte_cta" +
                " where cod_nrbe_en='" + strEntidad + "'" +
                " and fecha_oprcn_dif >= trunc(to_date('" + strDateIni + "','yyyy-mm-dd'),'mm')";
        if(strDateFin == null){
           strQuery = strQuery + " and fecha_oprcn_dif <= (select max(fecha_oprcn_dif) from rdwc01.mi_do_apte_cta where cod_nrbe_en='" + request.getParameter("codEntidad") + "')";
        }
        else{
      	  strDateFin = strDateFin + "-21";
      	  strQuery = strQuery + " and fecha_oprcn_dif <= trunc(to_date('" + strDateFin + "','yyyy-mm-dd'),'mm')";
        }
      	 
        strQuery = strQuery + " and ind_accion <> 3" +
                " and ind_2 in ('S','N','O')" +
                " and cod_numrco_moneda = '978'";
        
        if(strContrato == null){
      	  strQuery = strQuery + " and cod_linea ='" + strLinea + "'"; 
      	  strQuery = strQuery + " and num_sec_ac in (" +
      			  " select num_sec_ac from rdwc01.mi_ac_cont_gen" +
      			  " where cod_nrbe_en='" + strEntidad + "' " +
      			  "    and id_interno_pe=" + strIdInternoPe +
      			  "    and mi_fecha_fin=to_date('31.12.9999','dd.mm.yyyy')" +
      			  "     and cod_linea='" + strLinea + "'" +
      		")";
        }
        else{
      	  strQuery = strQuery + " and num_sec_ac =" + strContrato; 
      	  
      	  
      	  
        }      
        strQuery = strQuery + " group by to_char(fecha_oprcn_dif,'YYYY-MM'),fecha_oprcn_dif, sgn  " ;              
  
        qCustomizer.setFieldsList(request.getParameter("fieldslist"));
        qCustomizer.setSortersList(request.getParameter("sorterslist"));
        strQuery = qCustomizer.getParsedQuery(strQuery);              
                
        Connection pConnection = null;
        PreparedStatement pPreparedStatement = null;
        ResultSet pResultSet = null;
        pConnection = DDBBPoolFactory.getDDBB(DDBBProvider.OracleCIP);
        pPreparedStatement = pConnection.prepareStatement(strQuery);
        pResultSet = pPreparedStatement.executeQuery();

        JSONObject pJson = new JSONObject();
        JSONObject pJsonExit= new JSONObject();
        Logger  pLog = LoggerFactory.getLogger("jsp");  
        
        JSONArray json = Utils.convertResultSet2JSON(pResultSet);
        pResultSet.close();
        pPreparedStatement.close();
        pConnection.close();
        pJsonExit.put("data", json);
        pJson.put("response", pJsonExit);
        strResponse = pJson.toString();
    }else{
        strResponse = strJsonError;
    }
    
    response.setHeader("content-type", "application/json");
    //pLog.info("------------------------------- " + json.toString());
%><%=strResponse%>
