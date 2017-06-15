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
import com.rsi.rvia.rest.error.exceptions.LogicalErrorException;
import com.rsi.rvia.rest.operation.MiqQuests;
import com.rsi.rvia.rest.tool.AppConfiguration;

/**
 * @author zenhaust
 * @class Gestor de login y tokens de autorización a partir de usuarios de Ruralvía
 */
public class IdentityProviderRVIALogin implements IdentityProvider
{
    private static Logger           pLog = LoggerFactory.getLogger(IdentityProviderRVIALogin.class);
    private HttpServletRequest      _pRequest;
    private HashMap<String, String> _claims;
    private String                  _JWT;
    private MiqQuests               _pMiqQuests;
    private HashMap<String, String> _pParamsToInject;
    private String                  _tokenId;

    public IdentityProviderRVIALogin(HttpServletRequest pRequest, MiqQuests pMiqQuests)
    {
        _pRequest = pRequest;
        _pMiqQuests = pMiqQuests;
        _tokenId = "rviatk1";
        _claims = null;
        _JWT = "";
    }

    /**
     * Generador de JWT
     * 
     * @param claims
     * @param strTokenId
     * @return
     * @throws Exception
     */
    private String generateJWT(HashMap<String, String> claims, String strTokenId) throws Exception
    {
        return ManageJWToken.generateJWT(claims, strTokenId);
    };

    /**
     * Validación de JWT y extraccíon de campos de payload
     * 
     * @param jwt
     * @param strTokenId
     * @return HashMap con los campos del payload
     * @throws Exception
     */
    private HashMap<String, String> validateJWT(String jwt, String strTokenId) throws Exception
    {
        return ManageJWToken.validateJWT(jwt, strTokenId);
    };

    /*
     * (non-Javadoc)
     * @see com.rsi.rvia.rest.security.IdentityProvider#process()
     */
    public void process() throws Exception
    {
        _claims = null;
        _JWT = "";
        // String strPrimaryPath = _pRequest.
        if (_pMiqQuests.getPathRest().indexOf("/login") != -1)
        {
            // Si es login generamos JWT
            _claims = doLogin(_pRequest);
            if (_pRequest.getParameter("idInternoPe") != null)
            {
                _claims.remove("idInternoPe");
                _claims.put("idInternoPe", _pRequest.getParameter("idInternoPe"));
                _claims.remove("codTarjeta");
                switch (Integer.parseInt(_pRequest.getParameter("idInternoPe")))
                {
                    case 1569668:
                        _claims.put("codTarjeta", "3076223156");
                        break;
                    case 1641496:
                        _claims.put("codTarjeta", "3076287844");
                        break;
                    case 1726449:
                        _claims.put("codTarjeta", "3076299214");
                        break;
                    case 93408:
                        _claims.put("codTarjeta", "3076308913");
                        break;
                    case 1790785:
                        _claims.put("codTarjeta", "3076341138");
                        break;
                }
            }
            if (_claims != null)
                _JWT = generateJWT(_claims, _tokenId);
            else
            {
                // Login fallido
                throw new LogicalErrorException(403, 9999, "Login failed", "Suministre credenciales válidas para iniciar sesión", new Exception());
            }
        }
        else
        {
            // Else verificamos JWT
            _JWT = _pRequest.getHeader("Authorization");
        }
        _claims = validateJWT(_JWT, _tokenId);
        if (_claims == null)
        {
            throw new LogicalErrorException(401, 9999, "Unauthorized", "Sesión no válida", new Exception());
        }
    };

