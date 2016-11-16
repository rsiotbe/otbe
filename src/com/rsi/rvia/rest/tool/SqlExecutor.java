package com.rsi.rvia.rest.tool;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.rsi.rvia.rest.client.MiqAdminValidator;

public class SqlExecutor
{
    private static Logger pLog = LoggerFactory.getLogger(MiqAdminValidator.class);

    public static String exec(HttpServletRequest pRequest, HttpServletResponse pResponse)
            throws MalformedClaimException, NoSuchAlgorithmException, InvalidKeySpecException, JoseException,
            IOException
    {
        String querys = pRequest.getParameter("code");
        if (querys != null)
        {
            if ("".equals(querys.trim()))
            {
                return "Nada que procesar";
            }
            return processQuerys(querys);
        }
        return null;
    }

    private static String processQuerys(String querys)
    {
        String retorno = "No se ha procesado la solicitud";
        querys = querys.replaceAll("\"", "\"");
        return retorno;
    }
}
