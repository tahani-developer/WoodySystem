package com.falconssoft.woodysystem.email;

import android.net.Uri;
import android.util.Log;

import com.falconssoft.woodysystem.SettingsFile;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GMail {

    private final String emailPort = "587";// gmail's smtp port
    private final String smtpAuth = "true";
    private final String starttls = "true";
    private final String emailHost = SettingsFile.hostName; // related of sender email

    private String fromEmail, fromPassword, emailSubject, emailBody;
    List toEmailList;

    private Properties emailProperties;
    private Session mailSession;
    private MimeMessage emailMessage; //multipurpose internet mail extensions

    public GMail() {

    }

    public GMail(String fromEmail, String fromPassword,
                 List toEmailList, String emailSubject, String emailBody) {//, String emailImage) {
        this.fromEmail = fromEmail;
        this.fromPassword = fromPassword;
        this.toEmailList = toEmailList;
        this.emailSubject = emailSubject;
        this.emailBody = emailBody;
//        this.emailImage = emailImage;

        emailProperties = System.getProperties();
        emailProperties.put("mail.smtp.port", emailPort);
        emailProperties.put("mail.smtp.auth", smtpAuth);
        emailProperties.put("mail.smtp.starttls.enable", starttls);
        Log.i("GMail", "Mail server properties set.");
    }

    public MimeMessage createEmailMessage() throws AddressException,
            MessagingException, UnsupportedEncodingException {

        mailSession = Session.getDefaultInstance(emailProperties, null);
        emailMessage = new MimeMessage(mailSession);

        emailMessage.setFrom(new InternetAddress(fromEmail, fromEmail));
//        for (int i=0;i<toEmailList.size();i++) {
//            String toEmail=  toEmailList.get(0).toString();
//            Log.i("GMail", "toEmail: " + toEmail);
        emailMessage.addRecipient(Message.RecipientType.TO,
                new InternetAddress(SettingsFile.recipientName));
//        }

        emailMessage.setSubject(emailSubject);
        emailMessage.setContent(emailBody, "text/html");// for a html email
//        emailMessage.setContent(emailBody, "image/png");
//        emailMessage.set
        // emailMessage.setText(emailBody);// for a text email
        Log.i("GMail", "Email Message created.");
        return emailMessage;
    }

    public void sendEmail() throws AddressException, MessagingException {

        Transport transport = mailSession.getTransport("smtp");
        transport.connect(emailHost, fromEmail, fromPassword);
        Log.i("GMail", "allrecipients: " + emailMessage.getAllRecipients());
        transport.sendMessage(emailMessage, emailMessage.getAllRecipients());
        transport.close();
        Log.i("GMail", "Email sent successfully.");
    }

}