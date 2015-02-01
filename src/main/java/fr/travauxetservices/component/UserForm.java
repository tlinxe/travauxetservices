package fr.travauxetservices.component;

import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.User;
import fr.travauxetservices.views.PictureField;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phobos on 02/01/15.
 */
public class UserForm extends Form {
    private final User user;
    private final Item item;
    private boolean readOnly;
    private boolean textual;
    private FormLayout form;
    final HorizontalLayout wrapUser = new HorizontalLayout();

    final Button emailButton = new Button("Contacter par email", new Button.ClickListener() {
        @Override
        public void buttonClick(Button.ClickEvent event) {
            Object data = event.getButton().getData();
            event.getButton().setCaption(data != null ? data.toString() : "?");
        }
    });
    final Button phoneButton = new Button("Afficher le téléphone", new Button.ClickListener() {
        @Override
        public void buttonClick(Button.ClickEvent event) {
            Object data = event.getButton().getData();
            event.getButton().setCaption(data != null ? data.toString() : "?");
        }
    });

    public UserForm(User user) {
        this(user, new BeanItem<User>(user), true, false);
    }

    public UserForm(User user, Item item, boolean readOnly, boolean textual) {
        this.user = user;
        this.item = item;
        this.readOnly = readOnly;
        this.textual = textual;

        setBuffered(true);
        setValidationVisible(false);
        //setValidationVisibleOnCommit(false);

        form = new FormLayout();
        //form.setMargin(true);
        form.setSpacing(true);
        form.setReadOnly(this.readOnly);
        wrapUser.addComponent(form);

        setLayout(wrapUser);

        wrapUser.addStyleName("profile-form");
        wrapUser.setMargin(true);
        wrapUser.setSpacing(true);

        phoneButton.setIcon(FontAwesome.PHONE);
        phoneButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        phoneButton.addStyleName(ValoTheme.BUTTON_SMALL);

        emailButton.setIcon(FontAwesome.ENVELOPE);
        emailButton.addStyleName(ValoTheme.BUTTON_DANGER);
        emailButton.addStyleName(ValoTheme.BUTTON_SMALL);

        // FieldFactory for customizing the fields and adding validators
        setFormFieldFactory(new CustomFieldFactory());
        setItem(item, this.readOnly);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    private List<String> getFields() {
        List<String> values = new ArrayList<String>();
        values.add("picture");
        if (!isReadOnly() && (user != null && user.isAdmin())) values.add("role");
        if (!isReadOnly()) values.add("email");
        values.add("gender");
        values.add("firstName");
        values.add("lastName");
        if (!isReadOnly()) values.add("professional");
        if (!isReadOnly()) values.add("password");
        if (isReadOnly()) values.add("email");
        values.add("phone");
        return values;
    }

    public void setItem(Item item, boolean readOnly) {
        this.readOnly = readOnly;
        form.removeAllComponents();
        wrapUser.removeAllComponents();
        wrapUser.addComponent(form);
        setItemDataSource(item, getFields()); // bind to POJO via BeanItem
    }


    @Override
    protected void attachField(Object propertyId, Field field) {
        Object value = field.getValue();
        if (propertyId.equals("picture")) {
            if (textual && value == null) {
                return;
            }
            field.setReadOnly(textual || isReadOnly());
            wrapUser.addComponent(field, 0);
            wrapUser.setComponentAlignment(field, Alignment.TOP_CENTER);
        } else if (isReadOnly()) {
            if (value == null) return;
            if (propertyId.equals("gender")) return;
            if (propertyId.equals("gender")) return;
            if (propertyId.equals("firstName")) return;
            if (!textual) {
                if (propertyId.equals("email")) {
                    emailButton.setData(field.getValue());
                    form.addComponent(emailButton);
                    return;
                } else if (propertyId.equals("phone")) {
                    phoneButton.setData(field.getValue());
                    form.addComponent(phoneButton);
                    return;
                }
            }
            form.addComponent(getField(field));
        } else {
            form.addComponent(getField(field));
        }
    }

    private Component getField(Field field) {
        return getField(field, field.getCaption());
    }

    private Component getField(Field field, String caption) {
        field.setCaption(caption);
        if (!isReadOnly()) return field;
        if (!textual && field instanceof Button) return field;
        Object value = field.getValue();
        Label label = new Label(value != null ? value.toString() : null);
        label.setIcon(field.getIcon());
        if (field instanceof TextArea) {
            label.setWidth(100, Unit.PERCENTAGE);
        }
        if (caption != null) label.setCaption(caption + " :");
        label.addStyleName(ValoTheme.LABEL_SMALL);
        return label;
    }

    private class CustomFieldFactory extends DefaultFieldFactory {
        final PictureField pictureField = new PictureField(null);
        final TextField emailField = new TextField(AppUI.I18N.getString("user.email"));
        final GenderComboBox genderField = new GenderComboBox(AppUI.I18N.getString("user.gender"));
        final TextField firstNameField = new TextField(AppUI.I18N.getString("user.firstName"));
        final TextField lastNameField = new TextField(AppUI.I18N.getString("user.lastName"));
        final RoleComboBox roleField = new RoleComboBox(AppUI.I18N.getString("user.role"));
        final PasswordField passwordField = new PasswordField(AppUI.I18N.getString("user.password"));
        final TextField phoneField = new TextField(AppUI.I18N.getString("user.phone"));
        final CheckBox professionalField = new CheckBox(AppUI.I18N.getString("user.professional"));

        public CustomFieldFactory() {
            emailField.addValidator(new EmailValidator(AppUI.I18N.getString("validator.email")));
            lastNameField.setDescription(AppUI.I18N.getString("user.lastName.description"));

            phoneField.setIcon(FontAwesome.PHONE);
            emailField.setIcon(FontAwesome.ENVELOPE);
            passwordField.setIcon(FontAwesome.LOCK);
        }

        @Override
        public Field createField(Item item, Object propertyId, Component uiContext) {
            // Use the super class to create a suitable field base on the
            // property type.
            Field field = super.createField(item, propertyId, uiContext);
            if ("picture".equals(propertyId)) {
                final Object professional = item.getItemProperty("professional").getValue();
                if (professional instanceof Boolean) {
                    pictureField.setCaption((Boolean)professional ? AppUI.I18N.getString("user.professional") : AppUI.I18N.getString("user.individual"));
                }
                field = pictureField;
            } else if ("email".equals(propertyId)) {
                field = emailField;
            } else if ("gender".equals(propertyId)) {
                field = genderField;
            } else if ("firstName".equals(propertyId)) {
                field = firstNameField;
            } else if ("lastName".equals(propertyId)) {
                if (isReadOnly()) {
                    final Object gender = item.getItemProperty("gender").getValue();
                    final Object firstName = item.getItemProperty("firstName").getValue();
                    final Object lastName = item.getItemProperty("lastName").getValue();
                    field = new TextField(lastNameField.getCaption()) {
                        public String getValue() {
                            StringBuffer text = new StringBuffer();
                            if (gender != null) {
                                text.append(AppUI.I18N.getString("gender." + gender.toString()));
                            }
                            if (firstName != null) {
                                text.append(" " + firstName.toString());
                            }
                            if (lastName != null) {
                                text.append(" " + lastName.toString());
                            }
                            return text.toString();
                        }
                    };
                } else field = lastNameField;
            } else if ("professional".equals(propertyId)) {
                field = professionalField;
            } else if ("role".equals(propertyId)) {
                field = roleField;
            } else if ("password".equals(propertyId)) {
                field = passwordField;
            } else if ("phone".equals(propertyId)) {
                field = phoneField;
            }
            if (!isReadOnly()) field.addStyleName("tiny");
            field.addValidator(new BeanValidator(User.class, propertyId.toString()));
            return field;
        }
    }

    @Override
    public void commit() throws SourceException, Validator.InvalidValueException {
        super.commit();
        fireEvent(new EditorSavedEvent(this, item));
    }

    public void addListener(EditorSavedListener listener) {
        try {
            Method method = EditorSavedListener.class.getDeclaredMethod("editorSaved", new Class[]{EditorSavedEvent.class});
            addListener(EditorSavedEvent.class, listener, method);
        } catch (final NoSuchMethodException e) {
            // This should never happen
            throw new RuntimeException("Internal error, editor saved method not found");
        }
    }

    public void removeListener(EditorSavedListener listener) {
        removeListener(EditorSavedEvent.class, listener);
    }

    public static class EditorSavedEvent extends Event {

        private Item savedItem;

        public EditorSavedEvent(Component source, Item savedItem) {
            super(source);
            this.savedItem = savedItem;
        }

        public Item getSavedItem() {
            return savedItem;
        }
    }

    public interface EditorSavedListener extends Serializable {
        public void editorSaved(EditorSavedEvent event);
    }
}
