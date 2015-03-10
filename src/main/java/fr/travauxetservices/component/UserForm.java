package fr.travauxetservices.component;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Item;
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
public class UserForm extends ExtendedForm {
    private final User user;
    private boolean textual;

    public UserForm(User user) {
        this(user, new BeanItem<User>(user), true, false);
    }

    public UserForm(User user, Item item, boolean readOnly, boolean textual) {
        super(readOnly);
        this.user = user;
        this.textual = textual;

        // FieldFactory for customizing the fields and adding validators
        setFormFieldFactory(new CustomFieldFactory());
        setItem(item, readOnly);
    }

    protected List<String> getFields() {
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
                            return user != null ? user.getFullName() : null;
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
}
