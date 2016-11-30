package nbipackage;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;

public class Emailer
{
	String serwerSmtpm;
	String host;
	String from;
	String to[];
	String subject;
	String messageBody;
	String[] attachmentPaths;


	public Emailer(String serwerSmtpm,String host,String from,String[] to,String subject,String messageBody,String[] attachmentPaths)
	{
		this.serwerSmtpm=serwerSmtpm;
		this.host=host;
		this.from=from;
		this.to=to;
		this.subject=subject;
		this.messageBody=messageBody;
		this.attachmentPaths=attachmentPaths;
	}

	public void send() throws Exception
	{
		

		// Get system properties
		Properties props = System.getProperties();
		
		// Setup mail server
		props.put(serwerSmtpm, host);
		
		// Get session
		Session session = Session.getInstance(props, null);
		
		// Define message
		MimeMessage message = new MimeMessage(session);

		message.setFrom(new InternetAddress(from));
		for(int t=0;t<to.length;t++)
			message.addRecipient(Message.RecipientType.TO,new InternetAddress(to[t]));
		message.setSubject(subject);//, "UTF-8");
                
		message.addHeader("X-Priority", "1");
		// create the message part 
		MimeBodyPart messageBodyPart = new MimeBodyPart();
		
		//fill message
		//messageBodyPart.setText(messageBody);
                
		messageBodyPart.setContent(messageBody, "text/html;");
//                messageBodyPart.setHeader("Content-Type","text/html; charset=\"utf-8\"");
  //              messageBodyPart.setHeader("Content-Transfer-Encoding", "quoted-printable");


		Multipart multipart = new MimeMultipart();
                
		multipart.addBodyPart(messageBodyPart);
               
		if(attachmentPaths!=null)
		for(int z=0;z<attachmentPaths.length;z++)
		{
			// Part two is attachment
			messageBodyPart = new MimeBodyPart();
			DataSource source =new FileDataSource(attachmentPaths[z]);
			messageBodyPart.setDataHandler(new DataHandler(source));
			
			String[] ftmp=attachmentPaths[z].split(System.getProperty("file.separator"));
			String nameF=ftmp[ftmp.length-1];
			messageBodyPart.setFileName(nameF);
			multipart.addBodyPart(messageBodyPart);
		}
		// Put parts in message
		message.setContent(multipart);
		//message.setFlag(Flags.Flag., true)
		
		Transport.send( message );

	}
}