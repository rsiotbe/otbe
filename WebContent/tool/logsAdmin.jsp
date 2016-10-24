<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@page import="ch.qos.logback.core.Appender"%>
<%@page import="java.util.Iterator"%>
<%@page import="ch.qos.logback.classic.Level"%>
<%@page import="ch.qos.logback.classic.Logger"%>
<%@page import="java.util.List"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="ch.qos.logback.classic.LoggerContext"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Set"%>
<%@page import="java.util.Arrays"%>
<%@page import ="ch.qos.logback.classic.spi.ILoggingEvent" %>
<%
	long beginPageLoadTime = System.currentTimeMillis();
%>
<html>
<head>
<title>Adminsitración de Logs RVIA REST</title>
<style type="text/css">
<!--
#content {
	margin: 0px;
	padding: 0px;
	text-align: center;
	width: 100%;
}

body {
	position: relative;
	color: #000;
	font-family: Arial, "sans-serif";
	padding: 5px 5px 5px;
}

h1 {
	margin-top: 18px;
	color: #007557;
	font-size: 1.5 em;
}

h2 {
	margin-top: 10px;
	text-align: left;
	font-size: 0.75 em;
}

a,a:link,a:visited,a:active {
	text-decoration: none;
	text-transform: uppercase;
}

table {
	width: 100%;
	background-color: #FFFFFF;
	padding: 3px;
	border: 0px;
}

th {
	font-size: 0.8em;
	background-color: #000;
	color: #FFF;
	padding-left: 5px;
	text-align: center;
	border: 1px solid #FFF;
	white-space: nowrap;
}

td {
	background-color: #fff;
	white-space: nowrap;
} 	

td.center {
	background-color: #fff;
	text-align: center;
	white-space: nowrap;
}

.filterForm {
	background-color: #EAEAEA;
	color: #3C3B3B;
	padding-left: 20px;
	text-align: left;
	white-space: nowrap;
}

.filterText {
	font-size: 1em;
	background-color: #fff;
	color: #000;
	text-align: left;
	border: 1px solid #ccc;
	white-space: nowrap;
}

.filterButton {
	background-color: #71c100;
    border: none;
    color: white;
    padding: 10px 15px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 14px;
    margin: 4px 2px;
    cursor: pointer;
    width: 125px;    
}
.filterButton:hover:enabled {
    background-color: #2ABB04;
    font-style: italic;
}

