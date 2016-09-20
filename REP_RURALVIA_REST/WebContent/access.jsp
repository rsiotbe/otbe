<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
    import="com.rsi.rvia.rest.DDBB.DDBBPoolFactory,
    		com.rsi.rvia.rest.operation.MiqQuests,
		 	java.sql.Connection, java.net.URL"
    %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
<title></title>
<script type="text/javascript" src="https://code.jquery.com/jquery-2.2.4.min.js"></script>
<script type="text/javascript" src="/api/js/manageRequestRviaRest.js"></script>
<script type="text/javascript" src="http://cdn.jsdelivr.net/iframe-resizer/3.5.3/iframeResizer.contentWindow.min.js"></script>
</head>
<%
	MiqQuests pMiqQuests = null;
	String strPathRest = null;
	int nMiqQuestId = 0;
	String strIdMiq = request.getParameter("idMiq");
	try{
		nMiqQuestId = Integer.parseInt(strIdMiq);
	}catch(Exception ex){
		
	}
	
	String strType = request.getParameter("type");
	String strToken = request.getParameter("token");
	if(strType == null){
		strType = "GET";
	}
	if(strIdMiq != null){
		pMiqQuests = MiqQuests.getMiqQuests(nMiqQuestId);
		strPathRest = pMiqQuests.getPathRest();
	}else{
		strPathRest = "Path Error";
	}
	URL resource = getClass().getResource("/");
	String pathApp = resource.getPath();
	
%>
<body>
<script type="text/javascript">
  var data = {};
  var appPath = '/' + window.location.pathname.substr(1).split('/')[0] + '/rest' + '<%=strPathRest%>';
  var method = '<%=strType%>';
 var token = '<%=strToken%>';
<%--   $.ajax({
    url : appPath + '<%=strPathRest%>',
    data : data,
    type : '<%=strType%>',
    success : function(response) {
      $('html').html(response);
    },
    error: function(data, textStatus, errorThrown)
    {
    	//$('html').html(data.responseText);
    	document.write(data.responseText);
    }
  }); --%>
  $('<form action="' + appPath + '" type="' + method + '"><input type="hidden" name="token" value="' + token + '"></form>').appendTo('body').submit();

</script>
</body>
</html>