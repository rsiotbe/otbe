package com.rsi.rvia.rest.endpoint.ruralvia.translatejson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.CharEncoding;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient; // nueva
import org.apache.http.client.config.RequestConfig; // nueva
import org.apache.http.client.methods.HttpGet; // nueva
import org.apache.http.client.methods.HttpPost; // nueva
import org.apache.http.entity.ContentType; // nueva
import org.apache.http.entity.StringEntity; // nueva
import org.apache.http.impl.client.HttpClientBuilder; // nueva
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.json.JSONException;
import org.json.JSONObject;
import com.rsi.rvia.rest.error.exceptions.ISUMException;

public class PruebaTranslate
{
    /** The Constant URL_SERVICE. */
    private static final String URL_SERVICE   = "http://localhost:8080/OPC/rest/api/rviatranslatejson";
    /** The Constant CONTEN_TYPE. */
    private static final String CONTEN_TYPE   = "application/json";
    /** The Constant ENCODING_UTF8. */
    private static final String ENCODING_UTF8 = "UTF-8";

    @SuppressWarnings("unchecked")
    public static void main(String[] args) throws Exception
    {
        Hashtable<String, Object> data = new Hashtable<String, Object>();
        String strReturn = null;
        /*
         * data.put("strCode", "Mars"); data.put("strDesc", 32); data.put("city", "NY"); JSONObject jsonObject = new
         * JSONObject(); jsonObject.putAll(data); System.out.println("Entro_2"); if
         * (TranslateJsonConnector.isJsonError(jsonObject.toJSONString())) { System.out.println("Es error"); } else {
         * System.out.println("No es error"); }
         */
        data.put("CANALAIX", "000001");
        data.put("CONTRATACION", "N");
        data.put("PATH", "/portal_rvia/ServletDirectorPortal;RVIASESION=GblLRpHvqL6PW_FPSaUZil_8-3VjrSSeyrdmPmmD_NSuJe2I7liv!-1655323657!-891786700");
        data.put("HQDDNE", "false");
        data.put("USUARIO_RACF", "323008MQ");
        data.put("PRITAR", "3008894576");
        data.put("IP", "172.22.45.16");
        data.put("NUMTAR", "3008894576");
        data.put("marcaAix", "0000");
        data.put("CLAVE_PAGINA", "MENUP_RECIB_BAJA_DOMI");
        data.put("SELCON", "00000000001933265892");
        data.put("NUMEXP", "9999999999999999");
        data.put("NUM_SESSION", "21135293");
        data.put("marca", "0000");
        data.put("MARCAAIX", "0000");
        data.put("idioma", "es_ES");
        data.put("USUARIO", "32894576");
        data.put("JSP_A_DESPACHAR", "/recibos/menup_recib_baja_domi.jsp");
        data.put("JSP_DESTINO", "/recibos/menup_recib_baja_domi.jsp");
        data.put("canal", "000003");
        data.put("canalAix", "000001");
        data.put("METODO", "[Ljava.lang.String;@17bb21d2");
        data.put("CONTRA_RACF", "323008MQ");
        data.put("ENTID", "3008");
        data.put("demo", "0");
        data.put("CODOFI", "9640");
        data.put("lineaGrupo", "0");
        data.put("OPDORA", "AAAAAAAA");
        data.put("CODERR", (new Long(779)).toString());
        data.put("TXTERR", "No hay cuentas de pasivo");
        data.put("JSP", "CLV_ERR_CTAS");
        String strClavepagina = "MENUP_RECIB_BAJA_DOMI";
        String strJsonResult = objectToJson(data);
        strReturn = "{" + "\"" + strClavepagina + "\": { \"codigoRetorno\": \"1\", \"Respuesta\": " + strJsonResult
                + "}}";
        System.out.println("1_:" + strReturn);
        procesa(strReturn);
        procesa(strReturn, 0);
        /*
         * data = null; strReturn = "{" + "\"" + strClavepagina +
         * "\": { \"codigoRetorno\": \"0\", \"Errores\": { \"codigoMostrar\": \"99999\",\"mensajeMostrar\": \"No data\",\"solucion\":\"REVISE LA ESPECIFICACION DEL SERVICIO\"}}}}"
         * ; System.out.println("3_:" + strReturn); procesa(strReturn);
         */
        // System.out.println(isJsonError(strReturn));
    }

    public static Response procesa(String strData)
    {
        Response response = null;
        JSONParser parser = new JSONParser();
        try
        {
            // JSONObject jsonObject = (JSONObject) parser.parse(strData);
            JSONObject jsonObject = new JSONObject(strData);
            response = TranslateJsonManager.processData(null, null, jsonObject, false);
            // response = TranslateJsonManager.processData(pRequest, pUriInfo, jsonObject.get("strCode").toString(),
            // jsonObject.get("strDesc").toString());
        }
        catch (ISUMException e)
        {
            System.out.println("Error en ISUM: " + e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(e).encoding(CharEncoding.UTF_8).build();
        }
        // catch (ParseException e)
        catch (JSONException e)
        {
            System.out.println("Error en el json de entrada: " + e);
            return Response.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).entity(e).encoding(CharEncoding.UTF_8).build();
        }
        return response;
    }

