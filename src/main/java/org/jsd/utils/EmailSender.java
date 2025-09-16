package org.jsd.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailSender {
    private static final Logger log = LogManager.getLogger(EmailSender.class);
    
    public static void sendTestReport(String htmlContent, int totalTests, int passed, int failed) throws Exception {
        // Get email configuration
        String smtpHost = ConfigReader.get("email.smtp.host");
        String smtpPort = ConfigReader.get("email.smtp.port");
        String fromEmail = ConfigReader.get("email.from");
        String password = System.getenv("EMAIL_PASSWORD");
        String toEmails = ConfigReader.get("email.to");
        String subject = ConfigReader.get("email.subject");
        

        if (password == null) {
            throw new IllegalArgumentException("EMAIL_PASSWORD environment variable not set. Please set Gmail App Password.");
        }
        
        // Validate configuration
        if (smtpHost == null || fromEmail == null || toEmails == null) {
            throw new IllegalArgumentException("Email configuration missing in config.properties");
        }
        
        // Setup mail properties for Gmail
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

        
        // Create session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });
        
        // Create message
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        
        // Add recipients
        String[] recipients = toEmails.split(",");
        InternetAddress[] addresses = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addresses[i] = new InternetAddress(recipients[i].trim());
        }
        message.setRecipients(Message.RecipientType.TO, addresses);
        
        // Set subject with test results
        String emailSubject = String.format("%s - %d Tests (%d Passed, %d Failed)", 
            subject, totalTests, passed, failed);
        message.setSubject(emailSubject);
        
        // Set HTML content
        message.setContent(htmlContent, "text/html; charset=utf-8");
        
        // Send email
        log.info("Attempting to send email...");
        Transport.send(message);
        log.info("Email sent successfully to: {}", toEmails);
    }
}