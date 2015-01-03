package fr.travauxetservices.component;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.MyVaadinUI;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;

@SuppressWarnings("serial")
public class ConnectionWindow extends Window {
    public static final String ID = "connectionwindow";

    private ConnectionWindow() {
        addStyleName("profile-window");
        setId(ID);
        Responsive.makeResponsive(this);

        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
        setResizable(false);
        setClosable(false);
        setHeight(250, Unit.PIXELS);
        setWidth(550, Unit.PIXELS);

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(new MarginInfo(true, false, false, false));
        setContent(content);

        Component loginForm = buildLoginForm();
        content.addComponent(loginForm);
        content.setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);

//        Notification notification = new Notification("Welcome to Dashboard Demo");
//        notification.setDescription("<span>This application is not real, it only demonstrates an application built with the <a href=\"https://vaadin.com\">Vaadin framework</a>.</span> <span>No username or password is required, just click the <b>Sign In</b> button to continue.</span>");
//        notification.setHtmlContentAllowed(true);
//        notification.setStyleName("tray dark small closable login-help");
//        notification.setPosition(Position.BOTTOM_CENTER);
//        notification.show(Page.getCurrent());
    }

    private Component buildLoginForm() {
        final VerticalLayout loginPanel = new VerticalLayout();
        loginPanel.setSizeUndefined();
        loginPanel.setSpacing(true);
        Responsive.makeResponsive(loginPanel);
        loginPanel.addStyleName("login-panel");

        Component component = buildButtonFB();
        loginPanel.addComponent(component);
        loginPanel.addComponent(new Label("<hr/>", ContentMode.HTML));
        loginPanel.addComponent(buildFields());
        loginPanel.addComponent(new CheckBox("Remember me", true));
        loginPanel.setComponentAlignment(component, Alignment.MIDDLE_CENTER);
        return loginPanel;
    }

    private Component buildFields() {
        HorizontalLayout fields = new HorizontalLayout();
        fields.setSpacing(true);
        fields.addStyleName("fields");

        final TextField username = new TextField(MyVaadinUI.I18N.getString("user.email"));
        username.setIcon(FontAwesome.USER);
        username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        username.setValue("tlinxe@email.fr");

        final PasswordField password = new PasswordField(MyVaadinUI.I18N.getString("user.password"));
        password.setIcon(FontAwesome.LOCK);
        password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        password.setValue("tlinxe");

        final Button signin = new Button("Se connecter");
        signin.addStyleName(ValoTheme.BUTTON_PRIMARY);
        signin.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        signin.focus();

        fields.addComponents(username, password, signin);
        fields.setComponentAlignment(signin, Alignment.BOTTOM_LEFT);

        signin.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent event) {
                CustomEventBus.post(new CustomEvent.UserLoginRequestedEvent(username.getValue(), password.getValue()));
            }
        });
        return fields;
    }

    private Component buildButtonFB() {
        final Button signin = new Button("Connexion avec Facebook");
        signin.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        signin.setIcon(FontAwesome.FACEBOOK);
        return signin;
    }

    public static void open() {
        CustomEventBus.post(new CustomEvent.CloseOpenWindowsEvent());
        Window w = new ConnectionWindow();
        UI.getCurrent().addWindow(w);
        w.focus();
    }
}
