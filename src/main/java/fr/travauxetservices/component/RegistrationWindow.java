package fr.travauxetservices.component;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.I18N;

@SuppressWarnings("serial")
public class RegistrationWindow extends Window {
    public static final String ID = "resgistrationwindow";

    private UserForm form;

    private RegistrationWindow() {
        setCaption(I18N.getString("window.registration"));
        //addStyleName("profile-window");
        setId(ID);
        Responsive.makeResponsive(this);

        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
        setResizable(false);
        setHeight(500, Unit.PIXELS);
        setWidth(700, Unit.PIXELS);

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(true);
        setContent(content);

        Component form = buildForm();
        content.addComponent(form);
        content.addComponent(buildFooter());
        content.setExpandRatio(form, 1.0f);
    }

    private Component buildForm() {
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setDefaultComponentAlignment(Alignment.TOP_LEFT);
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setSpacing(true);
        Responsive.makeResponsive(layout);

        final BeanItem<User> newItem = new BeanItem<User>(new User());
        form = new UserForm(null, newItem, false, true);
        layout.addComponent(form);

        return layout;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        footer.setWidth(100, Unit.PERCENTAGE);
        footer.setSpacing(true);

        Label footerText = new Label();
        footerText.setSizeUndefined();

        Button ok = new Button(I18N.getString("button.validate"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    form.commit();
                    AppUI.getDataProvider().addUser(((BeanItem<User>) form.getItemDataSource()).getBean());
                    CustomEventBus.post(new CustomEvent.CloseOpenWindowsEvent());
                } catch (Exception e) {
                    //Ignored
                }
            }
        });
        ok.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        ok.addStyleName(ValoTheme.BUTTON_PRIMARY);

        Button cancel = new Button(I18N.getString("button.cancel"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                CustomEventBus.post(new CustomEvent.CloseOpenWindowsEvent());
            }
        });
        footer.addComponents(footerText, ok, cancel);
        footer.setExpandRatio(footerText, 1.0f);
        return footer;
    }


    public static void open() {
        CustomEventBus.post(new CustomEvent.CloseOpenWindowsEvent());
        Window w = new RegistrationWindow();
        UI.getCurrent().addWindow(w);
        w.focus();
    }
}
