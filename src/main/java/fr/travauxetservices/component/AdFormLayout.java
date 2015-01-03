package fr.travauxetservices.component;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import fr.travauxetservices.MyVaadinUI;
import fr.travauxetservices.model.Ad;
import fr.travauxetservices.model.Category;
import fr.travauxetservices.model.Division;

import java.util.Collection;

/**
 * Created by Phobos on 02/01/15.
 */
public class AdFormLayout extends FormLayout {
    private final BeanFieldGroup<Ad> fieldGroup;

    @PropertyId("type")
    private ComboBox typeField;

    @PropertyId("category")
    private ComboBox categoryField;

    @PropertyId("division")
    private ComboBox divisionField;

    @PropertyId("title")
    private TextField titleField;

    @PropertyId("description")
    private TextArea descriptionField;

    @PropertyId("price")
    private TextField priceField;

    public AdFormLayout() {
        typeField = new TypeComboBox("Type");
        typeField.setNullSelectionAllowed(false);
        typeField.setRequired(true);
        typeField.setPageLength(2);
        typeField.addStyleName("tiny");
        addComponent(typeField);

        categoryField = new CategoryComboxBox("Category");
        categoryField.setNullSelectionAllowed(false);
        categoryField.setRequired(true);
        categoryField.setPageLength(20);
        categoryField.addStyleName("tiny");
        addComponent(categoryField);

        // When an item is selected from the list...
        categoryField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                // Get the item to edit in the form
                Item item = categoryField.getItem(event.getProperty().getValue());
            }
        });

        divisionField = new DivisionComboxBox("Region");
        divisionField.setNullSelectionAllowed(false);
        divisionField.setRequired(true);
        divisionField.setPageLength(20);
        divisionField.addStyleName("tiny");
        addComponent(divisionField);

        titleField = new TextField("Title");
        titleField.setRequired(true);
        titleField.setWidth(500, Unit.PIXELS);
        titleField.addStyleName("tiny");
        addComponent(titleField);

        descriptionField = new TextArea("Description");
        descriptionField.setRequired(true);
        descriptionField.setWidth(100, Unit.PERCENTAGE);
        descriptionField.setHeight(400, Unit.PIXELS);
        descriptionField.addStyleName("notes");
        descriptionField.addStyleName("tiny");
        addComponent(descriptionField);

        priceField = new TextField("Price");
        priceField.addStyleName("tiny");
        addComponent(priceField);

        fieldGroup = new BeanFieldGroup<Ad>(Ad.class);
        fieldGroup.bindMemberFields(this);
    }

    public BeanFieldGroup<Ad> getFieldGroup() {
        return fieldGroup;
    }

    static class TypeComboBox extends ComboBox {
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
}
