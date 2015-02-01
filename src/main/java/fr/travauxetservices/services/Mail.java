package fr.travauxetservices.services;

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

    static public boolean sendMail(String server, String from, String to, String subject, String html, boolean debug) {
        boolean result = false;
        try {
            Properties props = new Properties();
            props.put("mail.smtp.host", server);
            Session session = Session.getDefaultInstance(props, null);
            session.setDebug(debug);
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
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
        Mail.sendMail("smtp.numericable.fr", "thierry.linxe@numericable.fr", "tlinxe@numericable.fr", "test", html, true);
    }
}
