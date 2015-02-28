package fr.travauxetservices.component;

import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.tools.I18N;

@SuppressWarnings("serial")
public class ForgotWindow extends Window {
    public static final String ID = "connectionwindow";

    private ForgotWindow() {
        //addStyleName("profile-window");
        setId(ID);
        Responsive.makeResponsive(this);

        setModal(true);
        setCaption(I18N.getString("window.forgot"));
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
        setResizable(false);
        setHeight(220, Unit.PIXELS);
        setWidth(500, Unit.PIXELS);

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
        final FormLayout layout = new FormLayout();
        layout.setMargin(false);
        layout.setSpacing(true);

        Label labelField = new Label(I18N.getString("forgot.label"));
        labelField.addStyleName(ValoTheme.LABEL_SMALL);
        labelField.addStyleName(ValoTheme.LABEL_COLORED);
        layout.addComponent(labelField);

        String username = AppUI.getValueCookie(AppUI.USERNAME_COOKIE);
        final TextField usernameField = new TextField(I18N.getString("user.email"));
        usernameField.setColumns(20);
        usernameField.setIcon(FontAwesome.USER);
        //usernameField.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
        usernameField.setValue(username != null ? username : "tlinxe@email.fr");
        layout.addComponent(usernameField);

        return layout;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        footer.setWidth(100, Unit.PERCENTAGE);
        footer.setSpacing(true);

        Label footerText = new Label();
        footerText.setSizeUndefined();

        Button ok = new Button(I18N.getString("button.send"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
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
        Window w = new ForgotWindow();
        UI.getCurrent().addWindow(w);
        w.focus();
    }
}
