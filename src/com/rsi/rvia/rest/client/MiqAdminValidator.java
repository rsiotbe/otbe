package com.rsi.rvia.rest.client;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import javax.servlet.http.Cookie;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MiqAdminValidator
{
    private static Logger pLog = LoggerFactory.getLogger(MiqAdminValidator.class);

    public static void adminIn(HttpServletRequest pRequest, HttpServletResponse pResponse)
            throws MalformedClaimException, NoSuchAlgorithmException, InvalidKeySpecException, JoseException,
            IOException
    {
        HashMap<String, String> pParamsToInject = new HashMap<String, String>();
        String JWT = pRequest.getParameter("admTk");
        Cookie cookies[];
        String uri = pRequest.getRequestURI();
        String validos = "u020885u020976u028879";
        cookies = pRequest.getCookies();
        if (cookies != null)
        {
            if (JWT == null)
            {
                for (int i = 0; i < cookies.length; i++)
                {
                    String name = cookies[i].getName();
                    if ("admTk".equals(name))
                    {
                        JWT = cookies[i].getValue();
                        break;
                    }
                }
            }
        }
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
        String clsname = "angryok";
        String html = "<div class=\"container\">";
        String strBocata = "";
        strBocata = "                <div class=\"c12-12-prev msg1\">                                                                                 "
                + "                     Who are you?"
                + "                </div>                                                                                                           ";
        if (!"".equals(message))
        {
            clsname = "angryko";
            strBocata = "                <div class=\"c12-12-prev msg2\">                                                                                 "
                    + message
                    + "                </div>                                                                                                           ";
        }
        if (!submit)
        {
            html = html
                    + "    <div class=\"dash-body\">                                                                                                    "
                    + "        <div class=\"c1-n c12-12-prev\">                                                                                         "
                    + "            <div class=\"c1-n c6-12-prev "
                    + clsname
                    + "\"></div>                                                                          "
                    + "            <div class=\"c1-n c6-12-post bocata\">                                                                               "
                    + strBocata
                    + "            </div>                                                                                                               "
                    + "            <div class=\"c1-n c4-12-prev\">&nbsp;</div>                                                                          "
                    + "            <div class=\"c1-n c4-12-prev\">                                                                                      "
                    + "                <form action=\""
                    + action
                    + "\"  method=\"post\">                                                                              "
                    + "                    <input type=\"hidden\" value=\""
                    + ruri
                    + "\"  name=\"requesturi\">                                                      "
                    + "                    <input type=\"hidden\" value=\""
                    + admTk
                    + "\"  name=\"admTk\">                                                           "
                    + "                    <p><label>User:</label> <input type=\"text\" placeholder=\"Usuario\" name=\"user\" value=\"\"></p>           "
                    + "                    <p><label>Pass:</label> <input type=\"password\" placeholder=\"Password\" name=\"pass\" value=\"\"></p>      "
                    + "                    <p><input type=\"submit\" value=\"Take me inside\"></p><br><br>                                              "
                    + "                </form>                                                                                                          "
                    + "            </div>                                                                                                               "
                    + "            <div class=\"c1-n c4-12-post\">&nbsp;</div>                                                                          "
                    + "        </div>                                                                                                                   "
                    + "    </div>                                                                                                                       ";
        }
        else
        {
            html = html + "<form action=\"" + action + "\" method=\"post\"> " + "<input type=\"hidden\" value=\""
                    + admTk + "\" name=\"admTk\"> " + "</form>                                                 "
                    + "<script>document.getElementsByTagName('form')[0].submit()</script>";
        }
        html = html + "</div>";
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
            Cookie cookie = new Cookie("admTk", JWT);
            cookie.setMaxAge(60 * 60 * 24); // 1 hour
            cookie.setPath("/api");
            pResponse.addCookie(cookie);
            return makeHTML("", "", strRequestUri, strRequestUri, JWT, "", true);
        }
        return makeHTML("", "", "", strRequestUri, JWT, "Donde crees que vas pájaro?<br><br>Que va a ser que aquí no puedes entrar sin el santo y seña !!", false);
    }

    private static void doURL(HttpServletResponse pResponse, String url) throws IOException
    {
        pResponse.sendRedirect(url);
    }
}
