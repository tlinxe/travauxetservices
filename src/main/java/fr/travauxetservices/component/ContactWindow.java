package fr.travauxetservices.component;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.data.Post;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.Ad;
import fr.travauxetservices.model.Contact;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.I18N;

@SuppressWarnings("serial")
public class ContactWindow extends Window {
    public static final String ID = "contactnwindow";

    private EntityItem<Ad> item;
    private Form form;
    private BeanFieldGroup<Contact> fieldGroup;

    private ContactWindow(EntityItem<Ad> item) {
        this.item = item;
        setCaption(I18N.getString("window.contact"));
//        addStyleName("profile-window");
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
        content.setExpandRatio(form, 1.0f);
    }

    private Component buildForm() {
        form = new Form();

        final VerticalLayout layout = new VerticalLayout();
        layout.setDefaultComponentAlignment(Alignment.TOP_LEFT);
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setSpacing(true);

        FormLayout details = new FormLayout();
        details.setSizeFull();

        Contact contact = new Contact();
        User user = getCurrentUser();
        if (user != null) {
            contact.setName(user.getLastName());
            contact.setEmail(user.getEmail());
            contact.setPhone(user.getPhone());
        }

        TextField nameField = new TextField(I18N.getString("contact.name"), contact.getName());
        nameField.setIcon(FontAwesome.USER);
        nameField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        nameField.setNullRepresentation("");
        nameField.setValidationVisible(false);
        nameField.setRequired(true);
        nameField.setRequiredError(I18N.getString("validator.required"));
        details.addComponent(nameField);

        TextField emailField = new TextField(I18N.getString("contact.email"), contact.getEmail());
        emailField.setIcon(FontAwesome.ENVELOPE);
        emailField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        emailField.setNullRepresentation("");
        emailField.setValidationVisible(false);
        emailField.setRequired(true);
        emailField.setRequiredError(I18N.getString("validator.required"));
        emailField.addValidator(new EmailValidator(I18N.getString("validator.email")));
        details.addComponent(emailField);

        TextField phoneField = new TextField(I18N.getString("contact.phone"), contact.getPhone());
        phoneField.setIcon(FontAwesome.PHONE);
        phoneField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        phoneField.setNullRepresentation("");
        phoneField.setValidationVisible(false);
        details.addComponent(phoneField);

        TextArea messageField = new TextArea(I18N.getString("contact.message"));
        messageField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        messageField.addStyleName("notes");
        messageField.setNullRepresentation("");
        messageField.setValidationVisible(false);
        messageField.setRequired(true);
        messageField.setRequiredError(I18N.getString("validator.required"));
        messageField.setSizeFull();
        messageField.setRows(8);

        layout.addComponent(details);
        layout.addComponent(messageField);
        layout.setExpandRatio(messageField, 1.0f);

        fieldGroup = new BeanFieldGroup<Contact>(Contact.class);
        fieldGroup.setItemDataSource(contact);
        fieldGroup.bind(nameField, "name");
        fieldGroup.bind(emailField, "email");
        fieldGroup.bind(phoneField, "phone");
        fieldGroup.bind(messageField, "message");
        fieldGroup.setBuffered(true);

        form.setLayout(layout);
        return form;
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
                    form.setComponentError(null);
                    fieldGroup.commit();
                    Post.postMessage(item.getEntity(), fieldGroup.getItemDataSource().getBean());
                    CustomEventBus.post(new CustomEvent.CloseOpenWindowsEvent());
                } catch (FieldGroup.CommitException e) {
                    for (Field field : fieldGroup.getFields()) {
                        if (field instanceof AbstractField) {
                            ((AbstractField) field).setValidationVisible(true);
                            ErrorMessage message = ((AbstractField<?>) field).getErrorMessage();
                            if (message != null) {
                                String name = !(field instanceof CheckBox) ? field.getCaption() + ":&#32;" : "";
                                String text = message.getFormattedHtmlMessage();
                                text = text.replaceAll("<div>", "");
                                text = text.replaceAll("</div>", "");
                                form.setComponentError(new UserError(name + text, AbstractErrorMessage.ContentMode.HTML, ErrorMessage.ErrorLevel.WARNING));
                                field.focus();
                                break;
                            }
                        }
                    }
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

    private User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
    }


    public static void open(EntityItem<Ad> item) {
        CustomEventBus.post(new CustomEvent.CloseOpenWindowsEvent());
        Window w = new ContactWindow(item);
        UI.getCurrent().addWindow(w);
        w.focus();
    }
}
