/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iti.evalue.business;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Aya Mahmoud
 */
public class Mailer {

    public static boolean sendMail(String to, String subject, String body) {
        boolean sent = false;
        String username = "maedzms@gmail.com";
        String password = "p2ssw0rd";
        Properties properties = System.getProperties();
        String host = "smtp.gmail.com";
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.user", username);
        properties.put("mail.smtp.password", password);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", true);
        Session session = Session.getDefaultInstance(properties, null);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setFrom(new InternetAddress(username));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);
            Transport transport = session.getTransport("smtp");
            transport.connect(host, username, password);
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            transport.close();
            sent = true;
        } catch (MessagingException ex) {
            Logger.getLogger(UserBusiness.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sent;
    }
}
