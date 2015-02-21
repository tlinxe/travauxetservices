package fr.travauxetservices.component;

import com.vaadin.server.ClassResource;
import com.vaadin.server.Resource;
import com.vaadin.server.StreamResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.HtmlEscape;
import fr.travauxetservices.tools.I18N;
import fr.travauxetservices.tools.IOToolkit;
import org.vaadin.teemu.ratingstars.RatingStars;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Phobos on 03/02/15.
 */
public class RatingTable extends Table {
    public RatingTable() {
        //setImmediate(true);
        setNullSelectionAllowed(false);
        setSortEnabled(false);
        setSelectable(false);
        //addGeneratedColumn("user", new UserColumnGenerator());
        addGeneratedColumn("title", new TitleColumnGenerator());
        addGeneratedColumn("overall", new OverallColumnGenerator());
    }

    public class TitleColumnGenerator implements Table.ColumnGenerator {
        public Component generateCell(Table source, Object itemId, Object columnId) {
            VerticalLayout vLayout = new VerticalLayout();
            vLayout.setDefaultComponentAlignment(Alignment.TOP_LEFT);

            HorizontalLayout hLayout = new HorizontalLayout();
            hLayout.setSpacing(true);
            hLayout.setDefaultComponentAlignment(Alignment.TOP_LEFT);

            Date created = (Date) source.getContainerProperty(itemId, "created").getValue();
            DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, getLocale() != null ? getLocale() : Locale.getDefault());

            Resource resource = new ClassResource("/images/profile-pic-300px.jpg");
            User user = (User) source.getContainerProperty(itemId, "user").getValue();
            if (user.getPicture() != null) {
                resource = new StreamResource(new IOToolkit.ByteArraySource(user.getPicture()), "picture.png");
            }
            Image image = new Image(null, resource);
            image.setWidth(60, Unit.PIXELS);
            image.setHeight(60, Unit.PIXELS);
            hLayout.addComponent(image);
            hLayout.addComponent(new Label("<p>" + I18N.getString("published") + " " + df.format(created) + "<br> " + I18N.getString("published.by") + " " + user.getCommonName() + "</p>", ContentMode.HTML));
            vLayout.addComponent(hLayout);

            String value = (String) source.getContainerProperty(itemId, columnId).getValue();
            String description = (String) source.getContainerProperty(itemId, "description").getValue();
            vLayout.addComponent(new Label("<p>" + (value != null ? "<strong>«" + value + "»</strong><br/>" : "") + HtmlEscape.escapeBr(description) + "</p>", ContentMode.HTML));
            return vLayout;
        }
    }

    public class OverallColumnGenerator implements Table.ColumnGenerator {
        public Component generateCell(Table source, Object itemId, Object columnId) {
            GridLayout layout = new GridLayout(2, 5);
            layout.addStyleName(ValoTheme.FORMLAYOUT_LIGHT);
            layout.setMargin(false);
            layout.setSpacing(false);

            addRatingStars(layout, source, itemId, "reception", 0);
            addRatingStars(layout, source, itemId, "advice", 1);
            addRatingStars(layout, source, itemId, "availability", 2);
            addRatingStars(layout, source, itemId, "quality", 3);
            addRatingStars(layout, source, itemId, "price", 4);
            return layout;
        }
    }

    private void addRatingStars(GridLayout layout, Table source, Object itemId, Object columnId, int index) {
        layout.addComponent(getLabel(columnId), 0, index);
        layout.addComponent(getRatingStar(source, itemId, columnId), 1, index);
    }

    private Component getLabel(Object columnId) {
        Label label = new Label(I18N.getString("rating." + columnId));
        label.addStyleName(ValoTheme.LABEL_SMALL);
        return label;
    }

    private Component getRatingStar(Table source, Object itemId, Object columnId) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.addComponent(getLabel(columnId));

        RatingStars ratingStars = new CustomRatingStars();
        //ratingStars.setCaption(I18N.getString("rating." + columnId));
        //ratingStars.setImmediate(true);
        ratingStars.addStyleName("tiny");
//        ratingStars.setHeight(20, Unit.PIXELS);
        int value = (Integer) source.getContainerProperty(itemId, columnId).getValue();
        ratingStars.setValue((double) value);
        ratingStars.setReadOnly(true);
        layout.addComponent(ratingStars);
        return ratingStars;
    }
}
