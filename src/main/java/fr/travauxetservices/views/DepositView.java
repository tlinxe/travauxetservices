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
import fr.travauxetservices.tools.I18N;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public final class DepositView extends Panel implements View {
    private AdForm formAd;
    private UserForm formUser;

    public DepositView() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        addStyleName("content-view");
        CustomEventBus.register(this);

        VerticalLayout root = new VerticalLayout();
        root.setWidth(100, Unit.PERCENTAGE);
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
                        user = ((BeanItem<User>) formUser.getItemDataSource()).getBean();
                        AppUI.getDataProvider().addUser(user);
                    }

                    Ad newAd = ((BeanItem<Ad>) formAd.getItemDataSource()).getBean();
                    newAd.setUser(user);
                    AppUI.getDataProvider().addAd(newAd);
                    if (newAd.getType() == Ad.Type.OFFER) {
                        UI.getCurrent().getNavigator().navigateTo(ViewType.OFFER.getViewName() + "/" + newAd.getId().toString());
                    } else {
                        UI.getCurrent().getNavigator().navigateTo(ViewType.REQUEST.getViewName() + "/" + newAd.getId().toString());
                    }
                } catch (Exception e) {
                    //Ignored
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
