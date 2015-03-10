package fr.travauxetservices.data;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.FileResource;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.Position;
import com.vaadin.ui.Notification;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.*;
import fr.travauxetservices.services.Mail;
import fr.travauxetservices.tools.I18N;
import fr.travauxetservices.tools.IOToolkit;
import fr.travauxetservices.views.ViewType;

import java.io.File;
import java.text.MessageFormat;

/**
 * Created by Phobos on 09/03/15.
 */
public class Post {
    static public void addedUser(final User u) {
        Notification notification = new Notification(I18N.getString("user.notice.title"));
        notification.setDescription(I18N.getString("user.notice.content"));
        notification.setHtmlContentAllowed(true);
        notification.setStyleName(ValoTheme.NOTIFICATION_SUCCESS);
        notification.setPosition(Position.BOTTOM_CENTER);
        notification.setDelayMsec(10000);
        notification.show(Page.getCurrent());

        Notice notice = new Notice();
        notice.setUser(u);
        notice.setAction(I18N.getString("user.notice.title"));
        notice.setContent(I18N.getString("user.notice.content"));
        AppUI.getDataProvider().addNotice(notice);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = AppUI.getEncodedUrl() + "/#!" + ViewType.PROFILE.getViewName() + "/" + u.getId();
                String subject = I18N.getString("user.message.subject");
                Item item = AppUI.getDataProvider().getMessage("user.message");
                if (item == null) {
                    String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
                    FileResource resource = new FileResource(new File(basepath + "/WEB-INF/user_message.html"));
                    item = new BeanItem<Message>(new Message("user.message", IOToolkit.getResourceAsText(resource)));
                }
                String html = MessageFormat.format((String) item.getItemProperty("content").getValue(), new String[]{u.toString(), url});
                if (!Mail.sendMail(u.toString(), subject, html, false)) {
                    AppUI.getDataProvider().removeUser(u.getId());
                }
            }
        });
        t.start();
    }

    static public void validatedAd(final Ad ad, final ViewType type) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String title = ad.getTitle();
                User user = ad.getUser();
                String url = AppUI.getEncodedUrl() + "/#!" + type.getViewName() + "/" + ad.getId();
                String subject = I18N.getString("ad.message.subject", new String[]{title});
                Item item = AppUI.getDataProvider().getMessage("ad.message");
                if (item == null) {
                    String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
                    FileResource resource = new FileResource(new File(basepath + "/WEB-INF/ad_message.html"));
                    item = new BeanItem<Message>(new Message("ad.message", IOToolkit.getResourceAsText(resource)));
                }
                String html = MessageFormat.format((String) item.getItemProperty("content").getValue(), new String[]{title, url});
                Mail.sendMail(user.getEmail(), subject, html, false);
            }
        });
        t.start();
    }

    static public void postMessage(final Ad ad, final Contact contact) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                String url = AppUI.getEncodedUrl() + "/#!" + (ad.getType() == Ad.Type.OFFER ? ViewType.OFFER.getViewName() : ViewType.REQUEST.getViewName()) + "/" + ad.getId();
                String subject = I18N.getString("contact.message.subject", new String[]{ad.getTitle()});
                Item item = AppUI.getDataProvider().getMessage("contact.message");
                if (item == null) {
                    String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
                    FileResource resource = new FileResource(new File(basepath + "/WEB-INF/contact_message.html"));
                    item = new BeanItem<Message>(new Message("contact.message", IOToolkit.getResourceAsText(resource)));
                }
                StringBuffer buffer = new StringBuffer();
                buffer.append("<div>").append("Nom   : ").append(contact.getName()).append("</div>");
                buffer.append("<div>").append("Email : ").append(contact.getEmail()).append("</div>");
                if (contact.getPhone() != null) {
                    buffer.append("<div>").append("Tél   : ").append(contact.getPhone()).append("</div>");
                }
                String html = MessageFormat.format((String) item.getItemProperty("content").getValue(), new String[]{contact.getMessage(), buffer.toString(), ad.getTitle(), url});
                Mail.sendMail(ad.getUser().getEmail(), subject, html, false);
            }
        });
        t.start();
    }
}
