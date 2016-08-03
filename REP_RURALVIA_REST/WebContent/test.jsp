<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script src='https://code.jquery.com/jquery-3.1.0.min.js'/></script>
</head>
<body>
<script type="text/javascript">
	function showVersion()
	{
		if (typeof jQuery != 'undefined') {  
		    // jQuery is loaded => print the version
		    alert(jQuery.fn.jquery);
		}
	}
</script>
<button onclick='showVersion()'> comprobar version</button>
<iframe id="proxyIframe" src="/api/test2.jsp" style="height:600px; width:800px;border: 1px black dotted;"></iframe>

</body>
</html>