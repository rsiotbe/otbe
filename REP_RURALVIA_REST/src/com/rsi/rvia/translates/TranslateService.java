package com.rsi.rvia.translates;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class translateService
 */
public class TranslateService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final String IDS_PARAM = "id";
	private static final String IDS_PARAM_SEP = ",";
	private static final String LANGUAGE_PARAM = "lang";
      
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TranslateService() {
        super();
    }


	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		String strJSONReturn;
		String strIds;
		String strlanguage;
		String[] astrIds; 
		Hashtable<String, String> htTranslates;
		strIds = request.getParameter(IDS_PARAM);
		strlanguage = request.getParameter(LANGUAGE_PARAM);
		if(IDS_PARAM == null || IDS_PARAM.trim().isEmpty())
		{
			response.sendError(HttpServletResponse.SC_NO_CONTENT);
			return;
		}
		astrIds = strIds.split(IDS_PARAM_SEP);
		htTranslates = TranslateProcessor.processIds(astrIds, strlanguage);
		strJSONReturn = getJsonResult(htTranslates);
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().print(strJSONReturn);
	}
	
	
	private String getJsonResult(Hashtable<String, String> htTranslates)
	{
		boolean fFirstElement = true;
		StringBuffer pSB = new StringBuffer();
		pSB.append("{");
	   Iterator<Entry<String, String>> pIterator = htTranslates.entrySet().iterator();
	   while (pIterator.hasNext()) 
	   {
	   	if(fFirstElement)
	   		fFirstElement = false;
	   	else
	   		pSB.append(",");
		  Map.Entry<String, String> pair = (Entry<String, String>) pIterator.next();
		  pSB.append("\"" + pair.getKey() + "\":\"" +  pair.getValue().replace("\"", "\\\"")+ "\"");
	   }
		pSB.append("}");
		return pSB.toString();
	}
}
