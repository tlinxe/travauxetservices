package fr.travauxetservices.component;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Buffered;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.AbstractErrorMessage;
import com.vaadin.server.ErrorMessage;
import com.vaadin.server.UserError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.model.Ad;
import fr.travauxetservices.model.Remuneration;
import fr.travauxetservices.tools.HtmlEscape;
import fr.travauxetservices.tools.I18N;
import pl.lt.vaadin.ui.FormRowLayout;
import pl.lt.vaadin.ui.RowLayout;
import pl.lt.vaadin.ui.client.rowlayout.RowLayoutState;

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
public class AdForm extends Form {
    private final Item item;
    private boolean readOnly;
    private FormRowLayout form;

    public AdForm(Item item, boolean readOnly) {
        this.item = item;
        this.readOnly = readOnly;

        // FieldFactory for customizing the fields and adding validators
        setFormFieldFactory(new CustomFieldFactory());

        setBuffered(true);
        setImmediate(true);
        setValidationVisible(false);
        setValidationVisibleOnCommit(false);

        form = new FormRowLayout(1, "100px");
        form.setDefaultCaptionWidth("100px");
        form.setDefaultComponentAlignment(Alignment.TOP_LEFT);
        form.setMargin(true);
        form.setSpacing(true);
        form.setReadOnly(this.readOnly);

        setLayout(form);

        setItem(item, this.readOnly);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    private List<String> getFields() {
        List<String> values = new ArrayList<String>();
        if (!readOnly && !(item instanceof EntityItem)) values.add("type");
        if (readOnly) values.add("created");
        values.add("category");
        values.add("division");
        values.add("city");
        values.add("title");
        values.add("description");
        values.add("price");
        values.add("remuneration");
        return values;
    }

    public void setItem(Item item, boolean readOnly) {
        this.readOnly = readOnly;
        form.removeAllComponents();
        setItemDataSource(item, getFields()); // bind to POJO via BeanItem
    }


    @Override
    protected void attachField(Object propertyId, Field field) {
        if (propertyId.equals("title")) {
            if (!isReadOnly()) {
                form.addComponent(getField(field));
            }
        } else if (propertyId.equals("city")) {
            RowLayout rowLayout = form.getRowLayouts().get(form.getComponentCount() - 1);
            rowLayout.addComponent(getField(field), "100px", RowLayoutState.CaptionPos.LEFT);
        } else if (propertyId.equals("remuneration")) {
            if (!isReadOnly()) {
                RowLayout rowLayout = form.getRowLayouts().get(form.getComponentCount() - 1);
                rowLayout.addComponent(getField(field), "100px", RowLayoutState.CaptionPos.LEFT);
            }
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
        Object value = field.getValue();
        LabelField label = new LabelField(field.getCaption());
        label.setIcon(field.getIcon());
        if (field instanceof TextArea) {
            label.setWidth(100, Unit.PERCENTAGE);
            label.setContentMode(ContentMode.HTML);
            if (value != null) label.setValue(HtmlEscape.escapeBr(value.toString()));
        }
        if (field.getValue() != null) label.setValue(field.getValue().toString());
        label.addStyleName(ValoTheme.LABEL_SMALL);
        return label;
    }

    public class TypeComboBox extends ComboBox {
        public TypeComboBox(String caption) {
            setCaption(caption);
            setRequired(true);
            setNullSelectionAllowed(false);
            addItem(Ad.Type.OFFER);
            setItemCaption(Ad.Type.OFFER, "Offre");
            addItem(Ad.Type.REQUEST);
            setItemCaption(Ad.Type.REQUEST, "Demande");
        }
    }

    private class CustomFieldFactory extends DefaultFieldFactory {
        final ComboBox typeField = new TypeComboBox(I18N.getString("ad.type"));
        final ComboBox categoryField = new CategoryComboxBox(I18N.getString("ad.category"));
        final ComboBox divisionField = new DivisionComboxBox(I18N.getString("ad.region"));
        final ComboBox cityField = new CityComboBox(I18N.getString("ad.city"));
        final TextArea descriptionField = new TextArea(I18N.getString("ad.description"));
        final ComboBox remunerationField = new RemunerationComboBox(I18N.getString("ad.remuneration"));

        public CustomFieldFactory() {
            typeField.setNullSelectionAllowed(false);
            typeField.setPageLength(2);

            categoryField.setPageLength(20);

            divisionField.setInputPrompt(I18N.getString("input.division"));
            divisionField.setPageLength(20);

            cityField.setInputPrompt(I18N.getString("input.city"));
            cityField.setPageLength(20);

            descriptionField.setWidth(100, Unit.PERCENTAGE);
            descriptionField.setRows(15);
            descriptionField.addStyleName("notes");

            remunerationField.setInputPrompt(I18N.getString("input.remuneration"));
            remunerationField.setPageLength(20);
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
                field = new TextField(I18N.getString("published")) {
                    public String getValue() {
                        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, getLocale() != null ? getLocale() : Locale.getDefault());
                        return df.format(date);
                    }
                };
            } else if ("category".equals(propertyId)) {
                field = categoryField;
            } else if ("division".equals(propertyId)) {
                field = divisionField;
            } else if ("city".equals(propertyId)) {
                field = cityField;
                Property p = item.getItemProperty("city");
                if (p != null) field.setValue(p.getValue());
            } else if ("title".equals(propertyId)) {
                field.setCaption(I18N.getString("ad.title"));
                field.setWidth(100, Unit.PERCENTAGE);
            } else if ("description".equals(propertyId)) {
                field = descriptionField;
            } else if ("price".equals(propertyId)) {
                if (isReadOnly()) {
                    final double price = (Double) item.getItemProperty("price").getValue();
                    final Remuneration remuneration = (Remuneration) item.getItemProperty("remuneration").getValue();
                    field = new TextField() {
                        public String getValue() {
                            StringBuffer text = new StringBuffer();
                            if (price > 0) {
                                Locale locale = getLocale() != null ? getLocale() : Locale.getDefault();
                                NumberFormat cf = NumberFormat.getCurrencyInstance(locale);
                                cf.setMinimumFractionDigits(0);
                                text.append(cf.format(price));
                            }
                            if (remuneration != null) {
                                text.append((price > 0) ? "/" : "").append(I18N.getString("remuneration." + remuneration.toString() + ".short"));
                            }
                            return text.toString();
                        }
                    };
                }
                field.setCaption(I18N.getString("ad.price"));
            } else if ("remuneration".equals(propertyId)) {
                field = remunerationField;
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

    @Override
    public void commit() throws Buffered.SourceException, Validator.InvalidValueException {
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
                        String text = message.getFormattedHtmlMessage();
                        text = text.replaceAll("<div>", "");
                        text = text.replaceAll("</div>", "");
                        System.out.println("Error: " + text);
                        setComponentError(new UserError(field.getCaption() + ":&#32;" + text, AbstractErrorMessage.ContentMode.HTML, ErrorMessage.ErrorLevel.WARNING));
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
        } catch (final java.lang.NoSuchMethodException e) {
            // This should never happen
            throw new java.lang.RuntimeException("Internal error, editor saved method not found");
        }
    }

    public void removeListener(EditorSavedListener listener) {
        removeListener(EditorSavedEvent.class, listener);
    }

    public static class EditorSavedEvent extends Component.Event {

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
