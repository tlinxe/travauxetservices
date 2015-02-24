package fr.travauxetservices.services;

import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.Configuration;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

/**
 * Created by Phobos on 25/01/15.
 */
public class Mail {
    private final static String MAILER_VERSION = "Java";

    static public boolean sendMail(String to, String subject, String html, boolean debug) {
        boolean result = false;
        try {
            Configuration configuration = AppUI.getConfiguration();
            Properties props = new Properties();
            props.put("mail.smtp.host", configuration.getMailSmtpHost());
            Session session = Session.getDefaultInstance(props, null);
            session.setDebug(debug);
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(configuration.getMailSmtpAddress()));
            InternetAddress toAddress = new InternetAddress(to);
            msg.addRecipient(Message.RecipientType.TO, toAddress);
            msg.setSubject(subject);
            msg.setHeader("X-Mailer", MAILER_VERSION);

            Multipart mp = new MimeMultipart();
            //BodyPart textPart = new MimeBodyPart();
            //textPart.setText("This is the message body.");
            BodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent(html, "text/html; charset=UTF-8");
            //mp.addBodyPart(textPart);
            mp.addBodyPart(htmlPart);
            msg.setContent(mp);
            Transport.send(msg);
            result = true;
        } catch (MessagingException e) {
            //Notification.show(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        String html = "<html><head><title>Java Mail</title></head><body>Test envoie email.</body></html>";
        Mail.sendMail("tlinxe@numericable.fr", "test", html, true);
    }
}
