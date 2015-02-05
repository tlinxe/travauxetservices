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
        setImmediate(true);
        setNullSelectionAllowed(false);
        setSortEnabled(false);
        setSelectable(false);
        //addGeneratedColumn("user", new UserColumnGenerator());
        addGeneratedColumn("title", new TitleColumnGenerator());
        addGeneratedColumn("overall", new OverallColumnGenerator());
    }

//    public class UserColumnGenerator implements Table.ColumnGenerator {
//        public Component generateCell(Table source, Object itemId, Object columnId) {
//            VerticalLayout layout = new VerticalLayout();
//            layout.setDefaultComponentAlignment(Alignment.TOP_CENTER);
//
//            Resource resource = new ClassResource("/images/profile-pic-300px.jpg");
//            User user = (User) source.getContainerProperty(itemId, "user").getValue();
//            if (user.getPicture() != null) {
//                resource = new StreamResource(new IOToolkit.ByteArraySource(user.getPicture()), "picture.png");
//            }
//            Image image = new Image(null, resource);
//            image.setWidth(60, Unit.PIXELS);
//            image.setHeight(60, Unit.PIXELS);
//            layout.addComponent(image);
//
//            Label label = new Label(user.getCommonName(TextBundle));
//            layout.addComponent(label);
//            return layout;
//        }
//    }

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
            FormLayout layout = new FormLayout();
            layout.setMargin(false);
            layout.setSpacing(false);
            layout.setReadOnly(true);
            layout.setSizeUndefined();
            layout.addComponent(getRatingStar(source, itemId, "reception"));
            layout.addComponent(getRatingStar(source, itemId, "advice"));
            layout.addComponent(getRatingStar(source, itemId, "availability"));
            layout.addComponent(getRatingStar(source, itemId, "quality"));
            layout.addComponent(getRatingStar(source, itemId, "price"));
            return layout;
        }
    }

    private Component getRatingStar(Table source, Object itemId, Object columnId) {
        HorizontalLayout layout = new HorizontalLayout();
        Label label = new Label(I18N.getString("rating." + columnId));
        label.addStyleName(ValoTheme.LABEL_TINY);
        layout.addComponent(label);

        RatingStars ratingStars = new RatingStars();
        ratingStars.setCaption(I18N.getString("rating." + columnId));
        ratingStars.setImmediate(true);
        ratingStars.addStyleName("tiny");
        int value = (Integer) source.getContainerProperty(itemId, columnId).getValue();
        ratingStars.setValue((double) value);
        ratingStars.setReadOnly(true);
        layout.addComponent(ratingStars);
        return ratingStars;
    }
}
