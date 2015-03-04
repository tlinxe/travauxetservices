package fr.travauxetservices.component;

import com.vaadin.data.Property;
import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.model.Remuneration;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.DateToolkit;
import fr.travauxetservices.tools.HtmlEscape;
import fr.travauxetservices.tools.I18N;
import fr.travauxetservices.tools.IOToolkit;
import org.vaadin.teemu.ratingstars.RatingStars;

import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Phobos on 15/12/14.
 */
public class AdTable extends PagedTable {
    final private boolean simplified;
    final private boolean formatted;

    public AdTable(int pageLength) {
        this(pageLength, false, false);
    }

    public AdTable(int pageLength, boolean simplified) {
        this(pageLength, simplified, true);
    }

    public AdTable(int pageLength, boolean simplified, boolean formatted) {
        this.simplified = simplified;
        this.formatted = formatted;

        setImmediate(true);
        setNullSelectionAllowed(false);
        setPageLength(pageLength);
        setSortEnabled(false);
        setSelectable(true);
        if (formatted) {
            addGeneratedColumn("location", new LocationColumnGenerator());
            addGeneratedColumn("remuneration", new RemunerationColumnGenerator(getLocale() != null ? getLocale() : Locale.getDefault()));
            addGeneratedColumn("title", new TitleColumnGenerator());
            addGeneratedColumn("user", new UserColumnGenerator());
            setColumnAlignment("user", Align.CENTER);
        }
    }

    @Override
    protected String formatPropertyValue(final Object rowId, final Object colId, final Property<?> property) {
        String result = super.formatPropertyValue(rowId, colId, property);
        if (colId.equals("created")) {
            if (property != null && property.getValue() != null) {
                Date date = (Date) property.getValue();
                result = DateToolkit.format(date, getLocale() != null ? getLocale() : Locale.getDefault());
            } else {
                result = "";
            }
        }
        return result;
    }

    public class UserColumnGenerator implements Table.ColumnGenerator {
        public Component generateCell(Table source, Object itemId, Object columnId) {
            VerticalLayout layout = new VerticalLayout();
            layout.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
            layout.setMargin(false);
            layout.setSpacing(false);

            User user = (User) source.getContainerProperty(itemId, "user").getValue();
            if (user.isProfessional()) {
                Label label = new Label(I18N.getString("user.pro"));
                label.setSizeUndefined();
                label.setDescription(I18N.getString("user.professional"));
                label.addStyleName(ValoTheme.LABEL_COLORED);
                label.addStyleName(ValoTheme.LABEL_BOLD);
                layout.addComponent(label);
            }

            Resource resource = new ClassResource("/images/profile-pic-300px.jpg");
            if (user.getPicture() != null) {
                resource = new StreamResource(new IOToolkit.ByteArraySource(user.getPicture()), "picture.png");
            }
            Image image = new Image(null, resource);
            image.addStyleName("border");
            image.setWidth(simplified ? 40 : 60, Unit.PIXELS);
            image.setHeight(simplified ? 40 : 60, Unit.PIXELS);
            if (user.isProfessional()) {
                image.setDescription(I18N.getString("user.professional"));
            }
            layout.addComponent(image);

            double rating = user.getOverallRating();
            if (rating > 0) {
                RatingStars ratingStars = new CustomRatingStars();
                ratingStars.setMaxValue(5);
                ratingStars.setValue(rating);
                ratingStars.setReadOnly(true);
                ratingStars.addStyleName("tiny");
                ratingStars.setImmediate(true);
                layout.addComponent(ratingStars);
            }
            return layout;
        }
    }

    public class RemunerationColumnGenerator implements Table.ColumnGenerator {
        private Locale locale;

        public RemunerationColumnGenerator(Locale locale) {
            this.locale = locale;
        }

        public Component generateCell(Table source, Object itemId, Object columnId) {
            StringBuffer text = new StringBuffer();
            Remuneration value = (Remuneration)source.getContainerProperty(itemId, columnId).getValue();
            if (value != null) {
                text.append("<nobr>");
                if (value.hasPrice()) {
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
                text.append(I18N.getString(value.getShortLabel()));
                text.append("</nobr>");
            }
            Property location = source.getContainerProperty(itemId, "location");
            if (location.getValue() != null) {
                if (text.length() > 0) text.append("<br/>");
                text.append(location);
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
            String description = (String) source.getContainerProperty(itemId, "description").getValue();
            description = description.length() > (simplified ? 100 : 255) ? description.substring(0, (simplified ? 100 : 255)) + "…" : description;
            return new Label("<p><strong>«" + value + "»</strong><br/>" + HtmlEscape.escapeBr(description) + "</p>", ContentMode.HTML);
        }
    }

    public class LocationColumnGenerator implements Table.ColumnGenerator {
        public Component generateCell(Table source, Object itemId, Object columnId) {
            VerticalLayout layout = new VerticalLayout();
            StringBuffer text = new StringBuffer();
            Property value = source.getContainerProperty(itemId, columnId);
            if (value.getValue() != null) {
                if (text.length() > 0) text.append("<br/>");
                text.append("<nobr>").append(value).append("</nobr>");
            }
            Property city = source.getContainerProperty(itemId, "city");
            if (city.getValue() != null) {
                if (text.length() > 0) text.append("<br/>");
                text.append("<nobr>").append(city.getValue()).append("</nobr>");
            }
            layout.addComponent(new Label(text.toString(), ContentMode.HTML));
            return layout;
        }
    }

    static public class ValueColumnGenerator implements Table.ColumnGenerator {
        /**
         * Generates the cell containing the value.
         * The column is irrelevant in this use case.
         */
        public Component generateCell(Table source, Object itemId, Object columnId) {
            // Get the object stored in the cell as a property
            Property prop = source.getItem(itemId).getItemProperty(columnId);
            String value = ((AdTable) source).formatPropertyValue(itemId, columnId, prop);
            Label label = new Label(value);

            // Set styles for the column: one indicating that it's
            // a value and a more specific one with the column
            // name in it. This assumes that the column name
            // is proper for CSS.
            label.addStyleName("column-type-value");
            label.addStyleName("column-" + columnId);
            return label;
        }
    }
}
