package fr.travauxetservices.component;

import com.vaadin.data.Item;
import com.vaadin.data.validator.BeanValidator;
import com.vaadin.ui.*;
import fr.travauxetservices.MyVaadinUI;
import fr.travauxetservices.model.Ad;
import fr.travauxetservices.model.Rate;
import fr.travauxetservices.model.User;
import fr.travauxetservices.views.PictureField;

import java.util.Arrays;

/**
 * Created by Phobos on 03/01/15.
 */
public class AdFormFactory extends Form implements FormFieldFactory {
    public AdFormFactory(Item item) {
        setFormFieldFactory(this);
        setBuffered(true);
        setImmediate(true);
        setItemDataSource(item, Arrays.asList("created", "category", "division", "title", "description", "price", "rate"));
    }

    @Override
    public Field createField(Item item, Object propertyId, Component uiContext) {
        Field field = DefaultFieldFactory.get().createField(item, propertyId, uiContext);
        if ("category".equals(propertyId)) {
            field = new CategoryComboxBox(field.getCaption());
        } else if ("division".equals(propertyId)) {
            field = new DivisionComboxBox(field.getCaption());
        } else if ("title".equals(propertyId)) {
            field.setWidth(500, Unit.PIXELS);
        }else if ("description".equals(propertyId)) {
            field = new TextArea(field.getCaption());
            field.setWidth(100, Unit.PERCENTAGE);
            field.setHeight(400, Unit.PIXELS);
            field.addStyleName("notes");
        }else if ("rate".equals(propertyId)) {
            field = new RateComboBox(field.getCaption());
        }
        field.addStyleName("tiny");

        field.addValidator(new BeanValidator(Ad.class, propertyId.toString()));
        return field;
    }

    static class RateComboBox extends ComboBox {
        public RateComboBox(String caption) {
            setCaption(caption);
            addItem(Rate.TIME);
            setItemCaption(Rate.TIME, MyVaadinUI.I18N.getString("rate.time"));
            addItem(Rate.DAY);
            setItemCaption(Rate.DAY, MyVaadinUI.I18N.getString("rate.day"));
            addItem(Rate.TASK);
            setItemCaption(Rate.TASK, MyVaadinUI.I18N.getString("rate.task"));
            addItem(Rate.PART);
            setItemCaption(Rate.PART, MyVaadinUI.I18N.getString("rate.part"));
            addItem(Rate.EXCHANGE);
            setItemCaption(Rate.EXCHANGE, MyVaadinUI.I18N.getString("rate.exchange"));
        }
    }
}
