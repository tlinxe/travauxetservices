package fr.travauxetservices.component;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Item;
import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.ui.*;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.tools.I18N;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Phobos on 02/01/15.
 */
public abstract class ExtendedForm extends Form {
    protected boolean readOnly;
    protected WrapperFormLayout form;

    public ExtendedForm(boolean readOnly) {
        this.readOnly = readOnly;
        setBuffered(true);
        setImmediate(true);
        setValidationVisible(false);
        setValidationVisibleOnCommit(false);

        form = new WrapperFormLayout();
        form.setSpacing(true);
        form.setReadOnly(this.readOnly);

        setLayout(form);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    protected abstract List<String> getFields();

    public void setItem(Item item, boolean readOnly) {
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
        }
        form.addComponent(getField(field));
    }

    private Component getField(Field field) {
        return getField(field, false);
    }

    protected Component getField(Field field, boolean forceReadOnly) {
        if (!isReadOnly() && !forceReadOnly) return field;
        LabelField label = new LabelField(field.getCaption());
        label.setIcon(field.getIcon());
        if (field instanceof TextArea) {
            label.setWidth(100, Unit.PERCENTAGE);
        }
        if (field.getValue() != null) label.setValue(field.getValue().toString());
        return label;
    }

    protected Object getEntity(Item item) {
        if (item instanceof BeanItem) {
            return ((BeanItem) item).getBean();
        }
        if (item instanceof EntityItem) {
            return ((EntityItem) item).getEntity();
        }
        return item;
    }

    @Override
    public void commit() throws SourceException, Validator.InvalidValueException {
        try {
            setComponentError(null);
            super.commit();
            fireEvent(new EditorSavedEvent(this, getItemDataSource()));
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
                    throw new InvalidValueException(I18N.getString("message.email.address.already.exists"));
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
