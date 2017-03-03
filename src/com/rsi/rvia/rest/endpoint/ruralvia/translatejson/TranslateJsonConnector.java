package com.rsi.rvia.rest.endpoint.ruralvia.translatejson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.json.internal.json_simple.parser.JSONParser;
import org.jose4j.json.internal.json_simple.parser.ParseException;

/**
 * The Class TranslateJsonConnector. Esta clase establece una conexión con el entorno de rural via REST y comprueba si
 * un json enviado tiene algún código de error
 */
public class TranslateJsonConnector
{
    /** The Constant URL_SERVICE. */
    private static final String URL_SERVICE   = "http://localhost:8080/OPC/rest/api/rviatranslatejson";
    /** The Constant CONTEN_TYPE. */
    private static final String CONTEN_TYPE   = "application/json";
    /** The Constant ENCODING_UTF8. */
    private static final String ENCODING_UTF8 = "UTF-8";

    /**
     * Checks if is json error.
     * 
     * @param strJson
     *            the input json
     * @return true, if is json error
     * @throws ClientProtocolException
     *             the client protocol exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     * @throws ParseException
     *             the parse exception
     */
    public static boolean isJsonError(String strJson) throws ClientProtocolException, IOException, ParseException
    {
        boolean isError;
        HttpResponse response;
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
        String output;
        while ((output = br.readLine()) != null)
        {
            writer.append(output);
        }
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(writer.toString());
        isError = jsonObject.get("response") != null && jsonObject.get("response").equals("1");
        return isError;
    }
}
