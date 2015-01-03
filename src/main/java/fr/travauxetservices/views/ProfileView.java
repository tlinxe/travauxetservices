package fr.travauxetservices.views;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainerItem;
import com.vaadin.data.Item;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.Position;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.MyVaadinUI;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.Gender;
import fr.travauxetservices.model.Role;
import fr.travauxetservices.model.User;

import java.util.Arrays;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public final class ProfileView extends Panel implements View, FormFieldFactory {
    private final Form form = new Form();

    public ProfileView() {
        final User user = getCurrentUser();
        if (user == null) {
            UI.getCurrent().getNavigator().navigateTo(ViewType.HOME.getViewName());
            return;
        }
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();

        VerticalLayout root = new VerticalLayout();
        root.setMargin(true);
        root.addStyleName("dashboard-view");
        root.addComponent(buildHeader());
        root.addComponent(buildContent());
        setContent(root);
        Responsive.makeResponsive(root);
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        Label titleLabel = new Label(MyVaadinUI.I18N.getString("menu.profile"));
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        return header;
    }

    private Component buildContent() {
        HorizontalLayout root = new HorizontalLayout();
        root.setWidth(100.0f, Unit.PERCENTAGE);
        root.setSpacing(true);
        root.setMargin(true);
        root.addStyleName("profile-form");

        form.setFormFieldFactory(this);
        form.setItemDataSource(getEntityUser(), Arrays.asList("picture", "gender", "firstName", "lastName", "password"));
        form.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
        root.addComponent(form);
        root.setExpandRatio(form, 1);

        Button ok = new Button("Save");
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);
        ok.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                commit();
            }
        });

        form.getFooter().addComponent(ok);
        return root;
    }

    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
        Field field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
        if ("picture".equals(propertyId)) {
            field = new PictureField(field.getCaption());
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

    private void commit() {
        try {
            form.commit();
            VaadinSession.getCurrent().setAttribute(User.class.getName(), ((JPAContainerItem) form.getItemDataSource()).getEntity());
            // Updated user should also be persisted to database. But
            // not in this demo.

            Notification success = new Notification("Profile updated successfully");
            success.setDelayMsec(2000);
            success.setStyleName("bar success small");
            success.setPosition(Position.BOTTOM_CENTER);
            success.show(Page.getCurrent());

            CustomEventBus.post(new CustomEvent.ProfileUpdatedEvent());
        } catch (Exception e) {
            Notification.show("Error while updating profile",
                    Notification.Type.ERROR_MESSAGE);
        }
    }


    private User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
    }

    private EntityItem<User> getEntityUser() {
        return MyVaadinUI.getDataProvider().getUser(getCurrentUser());
    }

    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {
        //notificationsButton.updateNotificationsCount(null);
    }

    static class GenderComboBox extends ComboBox {
        public GenderComboBox(String caption) {
            setCaption(caption);
            addItem(Gender.MR);
            setItemCaption(Gender.MR, "Mr.");
            addItem(Gender.MRS);
            setItemCaption(Gender.MRS, "Mrs.");
            addItem(Gender.MS);
            setItemCaption(Gender.MS, "Ms.");
        }
    }

    static class RoleComboBox extends ComboBox {
        public RoleComboBox(String caption) {
            setCaption(caption);
            addItem(Role.ADMIN);
            setItemCaption(Role.ADMIN, "Admin");
            addItem(Role.CUSTOMER);
            setItemCaption(Role.CUSTOMER, "Customer.");
        }
    }
}
