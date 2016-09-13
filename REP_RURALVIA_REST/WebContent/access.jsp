<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
    		com.rsi.rvia.rest.operation.MiqQuests,
		 	java.sql.Connection"
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<title></title>
<script type="text/javascript" src="https://code.jquery.com/jquery-2.2.4.min.js"></script>
<script type="text/javascript" src="/api/js/manageRequestRviaRest.js"></script>
<script type="text/javascript" src="/http://cdn.jsdelivr.net/iframe-resizer/3.5.3/iframeResizer.contentWindow.min.js"></script>
</head>
<%
	MiqQuests pMiqQuests = null;
	String strPathRest = null;
	String strIdMiq = request.getParameter("idMiq");
	int nMiqQuestId = Integer.parseInt(strIdMiq);
	String strType = request.getParameter("type");
	if(strType == null){
		strType = "GET";
	}
	if(strIdMiq != null){
		pMiqQuests = MiqQuests.getMiqQuests(nMiqQuestId);
		strPathRest = pMiqQuests.getPathRest();
	}else{
		strPathRest = "Path Error";
	}
	
%>
<body>
cargando...
<script type="text/javascript">
  var data = {};

  $.ajax({
    url : '<%=strPathRest%>',
    data : data,
    type : '<%=strType%>',
    success : function(response) {
      $('html').html(response);
    },
    error: function()
    {
    	$('html').text('Error al cargar la operativa');
    }
  });

</script>
</body>
</html>