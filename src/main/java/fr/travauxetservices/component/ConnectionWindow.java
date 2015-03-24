package fr.travauxetservices.component;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.*;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.I18N;

@SuppressWarnings("serial")
public class ConnectionWindow extends Window {
    public static final String ID = "connectionwindow";

    private ConnectionWindow() {
        addStyleName("profile-window");
        setId(ID);
        Responsive.makeResponsive(this);

        setModal(true);
        setCaption(I18N.getString("window.connection"));
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
        setResizable(false);
        setHeight(250, Unit.PIXELS);
        setWidth(600, Unit.PIXELS);

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(new MarginInfo(true, false, false, false));
        setContent(content);

        Component form = buildForm();
        content.addComponent(form);
        content.setComponentAlignment(form, Alignment.TOP_CENTER);
    }

    private Component buildForm() {
        final VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        layout.setSpacing(true);

        Component component = buildButtons();
        layout.addComponent(component);
        layout.addComponent(new Label("<hr/>", ContentMode.HTML));
        layout.addComponent(buildFields());
        layout.setComponentAlignment(component, Alignment.MIDDLE_CENTER);

        return layout;
    }

    private Component buildFields() {
        final Form form = new Form();
        form.setSizeUndefined();
        form.setBuffered(true);

        GridLayout layout = new GridLayout(3, 2);
        layout.setSpacing(true);
        form.setLayout(layout);

        String username = AppUI.getValueCookie(AppUI.USERNAME_COOKIE);
        final TextField usernameField = new TextField(I18N.getString("user.email"));
        usernameField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        usernameField.setRequired(true);
        usernameField.setRequiredError(I18N.getString("validator.required"));
        usernameField.setIcon(FontAwesome.USER);
        usernameField.addValidator(new EmailValidator(I18N.getString("validator.email")));
        usernameField.setValue(username != null ? username : "tlinxe@numericable.fr");
        layout.addComponent(usernameField, 0, 0);

        String password = AppUI.getValueCookie(AppUI.PASSWORD_COOKIE);
        final PasswordField passwordField = new PasswordField(I18N.getString("user.password"));
        passwordField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
        passwordField.setRequired(true);
        passwordField.setRequiredError(I18N.getString("validator.required"));
        passwordField.setIcon(FontAwesome.LOCK);
        passwordField.setValue(password != null ? password : "motdepasse");
        layout.addComponent(passwordField, 1, 0);

        final Button signinField = new Button(I18N.getString("connection.signin"));
        signinField.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        signinField.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        signinField.focus();
        layout.addComponent(signinField, 2, 0);

        final CheckBox rememberField = new CheckBox(I18N.getString("connection.remember"));
        rememberField.addStyleName(ValoTheme.CHECKBOX_SMALL);
        layout.addComponent(rememberField, 0, 1);

        ClickLabel forgotField = new ClickLabel(I18N.getString("forgot.password"));
        forgotField.addStyleName(ValoTheme.LABEL_COLORED);
        forgotField.addStyleName(ValoTheme.LABEL_SMALL);
        forgotField.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                ForgotWindow.open();
            }
        });
        layout.addComponent(forgotField, 1, 1);

        layout.setComponentAlignment(signinField, Alignment.BOTTOM_LEFT);
        signinField.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent event) {
                User user = AppUI.getDataProvider().authenticate(usernameField.getValue(), passwordField.getValue());
                if (user == null) {
                    form.setComponentError(new UserError("Votre identifiant ou mot de passe est incorrect.", AbstractErrorMessage.ContentMode.HTML, ErrorMessage.ErrorLevel.WARNING));
                    return;
                }
                CustomEventBus.post(new CustomEvent.UserLoginRequestedEvent(user, rememberField.getValue()));
            }
        });
        return form;
    }

    private Component buildButtons() {
        final HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);

        final Button facebook = new Button("Connexion Facebook");
        facebook.addStyleName(ValoTheme.BUTTON_SMALL);
        facebook.addStyleName(ValoTheme.BUTTON_PRIMARY);
        facebook.setIcon(FontAwesome.FACEBOOK);
        layout.addComponent(facebook);

        final Button google = new Button("Connexion Google+");
        google.addStyleName(ValoTheme.BUTTON_SMALL);
        google.addStyleName(ValoTheme.BUTTON_DANGER);
        google.setIcon(FontAwesome.GOOGLE_PLUS);
        layout.addComponent(google);
        return layout;
    }

    public static void open() {
        CustomEventBus.post(new CustomEvent.CloseOpenWindowsEvent());
        Window w = new ConnectionWindow();
        UI.getCurrent().addWindow(w);
        w.focus();
    }
}
