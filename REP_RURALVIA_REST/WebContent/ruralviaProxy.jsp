<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%
		String strId;
		int nMiqId;
		MiqQuests pMiqQuests;
		try
		{		
			strId = request.getParameter("id");
			nMiqId = Integer.parseInt(strId);
			pMiqQuests = MiqQuests.getMiqQuests(nMiqId);
		}
		catch (Exception ex)
		{
			pLog.Error("Se ha producido un error obtener la dirección rest de la petición recibida desde ruralvia", ex);
		}
		%>    
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

</body>
</html>