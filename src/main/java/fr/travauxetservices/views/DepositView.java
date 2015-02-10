package fr.travauxetservices.views;

import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.component.AdForm;
import fr.travauxetservices.component.UserForm;
import fr.travauxetservices.component.WrapperLayout;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.Ad;
import fr.travauxetservices.model.User;
import fr.travauxetservices.services.Mail;
import fr.travauxetservices.tools.I18N;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public final class DepositView extends Panel implements View {
    private AdForm formAd;
    private UserForm formUser;

    public DepositView() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        CustomEventBus.register(this);

        VerticalLayout root = new VerticalLayout();
        root.addStyleName("mytheme-view");
        root.setMargin(true);
        root.setSpacing(true);
        root.addComponent(buildHeader());
        root.addComponent(buildAdForm());
        root.addComponent(buildUserForm());
        root.addComponent(buildFooter());
        setContent(root);
        Responsive.makeResponsive(root);
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        Label label = new Label(I18N.getString("menu.deposit"));
        label.setSizeUndefined();
        label.addStyleName(ValoTheme.LABEL_H1);
        label.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(label);

        return header;
    }

    private Component buildAdForm() {
        final Ad newAd = new Ad();
        final BeanItem<Ad> newItem = new BeanItem<Ad>(newAd);
        formAd = new AdForm(newItem, false);

        return new WrapperLayout("Votre annonce", formAd);
    }

    private Component buildUserForm() {
        User user = getCurrentUser();

        final BeanItem<User> newItem = new BeanItem<User>(user != null ? user : new User());
        formUser = new UserForm(getCurrentUser(), newItem, user != null, true);

        return new WrapperLayout("Vos informations", formUser);
    }

    private Component buildFooter() {
        Button edit = new Button("Publier cette annonce", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                User user = getCurrentUser();
                try {
                    formAd.commit();
                    if (user == null) {
                        formUser.commit();
                        BeanItem item = (BeanItem) formUser.getItemDataSource();
                        user = (User) item.getBean();
                        Object email = user.getEmail();
                        AppUI.getDataProvider().addUser(user);

                        String url = AppUI.getEncodedUrl() + "/#!" + ViewType.PROFILE.getViewName() + "/" + user.getId();
                        String subject = I18N.getString("message.user.account.subject");
                        String text = I18N.getString("message.user.account.text", new String[]{email.toString(), url});
                        Mail.sendMail("smtp.numericable.fr", "thierry.linxe@numericable.fr", email.toString(), subject, text, false);
                    }

                    formAd.commit();
                    Ad newAd = ((BeanItem<Ad>) formAd.getItemDataSource()).getBean();
                    newAd.setUser(user);
                    newAd.setId(UUID.randomUUID());
                    newAd.setCreated(new Date(System.currentTimeMillis()));
                    newAd.setValidated(false);
                    if (newAd.getType() == Ad.Type.OFFER) {
                        AppUI.getDataProvider().addOffer(newAd);
                    } else {
                        AppUI.getDataProvider().addRequest(newAd);
                    }
                    if (newAd.getType() == Ad.Type.OFFER) {
                        UI.getCurrent().getNavigator().navigateTo(ViewType.OFFER.getViewName() + "/" + newAd.getId().toString());
                    } else {
                        UI.getCurrent().getNavigator().navigateTo(ViewType.REQUEST.getViewName() + "/" + newAd.getId().toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        edit.addStyleName("primary");
        edit.addStyleName("tiny");

        HorizontalLayout footer = new HorizontalLayout();
        footer.setMargin(new MarginInfo(false, true, true, true));
        footer.setSpacing(true);
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        footer.addComponent(edit);
        return footer;
    }

    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {
        //notificationsButton.updateNotificationsCount(null);
    }

    private User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
    }
}