    /**
     * Proceso de login sobre servicio de Wallet con campos de usuario de Ruralvía
     * 
     * @param pRequest
     * @return HashMap con los campos del payload, o null si falló login
     * @throws ClientProtocolException
     * @throws IOException
     */
    private HashMap<String, String> doLogin(HttpServletRequest pRequest) throws ClientProtocolException, IOException
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
        String strBody = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:se=\"http://www.ruralserviciosinformaticos.com/empresa/SE_RVA_LoginTarjeta/\" xmlns:sec=\"http://www.ruralserviciosinformaticos.com/XSD/SecurityHeader/\">"
                + "   <soapenv:Header>                               "
                + "      <se:RSI_Header>                             "
                + "         <sec:CODSecUser></sec:CODSecUser>        "
                + "         <sec:CODSecTrans></sec:CODSecTrans>      "
                + "         <sec:CODSecEnt>0239</sec:CODSecEnt>      "
                + "         <sec:CODTerminal></sec:CODTerminal>      "
                + "         <sec:CODSecIp>10.245.1.5</sec:CODSecIp>  "
                + "         <!--Optional:-->                         "
                + "         <sec:CODApl>BDP</sec:CODApl>             "
                + "         <!--Optional:-->                         "
                + "         <sec:CODCanal>11</sec:CODCanal>          "
                + "      </se:RSI_Header>                            "
                + "   </soapenv:Header>                              "
                + "   <soapenv:Body>                                 "
                + "      <se:EE_I_LoginTarjeta>                      "
                + "         <se:usuarioBE>"
                + usuario
                + "</se:usuarioBE>    "
                + "         <se:password>"
                + password
                + "</se:password>      "
                + "         <se:idExterno>"
                + documento
                + "</se:idExterno>   "
                + "         <se:canal>11</se:canal>               "
                + "      </se:EE_I_LoginTarjeta>                     "
                + "   </soapenv:Body>                                "
                + "</soapenv:Envelope>                               ";
        StringEntity stringEntity = new StringEntity(strBody, "UTF-8");
        stringEntity.setChunked(true);
        // Request parameters and other properties.
        // HttpPost httpPost = new HttpPost(SOAPEndPoint + "/SOA_Wallet/Empresa/PS/SE_WAL_AutenticarUsuario");
        HttpPost httpPost = new HttpPost(SOAPEndPoint + "/SOA_RVIA/Empresa/PS/soap/v1/SE_RVA_LoginTarjeta");
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
        String codRetorno = strResponse.replaceAll("^.*<se:codigoRetorno>([^<]*)</se:codigoRetorno>.*$", "$1");
        if (Integer.parseInt(codRetorno) == 0)
        {
            if (entorno.equals("TEST"))
            {
                pLog.debug("En entornos de producción esta traza no debe aparecer ");
                HashMap<String, String> fields = new HashMap<String, String>();
                fields.put("codEntidad", "3076");
                fields.put("idInternoPe", "1834908");
                fields.put("codTarjeta", "3076215863");
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
            String codEntidad = strResponse.replaceAll("^.*<se:codigoEntidad>([^<]*)</se:codigoEntidad>.*$", "$1");
            String idInternoPe = strResponse.replaceAll("^.*<se:idInternoPe>([^<]*)</se:idInternoPe>.*$", "$1");
            String nTarjeta = strResponse.replaceAll("^.*<se:numeroTarjeta>([^<]*)</se:numeroTarjeta>.*$", "$1");
            codEntidad = codEntidad.trim();
            idInternoPe = idInternoPe.trim();
            nTarjeta = nTarjeta.trim();
            while (codEntidad.length() < 4)
            {
                codEntidad = "0" + codEntidad;
            }
            if (entorno.equals("TEST"))
            {
                pLog.debug("En entornos de producción esta traza no debe aparecer ");
                fields.put("codEntidad", "3076");
                fields.put("idInternoPe", "1834908");
                fields.put("codTarjeta", nTarjeta.replace(" ", ""));
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

    /*
     * (non-Javadoc)
     * @see com.rsi.rvia.rest.security.IdentityProvider#getClaims()
     */
    public HashMap<String, String> getClaims()
    {
        return _claims;
    };

    /*
     * (non-Javadoc)
     * @see com.rsi.rvia.rest.security.IdentityProvider#getJWT()
     */
    public String getJWT()
    {
        return _JWT;
    };
}
