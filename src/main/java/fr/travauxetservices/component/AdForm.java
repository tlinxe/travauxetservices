package fr.travauxetservices.component;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Buffered;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.Ad;
import fr.travauxetservices.model.Remuneration;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.HtmlEscape;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Phobos on 02/01/15.
 */
public class AdForm extends Form {
    private final User user;
    private final Item item;
    private boolean readOnly;
    private FormLayout form;
    final HorizontalLayout wrapRegion = new HorizontalLayout();
    final HorizontalLayout wrapPrice = new HorizontalLayout();

    public AdForm(User user, Item item, boolean readOnly) {
        this.user = user;
        this.item = item;
        this.readOnly = readOnly;

        setBuffered(true);
        //setValidationVisible(false);
        //setValidationVisibleOnCommit(false);

        form = new FormLayout();
        form.setMargin(true);
        form.setSpacing(true);
        form.setReadOnly(this.readOnly);

        setLayout(form);

        wrapRegion.addStyleName("profile-form");
        wrapRegion.setMargin(false);
        wrapRegion.setSpacing(true);

        wrapPrice.addStyleName("profile-form");
        wrapPrice.setMargin(false);
        wrapPrice.setSpacing(true);

        // FieldFactory for customizing the fields and adding validators
        setFormFieldFactory(new CustomFieldFactory());
        setItem(item, this.readOnly);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    private List<String> getFields() {
        List<String> values = new ArrayList<String>();
        //if (!readOnly && (user != null && user.isAdmin())) values.add("validated");
        if (!readOnly && !(item instanceof EntityItem)) values.add("type");
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
        wrapRegion.removeAllComponents();
        wrapPrice.removeAllComponents();
        setItemDataSource(item, getFields()); // bind to POJO via BeanItem
    }


    @Override
    protected void attachField(Object propertyId, Field field) {
        if (propertyId.equals("title")) {
            if (!isReadOnly()) {
                form.addComponent(getField(field));
            }
        } else if (propertyId.equals("division")) {
            wrapRegion.addComponent(getField(field, null));
        } else if (propertyId.equals("city")) {
            wrapRegion.addComponent(getField(field, null));
            wrapRegion.setCaption(AppUI.I18N.getString("ad.location"));
            wrapRegion.setIcon(FontAwesome.LOCATION_ARROW);
            form.addComponent(wrapRegion);
        } else if (!isReadOnly() && propertyId.equals("price")) {
            wrapPrice.addComponent(getField(field, null));
        } else if (propertyId.equals("remuneration")) {
            if (!isReadOnly()) {
                wrapPrice.addComponent(getField(field, null));
                wrapPrice.setCaption(AppUI.I18N.getString("ad.price"));
                form.addComponent(wrapPrice);
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
        Label label = new Label(value != null ? value.toString() : null);
        label.setIcon(field.getIcon());
        if (field instanceof TextArea) {
            label.setWidth(100, Unit.PERCENTAGE);
            label.setContentMode(ContentMode.HTML);
            if (value != null) label.setValue(HtmlEscape.escapeBr(value.toString()));
        }
        if (caption != null) label.setCaption(caption + " :");
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
        final ComboBox typeField = new TypeComboBox(AppUI.I18N.getString("ad.type"));
        final ComboBox categoryField = new CategoryComboxBox(AppUI.I18N.getString("ad.category"));
        final ComboBox divisionField = new DivisionComboxBox(AppUI.I18N.getString("ad.region"));
        final ComboBox cityField = new CityComboBox(AppUI.I18N.getString("ad.city"));
        final TextField titleField = new TextField(AppUI.I18N.getString("ad.title"));
        final TextArea descriptionField = new TextArea(AppUI.I18N.getString("ad.description"));
        final TextField priceField = new TextField(AppUI.I18N.getString("ad.price"));
        final ComboBox remunerationField = new RemunerationComboBox(AppUI.I18N.getString("ad.remuneration"));

        public CustomFieldFactory() {
            typeField.setNullSelectionAllowed(false);
            typeField.setPageLength(2);

            categoryField.setPageLength(20);

            divisionField.setInputPrompt(AppUI.I18N.getString("input.division"));
            divisionField.setPageLength(20);

            cityField.setInputPrompt(AppUI.I18N.getString("input.city"));
            cityField.setPageLength(20);

            titleField.setCaption(AppUI.I18N.getString("ad.title"));
            titleField.setWidth(100, Unit.PERCENTAGE);

            descriptionField.setSizeFull();
            descriptionField.addStyleName("notes");

            remunerationField.setInputPrompt(AppUI.I18N.getString("input.remuneration"));
            remunerationField.setPageLength(20);
        }

        @Override
        public Field createField(Item item, Object propertyId, Component uiContext) {
            // Use the super class to create a suitable field base on the
            // property type.
            Field field = super.createField(item, propertyId, uiContext);
            if ("type".equals(propertyId)) {
                field = typeField;
            } else if ("category".equals(propertyId)) {
                field = categoryField;
            } else if ("division".equals(propertyId)) {
                field = divisionField;
            } else if ("city".equals(propertyId)) {
                field = cityField;
                Property p = item.getItemProperty("city");
                if (p != null) field.setValue(p.getValue());
            } else if ("title".equals(propertyId)) {
                field = titleField;
            } else if ("description".equals(propertyId)) {
                field = descriptionField;
            } else if ("price".equals(propertyId)) {
                if (isReadOnly()) {
                    final double price = (Double) item.getItemProperty("price").getValue();
                    final Remuneration remuneration = (Remuneration) item.getItemProperty("remuneration").getValue();
                    System.out.println("AdForm.createField field: price price: " + price + " remuneration: " + remuneration);
                    field = new TextField(AppUI.I18N.getString("ad.price")) {
                        public String getValue() {
                            StringBuffer text = new StringBuffer();
                            if (price > 0) {
                                Locale locale = getLocale() != null ? getLocale() : Locale.getDefault();
                                NumberFormat cf = NumberFormat.getCurrencyInstance(locale);
                                cf.setMinimumFractionDigits(0);
                                text.append(cf.format(price));
                            }
                            if (remuneration != null) {
                                text.append((price > 0) ? "/" : "").append(AppUI.I18N.getString("remuneration." + remuneration.toString() + ".short"));
                            }
                            return text.toString();
                        }
                    };
                } else field = priceField;
            } else if ("remuneration".equals(propertyId)) {
                field = remunerationField;
            }
            if (!isReadOnly()) field.addStyleName("tiny");
            field.addValidator(new BeanValidator(Ad.class, propertyId.toString()));
            return field;
        }
    }

    @Override
    public void commit() throws Buffered.SourceException, Validator.InvalidValueException {
        super.commit();
        fireEvent(new EditorSavedEvent(this, item));
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
