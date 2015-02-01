package fr.travauxetservices.views;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.component.GenderComboBox;
import fr.travauxetservices.component.RoleComboBox;
import fr.travauxetservices.component.UserForm;
import fr.travauxetservices.component.WrapperLayout;
import fr.travauxetservices.model.User;

import java.util.UUID;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public final class ProfileView extends Panel implements View, FormFieldFactory {
    private User user;
    private UserForm form;

    public ProfileView() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();

        VerticalLayout root = new VerticalLayout();
        root.setMargin(true);
        root.addStyleName("mytheme-view");
        root.addComponent(buildHeader());
        root.addComponent(buildContent());
        setContent(root);
        Responsive.makeResponsive(root);
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        Label titleLabel = new Label(AppUI.I18N.getString("menu.profile"));
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        return header;
    }

    private Component buildContent() {
        User currentUser = getCurrentUser();
        final User newUser = new User();
        final BeanItem<User> newItem = new BeanItem<User>(newUser);
        form = new UserForm(currentUser, newItem, false, false);
        Button edit = new Button(AppUI.I18N.getString("button.change"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    form.commit();
                    event.getButton().setCaption(AppUI.I18N.getString("button.change"));
                    event.getButton().removeStyleName("primary");
                } catch (Validator.InvalidValueException ive) {
                    Notification.show(ive.getMessage());
                    form.setValidationVisible(true);
                }
            }
        });
        edit.addStyleName("tiny");
        edit.addStyleName("primary");

        HorizontalLayout footer = new HorizontalLayout();
        footer.setMargin(new MarginInfo(false, true, true, true));
        footer.setSpacing(true);
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        footer.addComponent(edit);
        form.getFooter().addComponent(footer);

        return new WrapperLayout("Votre profile", form);
    }

    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
        Field field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
        if ("picture".equals(propertyId)) {
            field = new PictureField(field.getCaption());
        } else if ("email".equals(propertyId)) {
            field.addValidator(new EmailValidator(AppUI.I18N.getString("validator.email")));
        } else if ("gender".equals(propertyId)) {
            field = new GenderComboBox(field.getCaption());
        } else if ("role".equals(propertyId)) {
            field = new RoleComboBox(field.getCaption());
        } else if ("password".equals(propertyId)) {
            field = new PasswordField(field.getCaption());
        }
        field.addStyleName("tiny");

        field.addValidator(new BeanValidator(User.class, propertyId.toString()));
        return field;
    }

    private void setItemDataSource(User user) {
        if (user != null && !user.isValidated()) user.setValidated(true);
        form.setItem(getEntityUser(user), false);
    }

    private Item getEntityUser(User user) {
        if (user == null) {
            return new BeanItem<User>(new User());
        }
        return AppUI.getDataProvider().getUser(user.getId());
    }

    private User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
    }

    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {
        user = (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
        String parameters = event.getParameters();
        if (!parameters.isEmpty()) {
            try {
                UUID id = UUID.fromString(parameters);
                EntityItem<User> item = AppUI.getDataProvider().getUser(id);
                if (item != null) {
                    user = item.getEntity();
                }
            } catch (IllegalArgumentException e) {
                //Ignored
            }
        }
        if (user == null) {
            UI.getCurrent().getNavigator().navigateTo(ViewType.HOME.getViewName());
        }
        setItemDataSource(user);
    }
}
