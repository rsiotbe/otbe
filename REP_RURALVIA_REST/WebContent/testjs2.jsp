<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<script src='https://code.jquery.com/jquery-1.12.4.min.js'/></script>
<!--  <script src='https://code.jquery.com/jquery-2.2.4.min.js'/></script>
<script src='https://code.jquery.com/jquery-3.1.0.min.js'/></script>
-->

<script src='/api/manageRequestRviaRest.js'/></script>
<script src='/api/translateJs.js'/></script>
</head>
<body>
<script type="text/javascript">
function launchpageQuery()
{
	pageQuery('/api/testjs3OK.jsp');
}

function launchGenericAjax()
{
	var jParameters = {url:'/api/testjs3OK.jsp',dataType:'text'};
	$('#result').html(genericAjax(jParameters));
}

</script>

<button onCLick='launchpageQuery()'>pedir pagina</button>
<button onCLick='launchGenericAjax()'>pedir ajax</button>
<div id="result"></div>
</body>
</html>