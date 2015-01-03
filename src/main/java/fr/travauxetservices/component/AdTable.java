package fr.travauxetservices.component;

import com.vaadin.data.Property;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import fr.travauxetservices.MyVaadinUI;
import fr.travauxetservices.model.Rate;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.IOToolkit;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;

/**
 * Created by Phobos on 15/12/14.
 */
public class AdTable extends Table {
    private int imageSise;

    public AdTable(int imageSise) {
        this.imageSise = imageSise;
        setSortEnabled(false);
        setSelectable(true);
        setColumnAlignment("rate", Table.Align.RIGHT);
        addGeneratedColumn("user", new UserColumnGenerator(imageSise));
        addGeneratedColumn("title", new TitleColumnGenerator());
    }

    @Override
    protected String formatPropertyValue(final Object rowId, final Object colId, final Property<?> property) {
        String result = super.formatPropertyValue(rowId, colId, property);
        if (colId.equals("rate")) {
            result = "";
            if (property != null && property.getValue() != null) {
                Rate rate = (Rate) property.getValue();
                if (rate != null) {
                    if (rate.hasPrice()) {
                        Property p = this.getContainerProperty(rowId, "price");
                        if (p != null) {
                            double price = (Double) p.getValue();
                            NumberFormat cf = NumberFormat.getCurrencyInstance(getLocale());
                            result = cf.format(price) + "/";
                        }
                    }
                    result = result + MyVaadinUI.I18N.getString("rate." + rate + ".short");
                }
            }
            return result;
        } else if (colId.equals("created")) {
            if (property != null && property.getValue() != null) {
                Date value = (Date) property.getValue();
                DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale());
                result = df.format(value);
            } else {
                result = "";
            }
        }
        return result;
    }

    public static class UserColumnGenerator implements Table.ColumnGenerator {
        private int imageSise;

        public UserColumnGenerator(int imageSise) {
            this.imageSise = imageSise;
        }

        public Component generateCell(Table source, Object itemId, Object columnId) {
            Resource resource = new ClassResource("/images/profile-pic-300px.jpg");
            Property data = source.getContainerProperty(itemId, "user");
            if (data != null) {
                User user = (User) data.getValue();
                if (user != null) {
                    if (user.getPicture() != null) {
                        resource = new StreamResource(new IOToolkit.ByteArraySource(user.getPicture()), "picture.png");
                    }
                }
            }
            Image image = new Image(null, resource);
            image.addStyleName("border");
            image.setWidth(imageSise, Unit.PIXELS);
            image.setHeight(imageSise, Unit.PIXELS);
            return image;
        }
    }

    public static class TitleColumnGenerator implements Table.ColumnGenerator {
        public Component generateCell(Table source, Object itemId, Object columnId) {
            Property value = source.getContainerProperty(itemId, columnId);
            Property data = source.getContainerProperty(itemId, "description");
            return new Label("<b>" + value + "</b><br/>" + data, ContentMode.HTML);
        }
    }
}
