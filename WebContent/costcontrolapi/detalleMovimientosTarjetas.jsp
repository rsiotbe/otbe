<%@page import="java.net.URLEncoder"%>
<%@page import="java.net.URLDecoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="
        com.rsi.rvia.rest.endpoint.rsiapi.AcuerdosRuralvia,
        com.rsi.rvia.rest.client.QueryCustomizer,
        org.slf4j.Logger,
        org.slf4j.LoggerFactory 
"
%>
<%
String uri = request.getRequestURI();
String pageName = uri.substring(uri.lastIndexOf("/")+1);
Logger pLog  = LoggerFactory.getLogger(pageName);
String [] strRviaAcuerdos = AcuerdosRuralvia.getRviaContractsDecodeAliases(request);
    String strContrato = request.getParameter("idContract");  
    String strEntidad = request.getParameter("codEntidad");
    String strIdInternoPe = request.getParameter("idInternoPe");
    
    String strDateFin = request.getParameter("mesFin"); 
    String strDateIni = request.getParameter("mesInicio");
    
    String strParamLocalidad = request.getParameter("localidad"); 
    String strParamCategoria = request.getParameter("categoria");     
    String strParamComercio = request.getParameter("comercio"); 
    
    String strFiltro = "";
    if(strParamLocalidad != null){
        strFiltro = "and nvl(trim(e.localidad2),'OTROS') = '" + strParamLocalidad + "'";
    }
    else if(strParamCategoria != null){
        strFiltro = "and nvl(trim(h.descripcion),'Otros') = '" + strParamCategoria + "'";
    }
    else if(strParamComercio != null){
        strFiltro = "and nvl(trim(e.nomcomred),'OTROS') = '" + strParamComercio + "'";
    }       
    if(strDateIni == null){
        strDateIni = strDateFin;
    }      
    if(strDateFin == null){
        strDateFin = "9999-11";
    }      
    
    strDateFin= QueryCustomizer.yearMonthToFirstDayOfNextMonth(strDateFin);
    strDateIni= QueryCustomizer.yearMonthToLastDayOfPreviousMonth(strDateIni);
    String strQuery =
            " select /" + "*" + "+ FULL(e) *" + "/ " +
                    "   e.fecha_oprcn \"fechaOperacion\", " +
                    "   e.hora_oprcn_u \"horaOperacion\", " +
                    "   case " +
                    "       when e.tipfac2 = '0033' then (e.imptrn * -1) else e.imptrn " +
                    "   end \"importe\", " +        
                    "   nvl(trim(e.nomcomred),'OTROS') \"nombreComercio\", " +
                    "   nvl(trim(e.localidad2),'OTROS') \"localidad\", " +
                    "   e.codact \"codCategoria\", " +
                    "   nvl(trim(h.descripcion),'Otros') \"categoria\" " +
                    " from " +
                    "   rdwc01.MI_MPA2_OPERAC_TARJETAS e " +
				    " left join rdwc01.mi_ac_cont_gen f " +
				    "   on e.cod_nrbe_en = f.cod_nrbe_en " +
				    "   and e.num_sec_ac = f.num_sec_ac " +
				    "   and f.mi_fecha_fin = to_date('9999-12-31','yyyy-mm-dd') " +
                    " left outer join proc01.tp_codact_tasas h " +
                    "   on e.codact = h.codacti " +
                    "   and e.cod_nrbe_en = h.cod_nrbe_en " +
                    " WHERE  e.COD_NRBE_EN = '" + strEntidad + "' " +
                    " and e.codrespu = '000' " +
                    " and e.indcruce = 1 " +
                    strFiltro +
                    " and e.FECHA_OPRCN < to_date('" + strDateFin + "', 'yyyy-mm-dd') " +
                    " and e.FECHA_OPRCN > to_date('" + strDateIni + "', 'yyyy-mm-dd') ";
                    if(strContrato == null){
                        if(strRviaAcuerdos[1]==null){
                            strQuery = strQuery +  " and f.id_interno_pe = " + strIdInternoPe;
                        }
                        else{
                            strQuery = strQuery + " and e.num_sec_ac in (" + strRviaAcuerdos[1] +  ") ";
                        }
                     }
                     else{
                        strQuery = strQuery + " and e.num_sec_ac =" + strContrato; 
                     }                                 
    pLog.info("Query al customizador: " + strQuery);       
    String strResponse = QueryCustomizer.process(request,strQuery);
    response.setHeader("content-type", "application/json");
%>
<%=strResponse %>