    public static HttpResponse procesa(String strData, int uno)
    {
        HttpResponse response = null;
        StringWriter writer = null;
        BufferedReader br = null;
        String output = null;
        JSONObject jsonObject = null;
        String error = "00";
        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder = requestBuilder.setConnectTimeout(10000);
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(requestBuilder.build());
        // Creamos el http client
        HttpClient httpclient = builder.build();
        // Creamos la petición GET y enviamos como parámetro el json de entrada
        try
        {
            strData = URLEncoder.encode(strData, ENCODING_UTF8);
            HttpGet httpGet = new HttpGet("http://172.22.45.16:8080/api/rest/api/rviatranslatejson/" + strData);
            httpGet.addHeader("Content-Type", CONTEN_TYPE);
            httpGet.addHeader("Authorization", "Bearer "
                    + "RVIASESION=oRWKxG8ezgfj8c58rzfC0YAcaes8mURor-GBXPNKzma2nVUI3WEE!-1294293488!-772048924");
            // Ejecutamos la petición
            response = httpclient.execute(httpGet);
            // recuperamos la respuesta del response y obtenemos la respuesta enviada.
            writer = new StringWriter();
            br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            while ((output = br.readLine()) != null)
            {
                writer.append(output);
            }
            try
            {
                jsonObject = new JSONObject(writer.toString());
                if (jsonObject.get("response") != null)
                    error = (String) jsonObject.get("response");
                // isError = jsonObject.get("response") != null && jsonObject.get("response").equals("1");
            }
            catch (JSONException ex)
            {
                error = "02";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return response;
    }

    public static boolean isJsonError(String strJson) throws ClientProtocolException, IOException
    {
        boolean isError;
        HttpResponse response;
        JSONObject jsonObject;
        String output;
        // Creamos el request config para poner el timeout de conexión
        RequestConfig.Builder requestBuilder = RequestConfig.custom();
        requestBuilder = requestBuilder.setConnectTimeout(10000);
        HttpClientBuilder builder = HttpClientBuilder.create();
        builder.setDefaultRequestConfig(requestBuilder.build());
        // Creamos el http client
        HttpClient httpclient = builder.build();
        // Creamos la petición POST y enviamos como parámetro el json de entrada
        HttpPost httpPost = new HttpPost(URL_SERVICE);
        httpPost.setEntity(new StringEntity(strJson, ContentType.create(CONTEN_TYPE, ENCODING_UTF8)));
        // Ejecutamos la petición
        response = httpclient.execute(httpPost);
        // recuperamos la respuesta del response y obtenemos la respuesta enviada.
        StringWriter writer = new StringWriter();
        BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        while ((output = br.readLine()) != null)
        {
            writer.append(output);
        }
        // Creamos la petición GET y enviamos como parámetro el json de entrada
        strJson = URLEncoder.encode(strJson, ENCODING_UTF8);
        HttpGet httpGet = new HttpGet(URL_SERVICE + "/" + strJson);
        // httpGet.addHeader("Content-Type", CONTEN_TYPE);
        // Ejecutamos la petición
        response = httpclient.execute(httpGet);
        // recuperamos la respuesta del response y obtenemos la respuesta enviada.
        writer = new StringWriter();
        br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        while ((output = br.readLine()) != null)
        {
            writer.append(output);
        }
        try
        {
            jsonObject = new JSONObject(writer.toString());
            isError = jsonObject.get("response") != null && jsonObject.get("response").equals("1");
        }
        catch (JSONException ex)
        {
            isError = true;
        }
        return isError;
    }

    private static String objectToJson(Object oObject)
    {
        StringBuffer pSB = new StringBuffer();
        String strObjectType = oObject.getClass().getName();
        if ("java.util.Hashtable".equals(strObjectType))
        {
            Hashtable pHashtable = (Hashtable) oObject;
            boolean fFirstElement = true;
            pSB.append("{");
            Enumeration attributeNames = pHashtable.keys();
            while (attributeNames.hasMoreElements())
            {
                String strName = (String) attributeNames.nextElement();
                Object oInnerObject = pHashtable.get(strName);
                String strValue = objectToJson(oInnerObject);
                if (fFirstElement)
                {
                    fFirstElement = false;
                }
                else
                {
                    pSB.append(",");
                }
                pSB.append("\"" + strName + "\":" + strValue);
            }
            pSB.append("}");
            return pSB.toString();
        }
        else if (isArrayObject(oObject))
        {
            boolean fFirstElement = true;
            pSB.append("[");
            for (Object oItem : (List) oObject)
            {
                String strValue = objectToJson(oItem);
                if (fFirstElement)
                {
                    fFirstElement = false;
                }
                else
                {
                    pSB.append(",");
                }
                pSB.append(strValue);
            }
            pSB.append("]");
            return pSB.toString();
        }
        else if ("java.lang.Short".equals(strObjectType) || "java.lang.Integer".equals(strObjectType)
                || "java.lang.Long".equals(strObjectType) || "java.lang.Double".equals(strObjectType)
                || "java.lang.Decimal".equals(strObjectType) || "java.lang.Float".equals(strObjectType)
                || "java.lang.Boolean".equals(strObjectType))
        {
            pSB.append(replaceIlegalCharacters(oObject.toString()));
        }
        else
        {
            pSB.append("\"" + replaceIlegalCharacters(oObject.toString()) + "\"");
        }
        return pSB.toString();
    }

    private static boolean isArrayObject(Object obj)
    {
        boolean fReturn = false;
        if (obj instanceof java.util.List)
        {
            fReturn = true;
        }
        else if (obj.getClass().isArray())
        {
            fReturn = true;
        }
        return fReturn;
    }

    private static String replaceIlegalCharacters(String strInput)
    {
        return strInput.replaceAll("\"", "\\\"").replaceAll("\n", " ").replaceAll("\t", " ").replaceAll("\r", "");
    }
}
