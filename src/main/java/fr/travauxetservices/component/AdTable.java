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
import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.Remuneration;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.HtmlEscape;
import fr.travauxetservices.tools.IOToolkit;
import org.apache.commons.lang.StringEscapeUtils;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Phobos on 15/12/14.
 */
public class AdTable extends PagedTable {
    final private boolean simplified;

    public AdTable(int pageLength, boolean simplified) {
        this.simplified = simplified;

        setImmediate(true);
        setNullSelectionAllowed(false);
        setPageLength(pageLength);
        setSortEnabled(false);
        setSelectable(true);
        addGeneratedColumn("division", new DivisionColumnGenerator());
        addGeneratedColumn("remuneration", new RemunerationColumnGenerator(getLocale() != null ? getLocale() : Locale.getDefault()));
        addGeneratedColumn("user", new UserColumnGenerator());
        addGeneratedColumn("title", new TitleColumnGenerator());
    }

    @Override
    protected String formatPropertyValue(final Object rowId, final Object colId, final Property<?> property) {
        String result = super.formatPropertyValue(rowId, colId, property);
        if (colId.equals("created")) {
            if (property != null && property.getValue() != null) {
                Date value = (Date) property.getValue();
                DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale() != null ? getLocale() : Locale.getDefault());
                result = df.format(value);
            } else {
                result = "";
            }
        }
        return result;
    }

    public class UserColumnGenerator implements Table.ColumnGenerator {
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
            image.setWidth(simplified ? 40 : 60, Unit.PIXELS);
            image.setHeight(simplified ? 40 : 60, Unit.PIXELS);
            return image;
        }
    }

    public class RemunerationColumnGenerator implements Table.ColumnGenerator {
        private Locale locale;
        public RemunerationColumnGenerator(Locale locale) {
            this.locale = locale;
        }
        public Component generateCell(Table source, Object itemId, Object columnId) {
            StringBuffer text = new StringBuffer();
            Property value = source.getContainerProperty(itemId, columnId);
            if (value.getValue() != null) {
                text.append("<nobr>");
                if (((Remuneration)value.getValue()).hasPrice()) {
                    Property p = source.getContainerProperty(itemId, "price");
                    if (p != null) {
                        double price = (Double) p.getValue();
                        if (price > 0) {
                            NumberFormat cf = NumberFormat.getCurrencyInstance(locale);
                            cf.setMinimumFractionDigits(0);
                            text.append("<b>").append(cf.format(price)).append("</b>/");
                        }
                    }
                }
                text.append(AppUI.I18N.getString("remuneration." + value.getValue() + ".short"));
                text.append("</nobr>");
            }
            Property division = source.getContainerProperty(itemId, "division");
            if (division.getValue() != null) {
                if (text.length() > 0) text.append("<br/>");
                text.append(division);
            }
            Property city = source.getContainerProperty(itemId, "city");
            if (city.getValue() != null) {
                if (text.length() > 0) text.append("<br/>");
                text.append(city.getValue());
            }
            return new Label(text.toString(), ContentMode.HTML);
        }
    }

    public class TitleColumnGenerator implements Table.ColumnGenerator {
        public Component generateCell(Table source, Object itemId, Object columnId) {
            Property value = source.getContainerProperty(itemId, columnId);
            String description = (String)source.getContainerProperty(itemId, "description").getValue();
            description = description.length() > (simplified ? 100 :255) ? description .substring(0, (simplified ? 100 :255)) + "…" : description;
            return new Label("<b>" + value + "</b><br/>" + HtmlEscape.escapeBr(description), ContentMode.HTML);
        }
    }

    public class DivisionColumnGenerator implements Table.ColumnGenerator {
        public Component generateCell(Table source, Object itemId, Object columnId) {
            StringBuffer text = new StringBuffer();
            Property pUser = source.getContainerProperty(itemId, "user");
            if (pUser.getValue() != null) {
                User user = (User) pUser.getValue();
                if (user != null) {
                    if (user.isProfessional()) {
                        text.append("<b>").append(AppUI.I18N.getString("user.professional")).append("</b>");
                    }
                }
            }
            Property value = source.getContainerProperty(itemId, columnId);
            if (value.getValue() != null) {
                if (text.length() > 0) text.append("<br/>");
                text.append(value);
            }
            Property city = source.getContainerProperty(itemId, "city");
            if (city.getValue() != null) {
                if (text.length() > 0) text.append("<br/>");
                text.append(city.getValue());
            }
            return new Label(text.toString(), ContentMode.HTML);
        }
    }
}
