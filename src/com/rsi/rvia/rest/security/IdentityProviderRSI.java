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
 * @class Gestor de login y tokens de autorización a partir de usuarios existentes en LDAP, y válidos para acceso a
 *        Intranet
 */
public class IdentityProviderRSI implements IdentityProvider
{
    private static Logger           pLog = LoggerFactory.getLogger(IdentityProviderRSI.class);
    private HttpServletRequest      _pRequest;
    private HashMap<String, String> _claims;
    private String                  _JWT;
    private MiqQuests               _pMiqQuests;
    private HashMap<String, String> _pParamsToInject;
    private String                  _tokenId;

    /**
     * @param pRequest
     * @param pMiqQuests
     */
    public IdentityProviderRSI(HttpServletRequest pRequest, MiqQuests pMiqQuests)
    {
        _pRequest = pRequest;
        _pMiqQuests = pMiqQuests;
        _tokenId = "RSItk1";
        _claims = null;
        _JWT = "";
    }

    /**
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
     * @param jwt
     * @param strTokenId
     * @return
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
        _pParamsToInject = validateJWT(_JWT, _tokenId);
        if (_pParamsToInject == null)
        {
            throw new LogicalErrorException(401, 9999, "Unauthorized", "Sesión no válida", new Exception());
        }
    };

    /**
     * Proceso de login según campos del request
     * 
     * @param pRequest
     * @return
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
