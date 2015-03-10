package fr.travauxetservices.views;

import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.component.AdForm;
import fr.travauxetservices.component.UserForm;
import fr.travauxetservices.component.WrapperLayout;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.*;
import fr.travauxetservices.tools.I18N;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public final class PostView extends Panel implements View {
    private AdForm formAd;
    private UserForm formUser;

    public PostView() {
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
        root.addComponent(buildButton());
        root.addComponent(buildUserForm());
        root.addComponent(buildButton());
        setContent(root);
        Responsive.makeResponsive(root);
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        Label label = new Label(I18N.getString("menu.post"));
        label.setSizeUndefined();
        label.addStyleName(ValoTheme.LABEL_H1);
        label.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(label);

        return header;
    }

    private Component buildAdForm() {
        HorizontalLayout root = new HorizontalLayout();
        root.setWidth(100, Unit.PERCENTAGE);
        root.setMargin(true);
        root.setSpacing(true);

        final Ad newAd = new Ad();
        final BeanItem<Ad> newItem = new BeanItem<Ad>(newAd);
        formAd = new AdForm(newItem, false);
        root.addComponent(formAd);

        return new WrapperLayout("Votre annonce", root);
    }

    private Component buildUserForm() {
        HorizontalLayout root = new HorizontalLayout();
        root.setWidth(100, Unit.PERCENTAGE);
        root.setMargin(true);
        root.setSpacing(true);

        User user = getCurrentUser();

        final BeanItem<User> newItem = new BeanItem<User>(user != null ? user : new User());
        formUser = new UserForm(getCurrentUser(), newItem, user != null, true);
        root.addComponent(formUser);

        return new WrapperLayout("Vos informations", root);
    }

    private void commit() {
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

    private Component buildButton() {
        Button edit = new Button(I18N.getString("button.post"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                commit();
            }
        });
        edit.addStyleName("primary");
        edit.addStyleName("tiny");

        HorizontalLayout footer = new HorizontalLayout();
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        //footer.setMargin(new MarginInfo(false, true, true, true));
        footer.setSpacing(true);
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
