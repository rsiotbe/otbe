package com.rsi.rvia.test;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicLong;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(urlPatterns = { "/testContext.htm" })
public class SampleServlet extends HttpServlet
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private final DateFormat	dateFormat		= SimpleDateFormat.getDateTimeInstance();
	private String					runningVersion	= null;
	private String					startTime		= null;
	private final AtomicLong	visitors			= new AtomicLong(0l);

	@Override
	public void init(ServletConfig servletConfig) throws ServletException
	{
		super.init(servletConfig);
		final Calendar calendar = Calendar.getInstance();
		startTime = dateFormat.format(calendar.getTime());
		runningVersion = servletConfig.getServletContext().getRealPath("/");
	}

	@Override
	protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws ServletException, IOException
	{
		if ("true".equals(httpServletRequest.getParameter("forget")))
		{
			httpServletRequest.getSession(true).invalidate();
			httpServletResponse.sendRedirect(httpServletRequest.getRequestURI());
			return;
		}
		final HttpSession httpSession = httpServletRequest.getSession(true);
		if (httpSession.isNew())
		{
			final Calendar calendar = Calendar.getInstance();
			httpSession.setAttribute("userArrived", dateFormat.format(calendar.getTime()));
		}
		httpServletResponse.setContentType("text/html");
		httpServletResponse.getWriter().println(String.format("<html><head><title>Example</title></head><body>"
				+ "<p>Revision: %s<br/>Start Time: %s<br/>Page Visits: %d<br/>"
				+ "Session ID: %s<br/>Session Started: %s<br/>Real Path: %s</p>" + "<p><a href=\"%s\">Refresh</a> "
				+ "<a href=\"%s?forget=true\">New Session</a></p></body></html>", 
				runningVersion, startTime, visitors.incrementAndGet(), httpSession.getId(), 
				httpSession.getAttribute("userArrived"), 
				httpSession.getServletContext().getRealPath(httpServletRequest.getRequestURL().toString()), 
				httpServletRequest.getRequestURI(), httpServletRequest.getRequestURI()));
	}
}
