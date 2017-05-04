<%@page import="java.util.Date"%>
<%@page import="java.security.NoSuchAlgorithmException"%>
<%@page import="java.security.MessageDigest"%>
<%@page import="java.io.DataOutputStream"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="org.json.JSONException"%>
<%@page import="org.json.JSONObject"%>
<%@page import="java.sql.Types"%>
<%@page import="java.sql.CallableStatement"%>
<%@page import="org.json.JSONArray"%>
<%@page import="com.rsi.rvia.rest.tool.Utils"%>
<%@page import="oracle.jdbc.OracleTypes"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.rsi.rvia.rest.client.MiqAdminValidator"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory.DDBBProvider"%>
<%@page import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory"%>
<%
	pLog.info("Messages ::: newMessage ::: Start");
	JSONObject pJsonResponse = new JSONObject();
	response.setHeader("content-type", "application/json");

	String strCodNrbe =  request.getParameter("codNrbe");
	

	String digiere = "030303001989003es_ES";//request.getParameter("USUARIO")+request.getParameter("ENTALT")+
			 //request.getParameter("OFIALT")+request.getParameter("TIPUSR")+
			 //request.getParameter("idioma");
	JSONObject pJsonResult = null;
	try {
		pJsonResult = sendMessage(digiere);
	} catch (Exception e){
		%><%=Utils.generateWSResponseJsonError("newMessage", strErrorCode, strErrorMessage)%><%
	}
	pJsonResponse.put("data", pJsonResult);
	%><%=Utils.generateWSResponseJsonOk("newMessage", pJsonResponse.toString())%>
<%!

private final String USER_AGENT = "Mozilla/5.0";
private int strErrorCode     = -1;
private String strErrorMessage  = "errorMessage";
private Logger pLog = LoggerFactory.getLogger("newMessage.jsp");
private final String url = "http://lnxntf04:2035/gestionComunicados/altaMensaje.do";

private JSONObject sendMessage(String digiere) throws Exception{
	URL obj = new URL(url);
	HttpURLConnection con = (HttpURLConnection) obj.openConnection();

	//add reuqest header
	con.setRequestMethod("POST");
	con.setRequestProperty("User-Agent", USER_AGENT);
	con.setRequestProperty("Accept-Language", "es-ES,en;q=0.5");

	String urlParameters = "CODIGO_APP=GCO&ENTALT=198&OFIALT=900&USUARIO=03030300&TIPUSR=3&NUMTAR=198030300&idioma=es_ES&canal=000003&marca=0000&PATH=%2Fportal_rvia%2FServletDirectorPortal%3BRVIASESION%3DVcSqdOseUZ11oa1HZDa9XCkQ6O10UxrCnAid8T2vRHkouS9qj0V-%21-1324441015%21-1738423937&IP=10.1.243.186&num_session=21781507&CLIENTE_EMPRESA=P&PERCON=005&SELCON=00000000002021262692&PRITAR=198052445&servidor=Internet_el91teswls01_07&entornoRVIA=TEST&clMensajeId=4&asunto=Otra+m%E1s&textoOculto=&textClob=%3Cdiv+style%3D%22text-align%3A+center%3B%22%3EUna+m%E1aass%3C%2Fdiv%3E";
	String firma = java.net.URLEncoder.encode(""+digiereSHA1(digiere));
	urlParameters += "&firmaRSI="+firma;
	urlParameters += "&fechaRSI="+getfechaRSI();
	// Send post request
	con.setDoOutput(true);
	DataOutputStream wr = new DataOutputStream(con.getOutputStream());
	wr.writeBytes(urlParameters);
	wr.flush();
	wr.close();

	int responseCode = con.getResponseCode();
	
	switch (responseCode) {
		case 200:
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + urlParameters);
			System.out.println("Response Code : " + responseCode);
	
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
	
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
				System.out.println(inputLine);
			}
			in.close();
	
			System.out.println(response.toString());
			break;
		case 302:
			strErrorCode = -32;
			strErrorMessage = "Error ocurrido en la aplicación";
			throw new Exception("Error de aplicación");
		case 500:
			strErrorCode = -505;
			strErrorMessage = "Error ocurrido en la aplicación";
			throw new Exception("Error de aplicación");
		default:
			break;
	}
	
	return null;
}

public static String digiereSHA1(String cadena) {

/*

Test A: 'abc'
        A9993E36 4706816A BA3E2571 7850C26C 9CD0D89D

Test B: abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq
        84983E44 1C3BD26E BAAE4AA1 F95129E5 E54670F1        

Test C: One million 'a' characters
        34AA973C D4C4DAA4 F61EEB2B DBAD2731 6534016F
        
*/


	try{
		String algoritmo="SHA1";
		byte[] inputData = cadena.getBytes();
		MessageDigest md = MessageDigest.getInstance(algoritmo);
		md.update(inputData);
		byte[] digest = md.digest();
		String aux = new String(digest);
		//System.out.println("El digOut vale: "+digOut(digest)+".");
		
		return digOut(digest);
	}catch(NoSuchAlgorithmException nsae){
			return null;
	}	
}
/**
 * Inserte aquí la descripción del método.
 * Fecha de creación: (27/5/02 13:11:57)
 * @return java.lang.String
 */
private static String digOut(byte[] digestBits) {

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < digestBits.length; i++) {
            char c1, c2;

            c1 = (char) ((digestBits[i] >>> 4) & 0xf);
            c2 = (char) (digestBits[i] & 0xf);
            c1 = (char) ((c1 > 9) ? 'A' + (c1 - 10) : '0' + c1);
            c2 = (char) ((c2 > 9) ? 'A' + (c2 - 10) : '0' + c2);
            sb.append(c1);
            sb.append(c2);
            if (((i+1) % 4) == 0)
                sb.append(' ');
        }
        return sb.toString();
    }

	private long getfechaRSI() {
			long pipi = new Date().getTime();
			return pipi;
		}
%>

