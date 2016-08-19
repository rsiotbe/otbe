package com.rsi.rvia.rest.tool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogController
{
	private final String			PATH_LOG_FOLDER	= "D:" + File.separator + "logs";
	private final String			PATH_LOG_FILE		= "D:" + File.separator + "logs" + File.separator + "log_rest.log";
	private final long			MAX_FILE_LINES		= 5000;
	private final DateFormat	pHourdateFormat	= new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");

	public LogController()
	{
		long lNumLines = 0;
		lNumLines = getFileLines();
		if (MAX_FILE_LINES <= lNumLines)
		{
			restartFile();
		}
	}

	public long getFileLines()
	{
		File pFile = new File(PATH_LOG_FILE);
		String strCadena = "";
		long lNum = 0;
		File pDir = new File(PATH_LOG_FOLDER);
		if (!pDir.exists())
		{
			try
			{
				pDir.mkdir();
			}
			catch (Exception se)
			{
				se.printStackTrace();
			}
		}
		FileReader pFr;
		try
		{
			if (!pFile.exists())
			{
				pFile.createNewFile();
			}
			pFr = new FileReader(pFile);
			BufferedReader pBr = new BufferedReader(pFr);
			while ((strCadena = pBr.readLine()) != null)
			{
				lNum++;
			}
			pBr.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return lNum;
	}

	public void addLog(String strTypeLog, String strLog)
	{
		try
		{
			Date pDate = new Date();
			String strFinalLog = "<log> Date: " + pHourdateFormat.format(pDate) + " -- Type: " + strTypeLog + " -- Log: "
					+ strLog + "</log>\r\n";
			File pDir = new File(PATH_LOG_FOLDER);
			if (!pDir.exists())
			{
				try
				{
					pDir.mkdir();
				}
				catch (Exception se)
				{
					se.printStackTrace();
				}
			}
			File pFile = new File(PATH_LOG_FILE);
			try
			{
				if (!pFile.exists())
				{
					pFile.createNewFile();
				}
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pFile, true), "UTF-8"));
				;
				bw.write(strFinalLog);
				bw.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public void restartFile()
	{
		File pDir = new File(PATH_LOG_FOLDER);
		if (!pDir.exists())
		{
			try
			{
				pDir.mkdir();
			}
			catch (Exception se)
			{
				se.printStackTrace();
			}
		}
		File pFile = new File(PATH_LOG_FILE);
		try
		{
			if (!pFile.exists())
			{
				pFile.createNewFile();
			}
			FileWriter pFw = new FileWriter(pFile);
			BufferedWriter bw = new BufferedWriter(pFw);
			;
			bw.write("");
			bw.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
