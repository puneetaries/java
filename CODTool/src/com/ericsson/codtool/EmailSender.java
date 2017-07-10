package com.ericsson.codtool;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Date;
import java.util.Properties;
 
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
/**
 * @author Puneet Raj Srivastava
 * Ericsson India Pvt Ltd 
 * FEB 2015
 * 
 */
public class EmailSender {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(EmailSender.class);
 
    public static void sendEmailWithAttachments(String host, String fromAddress,String toAddress,String ccAddresses,String bccAddresses,String subject, String message, String[] attachFiles)
            throws AddressException, MessagingException {
		if (logger.isDebugEnabled()) {
			logger
				.debug("EmailSender-sendEmailWithAttachments(String, String, String, String, String, String, String, String[]) - start");
		}

        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host",host);
 
        
        Session session = Session.getInstance(properties);
 
        // creates a new e-mail message
        Message msg = new MimeMessage(session);
 
        msg.setFrom(new InternetAddress(fromAddress));
        
        String[] recipientList = toAddress.split(",");
        InternetAddress[] recipientAddress = new InternetAddress[recipientList.length];
        int counter = 0;
        for (String recipient : recipientList) {
            recipientAddress[counter] = new InternetAddress(recipient.trim());
            counter++;
        }
        msg.setRecipients(Message.RecipientType.TO, recipientAddress);
        
        if((ccAddresses!=null && !ccAddresses.isEmpty()))
        {
        String[] recipientListcc = ccAddresses.split(",");
        InternetAddress[] recipientAddresscc = new InternetAddress[recipientListcc.length];
        int countercc = 0;
        for (String recipientcc : recipientListcc) {
            recipientAddresscc[countercc] = new InternetAddress(recipientcc.trim());
            countercc++;
        }
        msg.setRecipients(Message.RecipientType.CC, recipientAddresscc);
        }
        if((bccAddresses!=null && !bccAddresses.isEmpty()))
        {
        	 String[] recipientListbcc = bccAddresses.split(",");
             InternetAddress[] recipientAddressbcc = new InternetAddress[recipientListbcc.length];
             int counterbcc = 0;
             for (String recipientbcc : recipientListbcc) {
                 recipientAddressbcc[counterbcc] = new InternetAddress(recipientbcc.trim());
                 counterbcc++;
             }
             msg.setRecipients(Message.RecipientType.CC, recipientAddressbcc);
        }
        
        
       
        
       
        msg.setSubject(subject);
        msg.setSentDate(new Date());
 
        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html");
 
        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);
 
        // adds attachments
        if (attachFiles != null && attachFiles.length > 0) {
            for (String filePath : attachFiles) {
                MimeBodyPart attachPart = new MimeBodyPart();
 
                try {
                    attachPart.attachFile(filePath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
 
                multipart.addBodyPart(attachPart);
            }
        }
 
        // sets the multi-part as e-mail's content
        msg.setContent(multipart);
 
        // sends the e-mail
        Transport.send(msg);
 
		if (logger.isDebugEnabled()) {
			logger.debug("EmailSender-sendEmailWithAttachments(String, String, String, String, String, String, String, String[]) - end");
		}
    }
 
    /**
     * Test sending e-mail with attachments
     */
    public static void main(String[] args) {
		
        // SMTP info
        String host = "146.11.115.136";
        String mailFrom = "puneet.raj.srivastava@ericsson.com";
 
        // message info
        String mailTo ="puneet.raj.srivastava@ericsson.com";
        String cc="puneet.raj.srivastava@ericsson.com";
        String bcc="puneet.raj.srivastava@ericsson.com";
        String subject = "New email with attachments";
        String message = "I have some attachments for you.";
 
        // attachments
        String[] attachFiles = new String[1];
        attachFiles[0] = "C:\\Users\\epunsri\\workspace\\CODTool\\src\\Test.txt";
        //attachFiles[1] = "e:/Test/Music.mp3";
        //attachFiles[2] = "e:/Test/Video.mp4";
 
        try {
            sendEmailWithAttachments(host, mailFrom, "puneetaries@gmail.com","puneet_20apr@yahoo.com,puneet.rs@hotmail.com","puneet_20apr@yahoo.com,puneet.rs@hotmail.com",subject, message, attachFiles);
			if (logger.isDebugEnabled()) {
				logger.debug("EmailSender-main(String[]) - Email sent .");
			}
        } catch (Exception ex) {
			logger.error("main(String[])", ex);

			if (logger.isDebugEnabled()) {
				logger.debug("EmailSender-main(String[]) - Could not send email.");
			}
			logger.error("main(String[])", ex);
        }
        
        
		if (logger.isDebugEnabled()) {
			logger.debug("EmailSender-main(String[]) - end");
		}
    }
}