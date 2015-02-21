package fr.travauxetservices.component;

import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

/**
 * Created by Phobos on 13/02/15.
 */
@SuppressWarnings("serial")
public class LabelField extends CustomField<String> {

    final private Label labelField;

    public LabelField(String caption) {
        super();
        labelField = new Label();
        setCaption(caption != null ? caption + ":" : "");
    }

    @Override
    protected Component initContent() {
        return labelField;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public void setPropertyDataSource(Property newDataSource) {
        super.setPropertyDataSource(newDataSource);
        labelField.setValue((String) newDataSource.getValue());
    }

    @Override
    public void setValue(String newValue) throws ReadOnlyException, Converter.ConversionException {
        super.setValue(newValue);
        labelField.setValue(newValue);
    }

    @Override
    public void setImmediate(boolean immediate) {
        super.setImmediate(immediate);
        labelField.setImmediate(immediate);
    }


    public void addStyleName(String style) {
        super.addStyleName(style);
        labelField.addStyleName(style);
    }

    public void setContentMode(ContentMode contentMode) {
        labelField.setContentMode(contentMode);
    }
}