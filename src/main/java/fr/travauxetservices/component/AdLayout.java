package fr.travauxetservices.component;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.model.Ad;
import fr.travauxetservices.model.Rating;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.I18N;
import fr.travauxetservices.views.PictureField;
import org.vaadin.teemu.ratingstars.RatingStars;

/**
 * Created by Phobos on 31/01/15.
 */
public class AdLayout extends VerticalLayout {
    private EntityItem<Ad> item;
    public AdLayout(EntityItem<Ad> item, String title) {
        this.item = item;
        setWidth(100, Unit.PERCENTAGE);
        setSpacing(true);
        addStyleName("mytheme-view");

        addComponent(buildHeader(title));
        addComponent(buildAdContent());
        addComponent(buildUserContent());
        addComponent(buildReviewsContent());
    }

    private Component buildHeader(String title) {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        Label label = new Label(title);
        label.setSizeUndefined();
        label.addStyleName(ValoTheme.LABEL_H1);
        label.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(label);

        return header;
    }

    private Component buildAdContent() {
        HorizontalLayout root = new HorizontalLayout();
        root.setMargin(true);

        final AdForm form = new AdForm(item, true);
        form.setWidth(100, Unit.PERCENTAGE);
        root.addComponent(form);

        return new WrapperLayout((item.getEntity()).getTitle(), root);
    }

    private Component buildUserContent() {
        HorizontalLayout root = new HorizontalLayout();
        root.setMargin(true);
        root.setSpacing(true);

        final User user = item.getEntity().getUser();
        PictureField pictureField = new PictureField(null);
        pictureField.setValue(user.getPicture());
        pictureField.setReadOnly(true);
        root.addComponent(pictureField);

        VerticalLayout layout = new VerticalLayout();
        layout.setSpacing(true);
        layout.addComponent(new Label(user.getFullName()));
        if (user.getPhone() != null) {
            final Button phoneButton = new Button(I18N.getString("button.phone"), new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    event.getButton().setCaption(user.getPhone());
                }
            });
            phoneButton.setIcon(FontAwesome.PHONE);
            phoneButton.addStyleName(ValoTheme.BUTTON_FRIENDLY);
            phoneButton.addStyleName(ValoTheme.BUTTON_SMALL);
            layout.addComponent(phoneButton);
        }
        final Button emailButton = new Button(I18N.getString("button.contact"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                ContactWindow.open(item);
            }
        });
        emailButton.setIcon(FontAwesome.ENVELOPE);
        emailButton.addStyleName(ValoTheme.BUTTON_DANGER);
        emailButton.addStyleName(ValoTheme.BUTTON_SMALL);
        layout.addComponent(emailButton);
        root.addComponent(layout);

        return new WrapperLayout("Contacter l'annonceur", root);
    }

    private Component buildReviewsContent() {
        User user = item.getEntity().getUser();

        VerticalLayout layout = new VerticalLayout();
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setMargin(true);
        layout.setSpacing(true);

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setMargin(true);
        horizontalLayout.setSpacing(true);

        RatingStars ratingStars = new CustomRatingStars();
        ratingStars.setMaxValue(5);
        ratingStars.setValue(user.getOverallRating());
        ratingStars.setReadOnly(true);
        ratingStars.addStyleName("large");
        ratingStars.setImmediate(true);
        horizontalLayout.addComponent(ratingStars);

        Label ratingLabel = new Label("(" + user.getNumberRating() + " avis)");
        horizontalLayout.addComponent(ratingLabel);
        horizontalLayout.setComponentAlignment(ratingLabel, Alignment.MIDDLE_CENTER);

        Button writeButton = new Button(I18N.getString("button.review"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

            }
        });
        writeButton.addStyleName(ValoTheme.BUTTON_SMALL);
        writeButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        horizontalLayout.addComponent(writeButton);
        horizontalLayout.setExpandRatio(writeButton, 1.0f);
        horizontalLayout.setComponentAlignment(writeButton, Alignment.MIDDLE_RIGHT);
        layout.addComponent(horizontalLayout);

        if (user.getRatings().size() > 0) {
            RatingTable table = new RatingTable();
            table.setWidth(100, Unit.PERCENTAGE);
            table.addStyleName(ValoTheme.TABLE_NO_STRIPES);
            table.addStyleName(ValoTheme.TABLE_COMPACT);
            table.addStyleName(ValoTheme.TABLE_SMALL);
            table.setNullSelectionAllowed(true);
            table.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);

            BeanItemContainer<Rating> container = new BeanItemContainer<Rating>(Rating.class);
            table.setContainerDataSource(container);
            container.addAll(user.getRatings());
            table.setPageLength(user.getRatings().size());

            table.setVisibleColumns("title", "overall");
            table.setColumnHeaders("Title", "Overall");
            //table.setColumnWidth("user", 100);
            table.setColumnExpandRatio("title", 1);
            table.setColumnWidth("overall", 230);
            layout.addComponent(table);
        }

        return new WrapperLayout("Avis d'utilisateurs", layout);
    }

    private User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
    }
}
