package fr.travauxetservices.component;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Buffered;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.model.Ad;
import fr.travauxetservices.model.Remuneration;
import fr.travauxetservices.tools.HtmlEscape;
import fr.travauxetservices.tools.I18N;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Phobos on 02/01/15.
 */
public class AdForm extends ExtendedForm {
    public AdForm(Item item, boolean readOnly) {
        super(readOnly);

        setFormFieldFactory(new CustomFieldFactory());
        setItem(item, readOnly);
    }

    protected List<String> getFields() {
        List<String> values = new ArrayList<String>();
        if (!readOnly && !(getItemDataSource() instanceof EntityItem)) values.add("type");
        if (readOnly) values.add("created");
        values.add("category");
        values.add("location");
        values.add("city");
        values.add("title");
        values.add("description");
        values.add("remuneration");
        values.add("price");
        return values;
    }

    @Override
    protected void attachField(Object propertyId, Field field) {
        Object value = field.getValue();
        if (isReadOnly()) {
            if (value == null) return;
            if (propertyId.equals("title")) return;
            if (propertyId.equals("price")) return;
        }
        if (propertyId.equals("city") || propertyId.equals("price")) {
            form.addWrapComponent(getField(field));
            return;
        }
        form.addComponent(getField(field));
    }

    private Component getField(Field field) {
        return getField(field, false);
    }

    public class TypeComboBox extends ComboBox {
        public TypeComboBox(String caption) {
            setCaption(caption);
            setPageLength(2);
            //setNullSelectionAllowed(false);
            addItem(Ad.Type.OFFER);
            setItemCaption(Ad.Type.OFFER, "Offre");
            addItem(Ad.Type.REQUEST);
            setItemCaption(Ad.Type.REQUEST, "Demande");
        }
    }

    private class CustomFieldFactory extends DefaultFieldFactory {
        final ComboBox typeField = new TypeComboBox(I18N.getString("ad.type"));
        final ComboBox categoryField = new CategoryComboxBox(I18N.getString("ad.category"));
        final ComboBox locationField = new LocationComboxBox(I18N.getString("ad.location"));
        final ComboBox cityField = new CityComboBox(I18N.getString("ad.city"));
        final TextArea descriptionField = new TextArea(I18N.getString("ad.description"));
        final ComboBox remunerationField = new RemunerationComboBox(I18N.getString("ad.remuneration"));
        final TextField priceField = new TextField(I18N.getString("ad.price"));

        public CustomFieldFactory() {
            typeField.setRequired(true);
            typeField.setRequiredError(I18N.getString("validator.required"));

            categoryField.setPageLength(20);
            categoryField.setRequired(true);
            categoryField.setRequiredError(I18N.getString("validator.required"));

            locationField.setRequired(true);
            locationField.setRequiredError(I18N.getString("validator.required"));
            locationField.setInputPrompt(I18N.getString("input.location"));
            locationField.setPageLength(20);

            cityField.setInputPrompt(I18N.getString("input.city"));
            cityField.setPageLength(20);

            descriptionField.setRequired(true);
            descriptionField.setRequiredError(I18N.getString("validator.required"));
            descriptionField.setWidth(100, Unit.PERCENTAGE);
            descriptionField.setRows(15);
            descriptionField.addStyleName(ValoTheme.TEXTAREA_HUGE);
            descriptionField.addStyleName("notes");

            remunerationField.setRequired(true);
            remunerationField.setRequiredError(I18N.getString("validator.required"));
            remunerationField.setInputPrompt(I18N.getString("input.remuneration"));
            remunerationField.setPageLength(20);

            priceField.setIcon(FontAwesome.EURO);
            priceField.setVisible(false);

            remunerationField.addValueChangeListener(new Property.ValueChangeListener() {
                public void valueChange(Property.ValueChangeEvent event) {
                    Remuneration value = (Remuneration)event.getProperty().getValue();
                    priceField.setVisible(value != null && value.hasPrice());
                }
            });
        }

        @Override
        public Field createField(Item item, Object propertyId, Component uiContext) {
            // Use the super class to create a suitable field base on the
            // property type.
            Field field = super.createField(item, propertyId, uiContext);
            if ("type".equals(propertyId)) {
                field = typeField;
            } else if ("created".equals(propertyId)) {
                final Date date = (Date) item.getItemProperty(propertyId).getValue();
                field = new TextField(I18N.getString("header.published")) {
                    public String getValue() {
                        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, getLocale() != null ? getLocale() : Locale.getDefault());
                        return df.format(date);
                    }
                };
            } else if ("category".equals(propertyId)) {
                field = categoryField;
            } else if ("location".equals(propertyId)) {
                field = locationField;
            } else if ("city".equals(propertyId)) {
                field = cityField;
                Property p = item.getItemProperty("city");
                if (p != null) field.setValue(p.getValue());
            } else if ("title".equals(propertyId)) {
                field.setRequired(true);
                field.setRequiredError(I18N.getString("validator.required"));
                field.setCaption(I18N.getString("ad.title"));
                field.setWidth(100, Unit.PERCENTAGE);
            } else if ("description".equals(propertyId)) {
                field = descriptionField;
            } else if ("remuneration".equals(propertyId)) {
                if (isReadOnly()) {
                    final double price = (Double) item.getItemProperty("price").getValue();
                    final Remuneration remuneration = (Remuneration) item.getItemProperty("remuneration").getValue();
                    field = new TextField(I18N.getString("ad.price")) {
                        public String getValue() {
                            StringBuffer text = new StringBuffer();
                            if (price > 0) {
                                Locale locale = getLocale() != null ? getLocale() : Locale.getDefault();
                                NumberFormat cf = NumberFormat.getCurrencyInstance(locale);
                                cf.setMinimumFractionDigits(0);
                                text.append(cf.format(price));
                            }
                            if (remuneration != null) {
                                text.append((price > 0) ? "/" : "").append(remuneration.getShortLabel());
                            }
                            return text.toString();
                        }
                    };
                } else field = remunerationField;
            } else if ("price".equals(propertyId)) {
                field = priceField;
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
            field.addValidator(new BeanValidator(Ad.class, propertyId.toString()));
            return field;
        }
    }
}
