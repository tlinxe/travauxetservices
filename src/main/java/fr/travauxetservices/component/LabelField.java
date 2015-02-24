package fr.travauxetservices.component;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;

/**
 * Created by Phobos on 13/02/15.
 */
@SuppressWarnings("serial")
public class LabelField extends CustomField<String> {

    final private Label field;

    public LabelField(String caption) {
        field = new Label();
        setCaption(caption != null ? caption + ":" : "");
    }

    @Override
    protected Component initContent() {
        return field;
    }

    @Override
    public Class<String> getType() {
        return String.class;
    }

    @Override
    public void setPropertyDataSource(Property newDataSource) {
        super.setPropertyDataSource(newDataSource);
        field.setValue((String) newDataSource.getValue());
    }

    @Override
    public void setValue(String newValue) throws ReadOnlyException, Converter.ConversionException {
        super.setValue(newValue);
        field.setValue(newValue);
    }

    @Override
    public void setImmediate(boolean immediate) {
        super.setImmediate(immediate);
        field.setImmediate(immediate);
    }

    @Override
    public void addStyleName(String style) {
        super.addStyleName(style);
        field.addStyleName(style);
    }

    @Override
    public void setWidth(float width, Unit unit) {
        if (field != null) field.setWidth(width, unit);
    }

    public void setContentMode(ContentMode contentMode) {
        if (field != null) field.setContentMode(contentMode);
    }
}