package com.rsi.rvia.mail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Email
{
	private static Logger					pLog			= LoggerFactory.getLogger(Email.class);
	private Properties						pProperties		= null;
	private String							strEmailFrom	= null;
	private ArrayList<String>				alTo			= null;
	private ArrayList<String>				alCC			= null;
	private ArrayList<String>				alBCC			= null;
	private String							strSubject		= null;
	private String							strHtmlMessage	= null;									;
	private ArrayList<EmailAttachObject>	alAttachFile	= null;									;

	/**
	 * Fija las porpiedades necesarias para ejecutar el envio. Al menos la configuración debe contener las claves:
	 * mail.transport.protocol, mail.smtp.host, mail.smtp.port, mail.smtp.connectiontimeout, mail.smtp.timeout,
	 * mail.smtp.writetimeout y mail.smtp.from
	 * 
	 * @param pProperties
	 *            Objeto que contiene las propiedades cargadas
	 */
	public void setConfig(Properties pProperties)
	{
		this.pProperties = pProperties;
	}

	/**
	 * Fija el remitente del correo, en caso de ser null se coje el que tenga el servicio por defecto configurado
	 * 
	 * @param strEmailFrom
	 */
	public void setFrom(String strEmailFrom)
	{
		this.strEmailFrom = strEmailFrom;
	}

	/**
	 * Añade un destinatario al correo
	 * 
	 * @param strEmailTo
	 *            dirección a añadir
	 */
	public void addTo(String strEmailTo)
	{
		if (alTo == null)
			alTo = new ArrayList<String>();
		alTo.add(strEmailTo);
	}

	/**
	 * Añade varios destinatarios al correo
	 * 
	 * @param alEmailTo
	 *            array con las direcciones a añadir
	 */
	public void addTo(ArrayList<String> alEmailTo)
	{
		if (alTo == null)
			alTo = new ArrayList<String>();
		alTo.addAll(alEmailTo);
	}

	/**
	 * Añade un destinatario en copia al correo
	 * 
	 * @param strEmailCC
	 *            dirección a añadir
	 */
	public void addCC(String strEmailCC)
	{
		if (alCC == null)
			alCC = new ArrayList<String>();
		alCC.add(strEmailCC);
	}

	/**
	 * Añade varios destinatarios en copia al correo
	 * 
	 * @param alEmailCC
	 *            array con las direcciones a añadir
	 */
	public void addCC(ArrayList<String> alEmailCC)
	{
		if (alCC == null)
			alCC = new ArrayList<String>();
		alCC.addAll(alEmailCC);
	}

	/**
	 * Añade un destinatario en copia oculta al correo
	 * 
	 * @param strEmailBCC
	 *            dirección a añadir
	 */
	public void addBCC(String strEmailBCC)
	{
		if (alBCC == null)
			alBCC = new ArrayList<String>();
		alBCC.add(strEmailBCC);
	}

	/**
	 * Añade varios destinatarios en copia oculta al correo
	 * 
	 * @param alEmailBCC
	 *            array con las direcciones a añadir
	 */
	public void addBCC(ArrayList<String> alEmailBCC)
	{
		if (alBCC == null)
			alBCC = new ArrayList<String>();
		alBCC.addAll(alEmailBCC);
	}

	/**
	 * Fija el asunto del correo
	 * 
	 * @param strSubject
	 *            Texto con el asunto del correo
	 */
	public void setSubject(String strSubject)
	{
		this.strSubject = strSubject;
	}

	/**
	 * Fija el contenido del cuerpo del mensaje
	 * 
	 * @param strHtmlMessage
	 *            Contenido Html con el cuerpo del mensaje
	 */
	public void setBodyContent(String strHtmlMessage)
	{
		this.strHtmlMessage = strHtmlMessage;
	}

	/**
	 * Añade un archivo adjunto al correo
	 * 
	 * @param strName
	 *            Nombre del fichero adjunto
	 * @param strMimeType
	 *            MimeType del fichero adjunto
	 * @param abContent
	 *            Array de bytes con el contenido
	 */
	public void AttachedFile(String strName, String strMimeType, byte[] abContent)
	{
		EmailAttachObject pEmailAttachObject = new EmailAttachObject(strName, strMimeType, abContent);
		if (alAttachFile == null)
			alAttachFile = new ArrayList<EmailAttachObject>();
		alAttachFile.add(pEmailAttachObject);
	}

	/**
	 * Añade un archivo adjunto al correo
	 * 
	 * @param pEmailAttachObject
	 *            Objeto que contiene el archivo adjunto
	 */
	public void addAttachedFile(EmailAttachObject pEmailAttachObject)
	{
		if (alAttachFile == null)
			alAttachFile = new ArrayList<EmailAttachObject>();
		alAttachFile.add(pEmailAttachObject);
	}

	/**
	 * Añade archivos adjuntos en bloque
	 * 
	 * @param alEmailAttachObject
	 *            Array con los archivos adjuntos
	 */
	public void addAttachedFile(ArrayList<EmailAttachObject> alEmailAttachObject)
	{
		if (alAttachFile == null)
			alAttachFile = new ArrayList<EmailAttachObject>();
		alAttachFile.addAll(alEmailAttachObject);
	}

	/**
	 * realiza el envío del correo previamente configurado
	 * 
	 * @throws Exception
	 */
	public void send() throws Exception
	{
		if (pProperties == null)
		{
			pLog.error("Error en enviar el correo electrénico. No se han definido las propiedades de configuración del servicio");
			throw new Exception("Error en enviar el correo electrénico. No se han definido las propiedades de configuración del servicio");
		}
		if (strEmailFrom == null)
		{
			pLog.info("No se ha definido ningún emisor del correo, se coge el definido por configuración: "
					+ pProperties.getProperty("mail.smtp.from"));
		}
		if (alTo == null)
		{
			pLog.info("No se ha definido ningún destinatario principal del correo");
		}
		if (alTo == null && alCC == null && alBCC == null)
		{
			pLog.error("Error en enviar el correo electrénico. No se han definido ningún destinatario para el correo");
			throw new Exception("Error en enviar el correo electrénico. No se han definido ningún destinatario para el correo");
		}
		/* se procede a enviar el correo */
		sendHtmlEmail(pProperties, strEmailFrom, alTo, alCC, alBCC, strSubject, strHtmlMessage, alAttachFile);
	}

	/**
	 * realiza el envio a bajo nivel del coreo electronico
	 * 
	 * @param pConfigProperties
	 *            Configuración del servicio
	 * @param strEmailFrom
	 *            Remitente del correo
	 * @param alTo
	 *            Array de direcciones To
	 * @param alCC
	 *            Array de direcciones en copia
	 * @param alBCC
	 *            Array de direcciones en copia oculta
	 * @param strSubject
	 *            Asunto del correo
	 * @param strHtmlMessage
	 *            Contenido del cuerpo del mensaje
	 * @param alAttachFile
	 *            Array de archivos adjuntos
	 * @throws Exception
	 */
	private static void sendHtmlEmail(Properties pConfigProperties, String strEmailFrom, ArrayList<String> alTo,
			ArrayList<String> alCC, ArrayList<String> alBCC, String strSubject, String strHtmlMessage,
			ArrayList<EmailAttachObject> alAttachFile) throws Exception
	{
		Session pSession;
		Transport pTransport;
		Message pMessage;
		Multipart pMultipart;
		MimeBodyPart pHtmlBodyPart;
		pLog.trace("Se inicia el proceso de envio de correo a " + alTo);
		pLog.trace("Se cargan lee la configuración de conexión con el el servidor de correo: " + pConfigProperties);
		try
		{
			pSession = Session.getDefaultInstance(pConfigProperties);
			pLog.trace("Se obtiene la sesión de correo");
			pTransport = pSession.getTransport();
			pTransport.connect();
			pLog.trace("Se connecta con el servicio de correo");
			/* se crean los objetos contenedores del email */
			pMessage = new MimeMessage(pSession);
			pMultipart = new MimeMultipart();
			pHtmlBodyPart = new MimeBodyPart();
			/* se añade el remitente */
			pMessage.setFrom(new InternetAddress(strEmailFrom));
			pLog.trace("Se añade el remitente:" + strEmailFrom);
			/* se añade el asunto */
			pMessage.setSubject(MimeUtility.encodeText(strSubject, "utf-8", "B"));
			pLog.trace("Se añade el asunto: " + strSubject);
			/* se añade el contenido html */
			pHtmlBodyPart.setContent(strHtmlMessage, "text/html; charset=utf-8");
			pMultipart.addBodyPart(pHtmlBodyPart); // 6
			pLog.trace("Se añade el contenido html: "
					+ (strHtmlMessage.length() > 100 ? strHtmlMessage.substring(0, 100) + "..." : strHtmlMessage));
			/* se cargan los destinatarios */
			if (alTo != null && !alTo.isEmpty())
			{
				InternetAddress[] aEmailsTos = new InternetAddress[alTo.size()];
				for (int i = 0; i < alTo.size(); i++)
				{
					aEmailsTos[i] = new InternetAddress(alTo.get(i));
				}
				pMessage.addRecipients(Message.RecipientType.TO, aEmailsTos);
				pLog.trace("se añaden los destinatarios TO: " + alTo);
			}
			if (alCC != null && !alCC.isEmpty())
			{
				InternetAddress[] aEmailsCCs = new InternetAddress[alCC.size()];
				for (int i = 0; i < alCC.size(); i++)
				{
					aEmailsCCs[i] = new InternetAddress(alCC.get(i));
				}
				pMessage.addRecipients(Message.RecipientType.CC, aEmailsCCs);
				pLog.trace("se añaden los destinatarios CC:" + alCC);
			}
			if (alBCC != null && !alBCC.isEmpty())
			{
				InternetAddress[] aEmailsBCCs = new InternetAddress[alBCC.size()];
				for (int i = 0; i < alBCC.size(); i++)
				{
					aEmailsBCCs[i] = new InternetAddress(alBCC.get(i));
				}
				pMessage.addRecipients(Message.RecipientType.BCC, aEmailsBCCs);
				pLog.trace("se añaden los destinatarios BCC: " + alBCC);
			}
			/* se cargan los adjuntos */
			if (alAttachFile != null && !alAttachFile.isEmpty())
			{
				for (int i = 0; i < alAttachFile.size(); i++)
				{
					DataSource pDataSource;
					DataHandler pDataHandler;
					EmailAttachObject pObject = alAttachFile.get(i);
					MimeBodyPart pAttachMimePart = new MimeBodyPart();
					pDataSource = new ByteArrayDataSource(pObject.getContent(), pObject.getMimeType());
					pDataHandler = new DataHandler(pDataSource);
					pAttachMimePart.setDataHandler(pDataHandler);
					pAttachMimePart.setFileName(pObject.getName());
					pMultipart.addBodyPart(pAttachMimePart);
					pLog.trace("se añade un nuevo fichero adjunto. Nombre: " + pObject.getName() + " - Tamaño: "
							+ pObject.getContent().length);
				}
			}
			pMessage.setContent(pMultipart);
			pTransport.sendMessage(pMessage, pMessage.getAllRecipients());
			pLog.info("Email enviado correctamente");
			pTransport.close();
		}
		catch (Exception ex)
		{
			pLog.error("Error en el proceso de envio de correo. ERROR: " + ex.toString());
			throw ex;
		}
	}

	/**
	 * Carga un objeto propiedades a partir de un fichero de configuración de la propia apliacaciín
	 * 
	 * @param strFileName
	 *            Nombre del fichero
	 * @return Objeto con las propiedades
	 * @throws IOException
	 */
	public static Properties loadProperties(String strFileName) throws IOException
	{
		Properties pProperties = new Properties();
		pProperties.load(Email.class.getResourceAsStream("/" + strFileName));
		return pProperties;
	}
}
