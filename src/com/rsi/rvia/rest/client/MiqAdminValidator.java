package com.rsi.rvia.rest.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.lang.JoseException;

public class MiqAdminValidator
{
    public static void adminIn(HttpServletRequest pRequest, HttpServletResponse pResponse)
            throws MalformedClaimException, NoSuchAlgorithmException, InvalidKeySpecException, JoseException,
            IOException
    {
        HashMap<String, String> pParamsToInject = new HashMap<String, String>();
        String JWT = pRequest.getParameter("admTk");
        String uri = pRequest.getRequestURI();
        String validos = "u020885-";
        if (JWT == null)
        {
            doURL(pResponse, "/api/access/adminLogin.jsp?requesturi=" + uri);
            return;
        }
        else
        {
            pParamsToInject = ManageJWToken.validateJWT(JWT, "admTk");
            if (validos.indexOf(pParamsToInject.get("user")) == -1)
            {
                pParamsToInject = null;
            }
        }
        if (pParamsToInject == null)
        {
            doURL(pResponse, "/api/access/adminLogin.jsp?requesturi=" + uri);
        }
    }

    public static String makeHTML(String user, String pass, String action, String ruri, String admTk, String message,
            boolean submit)
    {
        String html;
        String error = "";
        if (!"".equals(message))
        {
            error = "<div> " + message + " </div>";
        }
        if (!submit)
        {
            html = error + "<form action=\"" + action + "\" method=\"post\">  " + "<input type=\"hidden\" value=\""
                    + ruri + "\" name=\"requesturi\">  " + "<input type=\"hidden\" value=\"" + admTk
                    + "\" name=\"admTk\">       "
                    + "<p>User: <input type=\"text\" name=\"user\" value\"\"></p>        "
                    + "<p>Pass: <input type=\"password\" name=\"pass\" value\"\"></p>    "
                    + "<p><input type=\"submit\" value=\"enviar\"></p>         " + "</form> ";
        }
        else
        {
            html = "<form action=\"" + action + "\" method=\"post\"> " + "<input type=\"hidden\" value=\"" + admTk
                    + "\" name=\"admTk\"> " + "</form>                                                 "
                    + "<script>document.getElementsByTagName('form')[0].submit()</script>";
        }
        return html;
    }

    public static String doLogin(HttpServletRequest pRequest, HttpServletResponse pResponse) throws JoseException,
            IOException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        String strRequestUri = pRequest.getParameter("requesturi");
        String strUser = pRequest.getParameter("user");
        String strPass = pRequest.getParameter("pass");
        if (strRequestUri == null)
        {
        }
        String inLogin = "http://intranetrsi.caja.rural/names.nsf?Login&Username=" + strUser + "&Password=" + strPass;
        HttpGet httpGet = new HttpGet(inLogin);
        // httpGet.setEntity(stringEntity);
        httpGet.addHeader("Accept", "text/html");
        // Execute and get the response.
        HttpClient httpClient = HttpClientBuilder.create().build();
        // HttpClient httpClient = new HttpClient();
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        String strResponse = null;
        String JWT = "";
        if (entity != null)
        {
            strResponse = EntityUtils.toString(entity);
        }
        int loginok = strResponse.indexOf("/intranet.nsf/index.htm");
        if (loginok >= 0)
        {
            // Todo en orden. Redireccionando a pagina valida
            // pLog.warning("->>>>>>>>>>>>>>>>>>>> Error en el servicio de login");
            HashMap<String, String> claims = new HashMap<String, String>();
            claims.put("user", strUser);
            JWT = ManageJWToken.generateJWT(claims, "admTk");
            return makeHTML("", "", strRequestUri, strRequestUri, JWT, "", true);
        }
        return makeHTML("", "", "", strRequestUri, JWT, "<p>Donde vas pájaro ?</p><p>Que va a ser que aquí no puedes entrar sin el santo y seña !!</p>", false);
    }

    private static void doURL(HttpServletResponse pResponse, String url) throws IOException
    {
        pResponse.sendRedirect(url);
    }
}
