package fr.travauxetservices.component;

import com.vaadin.data.Property;
import com.vaadin.data.util.converter.Converter;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomField;

import java.awt.*;

/**
 * Created by Phobos on 13/02/15.
 */
@SuppressWarnings("serial")
public class CustomCheckBoxField extends CustomField<Boolean> {

    final private CheckBox field;

    public CustomCheckBoxField(String caption) {
        super();
        field = new CheckBox(caption);
        field.addValueChangeListener(new ValueChangeListener() {
            public void valueChange(Property.ValueChangeEvent event) {
                setValue((Boolean)event.getProperty().getValue());
            }
        });
        setCaption("");
    }

    @Override
    protected Component initContent() {
        return field;
    }

    @Override
    public Class<Boolean> getType() {
        return Boolean.class;
    }

    @Override
    public void setPropertyDataSource(Property newDataSource) {
        super.setPropertyDataSource(newDataSource);
        setValue((Boolean) newDataSource.getValue());
    }

    @Override
    public void setValue(Boolean newValue) throws ReadOnlyException, Converter.ConversionException {
        super.setValue(newValue);
        field.setValue(newValue);
    }

    @Override
    public void setImmediate(boolean immediate) {
        super.setImmediate(immediate);
        field.setImmediate(immediate);
    }


    public void addStyleName(String style) {
        super.addStyleName(style);
        field.addStyleName(style);
    }

    public void setCaptionAsHtml(boolean captionAsHtml) {
        field.setCaptionAsHtml(captionAsHtml);
    }
}