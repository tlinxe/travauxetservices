package fr.travauxetservices.component;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.UserError;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.I18N;
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
    private Item item;
    private boolean readOnly;
    private boolean textual;
    private WrapperFormLayout form;

    public UserForm(User user) {
        this(user, new BeanItem<User>(user), true, false);
    }

    public UserForm(User user, Item item, boolean readOnly, boolean textual) {
        this.user = user;
        this.item = item;
        this.readOnly = readOnly;
        this.textual = textual;

        setBuffered(true);
        setImmediate(true);
        setValidationVisible(false);
        setValidationVisibleOnCommit(false);

        form = new WrapperFormLayout();
        form.setSpacing(true);
        form.setReadOnly(this.readOnly);

        setLayout(form);

        // FieldFactory for customizing the fields and adding validators
        setFormFieldFactory(new CustomFieldFactory());
        setItem(item, this.readOnly);
    }

     public boolean isReadOnly() {
        return readOnly;
    }

    private List<String> getFields() {
        List<String> values = new ArrayList<String>();
        if (!isReadOnly() && !textual) values.add("picture");
        if (!isReadOnly() && (user != null && user.isAdmin())) values.add("role");
        if (!isReadOnly()) values.add("email");
        values.add("gender");
        values.add("firstName");
        values.add("lastName");
        if (!isReadOnly()) values.add("professional");
        if (!isReadOnly()) values.add("password");
        if (!isReadOnly()) values.add("confirm");
        if (isReadOnly()) values.add("email");
        values.add("phone");
        if (!isReadOnly()) values.add("newsletter");
        if (!isReadOnly()) values.add("terms");
        return values;
    }

    public void setItem(Item item, boolean readOnly) {
        this.item = item;
        this.readOnly = readOnly;
        if (item != null) {
            form.removeAllComponents();
            setItemDataSource(item, getFields()); // bind to POJO via BeanItem
        }
    }


    @Override
    protected void attachField(Object propertyId, Field field) {
        Object value = field.getValue();
        if (isReadOnly()) {
            if (value == null) return;
            if (propertyId.equals("gender")) return;
            if (propertyId.equals("firstName")) return;
        }

        if (propertyId.equals("professional") || propertyId.equals("confirm")) {
            form.addWrapComponent(getField(field));
            return;
        }
        form.addComponent(getField(field));
    }

    private Component getField(Field field) {
        return getField(field, false);
    }

    private Component getField(Field field, boolean forceReadOnly) {
        if (!isReadOnly() && !forceReadOnly) return field;
        LabelField label = new LabelField(field.getCaption());
        label.setIcon(field.getIcon());
        if (field instanceof TextArea) {
            label.setWidth(100, Unit.PERCENTAGE);
        }
        if (field.getValue() != null) label.setValue(field.getValue().toString());
//        label.addStyleName(ValoTheme.LABEL_SMALL);
        return label;
    }

    private class CustomFieldFactory extends DefaultFieldFactory {
        final PictureField pictureField = new PictureField(I18N.getString("user.picture"));
        final TextField emailField = new TextField(I18N.getString("user.email"));
        final GenderComboBox genderField = new GenderComboBox(I18N.getString("user.gender"));
        final RoleComboBox roleField = new RoleComboBox(I18N.getString("user.role"));
        final PasswordField passwordField = new PasswordField(I18N.getString("user.password"));
        final PasswordField confirmField = new PasswordField(I18N.getString("user.confirmation"));
        final CheckBox professionalField = new CheckBox(I18N.getString("user.professional.question"));
        final CheckBox newsletterField = new CheckBox(I18N.getString("check.newsletter"));
        final CheckBox termsField = new CheckBox(I18N.getString("check.terms"));

        public CustomFieldFactory() {
            emailField.setRequired(true);
            emailField.setRequiredError(I18N.getString("validator.required"));
            emailField.setIcon(FontAwesome.ENVELOPE);
            emailField.addValidator(new EmailValidator(I18N.getString("validator.email")));
            emailField.addValidator(new ExistsEmailValidator(emailField));

            passwordField.setRequired(true);
            passwordField.setRequiredError(I18N.getString("validator.required"));
            passwordField.setIcon(FontAwesome.LOCK);

            confirmField.setIcon(FontAwesome.LOCK);
            confirmField.addValidator(new Validator() {
                @Override
                public void validate(Object value) throws InvalidValueException {
                    if (passwordField.isModified()) {
                        String passwordValue = passwordField.getValue();
                        if (passwordValue != null) {
                            String confirmValue = confirmField.getValue();
                            if (!passwordValue.equals(confirmValue)) {
                                throw new InvalidValueException(I18N.getString("validator.confirm.password"));
                            }
                        }
                    }
                }
            });

            termsField.setRequired(true);
            termsField.setRequiredError(I18N.getString("validator.terms"));
            termsField.addValidator(new Validator() {
                @Override
                public void validate(Object value) throws InvalidValueException {
                    if (!Boolean.TRUE.equals(value)) {
                        throw new InvalidValueException(I18N.getString("validator.terms"));
                    }
                }
            });
            termsField.setCaptionAsHtml(true);
        }

        @Override
        public Field createField(Item item, Object propertyId, Component uiContext) {
            // Use the super class to create a suitable field base on the
            // property type.
            Field field = super.createField(item, propertyId, uiContext);
            if ("picture".equals(propertyId)) {
                if (isReadOnly() || textual) {
                    final Object professional = item.getItemProperty("professional").getValue();
                    if (professional instanceof Boolean) {
                        //pictureField.setCaption((Boolean) professional ? I18N.getString("user.professional") : I18N.getString("user.individual"));
                    }
                }
                field = pictureField;
            } else if ("email".equals(propertyId)) {
                field = emailField;
            } else if ("gender".equals(propertyId)) {
                field = genderField;
            } else if ("firstName".equals(propertyId)) {
                field.setCaption(I18N.getString("user.firstName"));
            } else if ("lastName".equals(propertyId)) {
                if (isReadOnly()) {
                    final User user = (User) getEntity(item);
                    field = new TextField() {
                        public String getValue() {
                            return user != null ? user.getCommonName() : null;
                        }
                    };
                }
                field.setRequired(true);
                field.setRequiredError(I18N.getString("validator.required"));
                field.setCaption(I18N.getString("user.lastname"));
                ((AbstractTextField) field).setDescription(I18N.getString("user.lastname.description"));
            } else if ("professional".equals(propertyId)) {
                field = professionalField;
            } else if ("role".equals(propertyId)) {
                field = roleField;
            } else if ("password".equals(propertyId)) {
                field = passwordField;
            } else if ("confirm".equals(propertyId)) {
                field = confirmField;
            } else if ("phone".equals(propertyId)) {
                field.setCaption(I18N.getString("user.phone"));
                field.setIcon(FontAwesome.PHONE);
            } else if ("newsletter".equals(propertyId)) {
                field = newsletterField;
            } else if ("terms".equals(propertyId)) {
                field = termsField;
            }
            if (field instanceof AbstractTextField) {
                ((AbstractTextField) field).setNullRepresentation("");
            }
            if (field instanceof AbstractField) {
                ((AbstractField) field).setValidationVisible(isValidationVisible());
            }
            if (!isReadOnly()) {
                if (field instanceof CheckBox) {
                    field.addStyleName(ValoTheme.CHECKBOX_SMALL);
                } else {
                    field.addStyleName("tiny");
                }
            }
            field.addValidator(new BeanValidator(User.class, propertyId.toString()));
            return field;
        }
    }

    private Object getEntity(Item item) {
        if (item instanceof BeanItem) {
            return ((BeanItem) item).getBean();
        }
        if (item instanceof EntityItem) {
            return ((EntityItem) item).getEntity();
        }
        return null;
    }

    @Override
    public void commit() throws SourceException, Validator.InvalidValueException {
        try {
            setComponentError(null);
            super.commit();
            fireEvent(new EditorSavedEvent(this, item));
        } catch (Validator.InvalidValueException e) {
            for (Object property : getItemPropertyIds()) {
                Field field = getField(property);
                if (field instanceof AbstractField) {
                    ((AbstractField) field).setValidationVisible(true);
                    ErrorMessage message = ((AbstractField<?>) field).getErrorMessage();
                    if (message != null) {
                        String name = !(field instanceof CheckBox) ? field.getCaption() + ":&#32;" : "";
                        String text = message.getFormattedHtmlMessage();
                        text = text.replaceAll("<div>", "");
                        text = text.replaceAll("</div>", "");
                        setComponentError(new UserError(name + text, AbstractErrorMessage.ContentMode.HTML, ErrorMessage.ErrorLevel.WARNING));
                        field.focus();
                        throw e;
                    }
                }
            }
        }

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

    public static class ExistsEmailValidator implements Validator {
        private Field field;

        public ExistsEmailValidator(Field field) {
            this.field = field;
        }

        @Override
        public void validate(Object value) throws InvalidValueException {
            if (field.isModified() && value != null) {
                if (AppUI.getDataProvider().hasUser(value.toString())) {
                    throw new Validator.InvalidValueException(I18N.getString("message.email.address.already.exists"));
                }
            }
        }
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
