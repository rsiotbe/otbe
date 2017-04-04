package com.rsi.rvia.rest.security;

import java.io.IOException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.Constants;
import com.rsi.rvia.rest.client.ManageJWToken;
import com.rsi.rvia.rest.tool.AppConfiguration;

public class IdentityProviderRVIA implements IdentityProvider
{
    private static Logger pLog = LoggerFactory.getLogger(IdentityProviderRVIA.class);

    public String generateJWT(HashMap<String, String> claims, String strTokenId) throws Exception
    {
        return ManageJWToken.generateJWT(claims, strTokenId);
    };

    public HashMap<String, String> validateJWT(String jwt, String strTokenId) throws Exception
    {
        return ManageJWToken.validateJWT(jwt, strTokenId);
    };

    public HashMap<String, String> doLogin(HttpServletRequest pRequest) throws ClientProtocolException, IOException
    {
        String usuario = pRequest.getParameter("usuario");
        String documento = pRequest.getParameter("documento");
        String password = pRequest.getParameter("password");
        String SOAPEndPoint = "http://soa.risa";
        String entorno = AppConfiguration.getInstance().getProperty(Constants.ENVIRONMENT);
        if (entorno.equals("TEST"))
        {
            usuario = "03052445";
            documento = "33334444S";
            password = "03052445";
            SOAPEndPoint = "http://soa02.risa";
        }
        String strBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ee=\"http://www.ruralserviciosinformaticos.com/empresa/EE_AutenticarUsuario/\">"
                + "   <soapenv:Header/>                                     "
                + "   <soapenv:Body>                                        "
                + "      <ee:EE_I_AutenticarUsuario>                        "
                + "         <ee:usuario>"
                + usuario
                + "</ee:usuario>        "
                + "         <ee:password>"
                + password
                + "</ee:password>     "
                + "         <ee:documento>"
                + documento
                + "</ee:documento>  "
                + "      </ee:EE_I_AutenticarUsuario>                       "
                + "   </soapenv:Body>                                       "
                + "</soapenv:Envelope>                                      ";
        StringEntity stringEntity = new StringEntity(strBody, "UTF-8");
        stringEntity.setChunked(true);
        // Request parameters and other properties.
        HttpPost httpPost = new HttpPost(SOAPEndPoint + "/SOA_Wallet/Empresa/PS/SE_WAL_AutenticarUsuario");
        httpPost.setEntity(stringEntity);
        httpPost.addHeader("Accept", "text/xml");
        httpPost.addHeader("SOAPAction", "");
        // Execute and get the response.
        HttpClient httpClient = HttpClientBuilder.create().build();
        // HttpClient httpClient = new HttpClient();
        HttpResponse response = httpClient.execute(httpPost);
        HttpEntity entity = response.getEntity();
        String strResponse = null;
        if (entity != null)
        {
            strResponse = EntityUtils.toString(entity);
        }
        pLog.info("Respuesta del servicio de login: " + strResponse);
        strResponse = strResponse.replace("\n", "");
        String codRetorno = strResponse.replaceAll("^.*<ee:codigoRetorno>([^<]*)</ee:codigoRetorno>.*$", "$1");
        if (Integer.parseInt(codRetorno) == 0)
        {
            if (entorno.equals("TEST"))
            {
                HashMap<String, String> fields = new HashMap<String, String>();
                fields.put("codEntidad", "3076");
                fields.put("idInternoPe", "1834908");
                fields.put("codTarjeta", "307671667");
                return fields;
            }
            else
            {
                return null;
            }
        }
        else
        {
            HashMap<String, String> fields = new HashMap<String, String>();
            String codEntidad = strResponse.replaceAll("^.*<ee:entidad>([^<]*)</ee:entidad>.*$", "$1");
            String idInternoPe = strResponse.replaceAll("^.*<ee:idInternoPe>([^<]*)</ee:idInternoPe>.*$", "$1");
            String nTarjeta = strResponse.replaceAll("^.*<ee:numeroTarjeta>([^<]*)</ee:numeroTarjeta>.*$", "$1");
            codEntidad = codEntidad.trim();
            idInternoPe = idInternoPe.trim();
            nTarjeta = nTarjeta.trim();
            if (entorno.equals("TEST"))
            {
                fields.put("codEntidad", "3076");
                fields.put("idInternoPe", "1834908");
                fields.put("codTarjeta", "307671667");
            }
            else
            {
                fields.put("codEntidad", codEntidad.replace(" ", ""));
                fields.put("idInternoPe", idInternoPe.replace(" ", ""));
                fields.put("codTarjeta", nTarjeta.replace(" ", ""));
            }
            return fields;
        }
    }
}
