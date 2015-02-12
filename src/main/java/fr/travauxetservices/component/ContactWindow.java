package fr.travauxetservices.component;

import com.vaadin.data.util.BeanItem;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.I18N;

@SuppressWarnings("serial")
public class ContactWindow extends Window {
    public static final String ID = "contactnwindow";

    private FormLayout form;

    private ContactWindow() {
        setCaption("Message");
        //addStyleName("profile-window");
        setId(ID);
        Responsive.makeResponsive(this);

        setModal(true);
        setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
        setResizable(false);
        setHeight(500, Unit.PIXELS);
        setWidth(500, Unit.PIXELS);

        VerticalLayout content = new VerticalLayout();
        content.setSizeFull();
        content.setMargin(true);
        setContent(content);

        Component form = buildForm();
        content.addComponent(form);
        content.addComponent(buildFooter());
        content.setExpandRatio(form, 1);
    }

    private Component buildForm() {
        final VerticalLayout layout = new VerticalLayout();
        layout.setWidth("100%");
        layout.setDefaultComponentAlignment(Alignment.TOP_LEFT);
        //layout.setSizeUndefined();
        layout.setSpacing(true);
        Responsive.makeResponsive(layout);

        form = new FormLayout();
        //form.setSizeFull();

        final TextField nameField = new TextField("Votre nom");
        nameField.setIcon(FontAwesome.USER);
        nameField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        form.addComponent(nameField);

        final TextField emailField = new TextField("Votre email");
        emailField.setIcon(FontAwesome.ENVELOPE);
        emailField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        form.addComponent(emailField);

        final TextField phoneField = new TextField("Votre téléphone");
        phoneField.setIcon(FontAwesome.PHONE);
        phoneField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        form.addComponent(phoneField);

        final TextArea textField = new TextArea("Texte");
        textField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        textField.addStyleName("notes");
        textField.setSizeFull();

        layout.addComponent(form);
        layout.addComponent(textField);
        //layout.addComponent(buildFooter());
        layout.setExpandRatio(textField, 1);

        return layout;
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setWidth("100%");
        footer.setSpacing(true);
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_RIGHT);
        //footer.addStyleName("v-window-bottom-toolbar");

        Label footerText = new Label();
        footerText.setSizeUndefined();

        Button ok = new Button("Envoyer", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
//                    form.commit();
//                    AppUI.getDataProvider().addUser(((BeanItem<User>) form.getItemDataSource()).getBean());
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
        footer.setExpandRatio(footerText, 1);
        return footer;
    }


    public static void open() {
        CustomEventBus.post(new CustomEvent.CloseOpenWindowsEvent());
        Window w = new ContactWindow();
        UI.getCurrent().addWindow(w);
        w.focus();
    }
}
