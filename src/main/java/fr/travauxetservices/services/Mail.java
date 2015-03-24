package fr.travauxetservices.services;

import com.vaadin.server.Page;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.Configuration;
import fr.travauxetservices.tools.I18N;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Phobos on 25/01/15.
 */
public class Mail {
    private final static Logger logger = Logger.getLogger(Mail.class.getName());
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
            logger.log(Level.WARNING, "Error Sending", e);
            Notification notification = new Notification(I18N.getString("error.sending.title"));
            notification.setDescription(I18N.getString("error.sending.content", new String[]{e.getLocalizedMessage()}));
            notification.setHtmlContentAllowed(true);
            notification.setStyleName(ValoTheme.NOTIFICATION_ERROR + " "+ValoTheme.NOTIFICATION_TRAY + " "+ValoTheme.NOTIFICATION_CLOSABLE);
            notification.setPosition(Position.TOP_CENTER);
            notification.setDelayMsec(10000);
            notification.show(Page.getCurrent());
        }
        return result;
    }

    public static void main(String[] args) {
        String html = "<html><head><title>Java Mail</title></head><body>Test envoie email.</body></html>";
        Mail.sendMail("tlinxe@numericable.fr", "test", html, true);
    }
}