.smallButton {
	background-color: #71c100;
    border: none;
    color: white;
    padding: 4px 8px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 10px;
    margin: 2px 1px;
    cursor: pointer;
    width: 60px;    
}
.smallButton:hover:enabled {
    background-color: #2ABB04;
    font-style: italic;
}
.smallButtonDisabled {
    background-color: #5555CC;
    font-weight: bold;
    border: none;
    color: white;
    padding: 4px 8px;
    text-align: center;
    text-decoration: none;
    display: inline-block;
    font-size: 10px;
    margin: 2px 1px;
    cursor: pointer;
    width: 60px;   
}
-->
</style>
</head>
<body onLoad="javascript:document.logFilterForm.logNameFilter.focus();">

	<%
		String containsFilter = "Contiene";
		String beginsWithFilter = "Comienza por...";
		String[] logLevels = { "trace", "debug", "info", "warn", "error", "all", "off" };
		String targetOperation = (String) request.getParameter("operation");
		String targetLogger = (String) request.getParameter("logger");
		String targetLogLevel = (String) request.getParameter("newLogLevel");
		String logNameFilter = (String) request.getParameter("logNameFilter");
		String logNameFilterType = (String) request.getParameter("logNameFilterType");
	%>
	<div id="content">
		<h1>Adminsitración de Logs RVIA REST</h1>

		<div class="filterForm">

			<form action="logsAdmin.jsp" name="logFilterForm">
				Filtrar por:&nbsp;&nbsp; <input name="logNameFilter" type="text"
					size="50" value="<%=(logNameFilter == null ? "" : logNameFilter)%>"
					class="filterText" /> <input name="logNameFilterType" type="submit"
					value="<%=beginsWithFilter%>" class="filterButton" />&nbsp; <input
					name="logNameFilterType" type="submit" value="<%=containsFilter%>"
					class="filterButton" />&nbsp; <input name="logNameClear"
					type="button" value="Borrar" class="filterButton"
					onmousedown='javascript:document.logFilterForm.logNameFilter.value="";' />
				<input name="logNameReset" type="reset" value="Reset"
					class="filterButton" />

				<param name="operation" value="changeLogLevel" />
			</form>
		</div>

		<table cellspacing="1">
			<tr>
				<th width="25%">Logger</th>

				<th width="25%">Appenders</th>
				<th width="15%">Nivel de log</th>

				<th width="35%">Cambiar nivel a</th>
			</tr>

			<%
				LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
				List<Logger> loggers = loggerContext.getLoggerList();
				HashMap<String, Logger> loggersMap = new HashMap<String, Logger>(128);
				Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
				if (!loggersMap.containsKey(rootLogger.getName()))
				{
					loggersMap.put(rootLogger.getName(), rootLogger);
				}
				for (int i = 0; i < loggers.size(); i++)
				{
					Logger logger = (Logger) loggers.get(i);
					if (logNameFilter == null || logNameFilter.trim().length() == 0)
					{
						loggersMap.put(logger.getName(), logger);
					}
					else if (containsFilter.equals(logNameFilterType))
					{
						if (logger.getName().toUpperCase().indexOf(logNameFilter.toUpperCase()) >= 0)
						{
							loggersMap.put(logger.getName(), logger);
						}
					}
					else
					{
						// Either was no filter in IF, contains filter in ELSE IF, or begins with in ELSE
						if (logger.getName().startsWith(logNameFilter))
						{
							loggersMap.put(logger.getName(), logger);
						}
					}
				}
				Set<String> loggerKeys = loggersMap.keySet();
				String[] keys = new String[loggerKeys.size()];
				keys = (String[]) loggerKeys.toArray(keys);
				Arrays.sort(keys, String.CASE_INSENSITIVE_ORDER);
				for (int i = 0; i < keys.length; i++)
				{
					Logger logger = (Logger) loggersMap.get(keys[i]);
					// MUST CHANGE THE LOG LEVEL ON LOGGER BEFORE GENERATING THE LINKS AND THE
					// CURRENT LOG LEVEL OR DISABLED LINK WON'T MATCH THE NEWLY CHANGED VALUES
					if ("changeLogLevel".equals(targetOperation) && targetLogger.equals(logger.getName()))
					{
						Logger selectedLogger = (Logger) loggersMap.get(targetLogger);
						selectedLogger.setLevel(Level.toLevel(targetLogLevel));
					}
					String loggerName = null;
					String loggerEffectiveLevel = null;
					String loggerAppenders = "";
					if (logger != null)
					{
						loggerName = logger.getName();
						loggerEffectiveLevel = String.valueOf(logger.getEffectiveLevel());
						// appenders
						Iterator<Appender<ILoggingEvent>> iterator = logger.iteratorForAppenders();
						while (iterator.hasNext())
						{
							loggerAppenders += (loggerAppenders.length() != 0 ? ":" : "")
									+ ((Appender<ILoggingEvent>) iterator.next()).getName();
						}
					}
			%>
			<tr>
				<td><%=loggerName%></td>

				<td><%=loggerAppenders%></td>
				<td class="center"><%=loggerEffectiveLevel%></td>
				<td class="center">
					<%
						for (int cnt = 0; cnt < logLevels.length; cnt++)
							{
								String url = "logsAdmin.jsp?operation=changeLogLevel&logger=" + loggerName + "&newLogLevel="
										+ logLevels[cnt] + "&logNameFilter=" + (logNameFilter != null ? logNameFilter : "")
										+ "&logNameFilterType=" + (logNameFilterType != null ? logNameFilterType : "");
								if (logger.getLevel() == Level.toLevel(logLevels[cnt])
										|| logger.getEffectiveLevel() == Level.toLevel(logLevels[cnt]))
								{
					%> <a class='smallButtonDisabled' href='<%=url%>'><%=logLevels[cnt]%></a> <%
						}
								else
								{
					%> <a class='smallButton' href='<%=url%>'><%=logLevels[cnt]%></a>
				&nbsp; <%
 	}
 		}
 %>
				</td>
			</tr>

			<%
				}
			%>
		</table>
	</div>
</body>
</html>
