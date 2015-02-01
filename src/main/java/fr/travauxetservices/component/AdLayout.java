package fr.travauxetservices.component;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Validator;
import com.vaadin.server.*;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.Ad;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.IOToolkit;

/**
 * Created by Phobos on 31/01/15.
 */
public class AdLayout extends VerticalLayout {
    public AdLayout(EntityItem<Ad> item, String title) {
        setMargin(true);
        setSpacing(true);
        addStyleName("mytheme-view");
        addComponent(buildHeader(title));
        addComponent(buildAdLayout(item));
        addComponent(buildUserLayout(item));
        addComponent(buildReviewsLayout(item));
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

    private Component buildAdLayout(final EntityItem<Ad> item) {
        User currentUser = getCurrentUser();
        User itemUser = (User) item.getItemProperty("user").getValue();

        final AdForm form = new AdForm(currentUser, item, true);
        if (currentUser != null && (currentUser.isAdmin() || currentUser.equals(itemUser))) {
            Button edit = new Button(AppUI.I18N.getString("button.change"), new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    if (form.isReadOnly()) {
                        form.setItem(item, false);
                        event.getButton().setCaption(AppUI.I18N.getString("button.save"));
                        event.getButton().addStyleName("primary");
                    } else {
                        try {
                            form.commit();
                            item.commit();
                            form.setItem(item, true);
                            event.getButton().setCaption(AppUI.I18N.getString("button.change"));
                            event.getButton().removeStyleName("primary");
                        } catch (Validator.InvalidValueException ive) {
                            Notification.show(ive.getMessage());
                            form.setValidationVisible(true);
                        }
                    }
                }
            });
            edit.addStyleName("tiny");

            HorizontalLayout footer = new HorizontalLayout();
            footer.setMargin(new MarginInfo(false, true, true, true));
            footer.setSpacing(true);
            footer.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
            footer.addComponent(edit);
            form.getFooter().addComponent(footer);
        }
        return new WrapperLayout((item.getEntity()).getTitle(), form);
    }

    private Component buildUserLayout(EntityItem<Ad> item) {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeUndefined();
        //horizontalLayout.setMargin(true);
        horizontalLayout.setSpacing(true);
        Resource resource = new ClassResource("/images/profile-pic-300px.jpg");
        User user = item.getEntity().getUser();
        if (user != null) {
            if (user.getPicture() != null) {
                resource = new StreamResource(new IOToolkit.ByteArraySource(user.getPicture()), "picture.png");
            }
        }
        Image image = new Image(null, resource);
        image.setWidth(100.0f, Unit.PIXELS);
        horizontalLayout.addComponent(new UserForm(item.getEntity().getUser()));

        return new WrapperLayout("Contacter l'annonceur", horizontalLayout);
    }

    private Component buildReviewsLayout(EntityItem<Ad> item) {
        VerticalLayout layout = new VerticalLayout();
        layout.setSizeUndefined();
        layout.setMargin(true);
        layout.setSpacing(true);
        return new WrapperLayout("Avis d'utilisateurs", layout);
    }

    private User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
    }
}
