package fr.travauxetservices.views;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.I18N;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public abstract class TextPageView extends Panel implements View {
    private String name;

    public TextPageView(String name) {
        this.name = name;
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        CustomEventBus.register(this);

        VerticalLayout root = new VerticalLayout();
        root.setMargin(true);
        root.addStyleName("mytheme-view");
        root.addComponent(buildHeader());
        root.addComponent(buildContent());
        setContent(root);
        Responsive.makeResponsive(root);
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        Label titleLabel = new Label(I18N.getString("menu." + name));
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        return header;
    }

    private Component buildContent() {
        final FormLayout form = new FormLayout();
        form.setSpacing(true);
        form.addStyleName("light");
        form.setSizeFull();

        final RichTextArea information = new RichTextArea();
        information.setSizeFull();
        form.addComponent(information);

        final FieldGroup binder = new FieldGroup(AppUI.getDataProvider().getMessage("view." + name));
        binder.bind(information, "text");

        form.setReadOnly(true);
        information.setReadOnly(true);

        Button edit = new Button(I18N.getString("button.change"), new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                boolean readOnly = form.isReadOnly();
                if (readOnly) {
                    form.setReadOnly(false);
                    information.setReadOnly(false);
                    event.getButton().setCaption(I18N.getString("button.save"));
                    event.getButton().addStyleName("primary");
                } else {
                    try {
                        binder.commit();
                        form.setReadOnly(true);
                        information.setReadOnly(true);
                        event.getButton().setCaption(I18N.getString("button.change"));
                        event.getButton().removeStyleName("primary");
                    } catch (FieldGroup.CommitException ive) {
                    }
                }
            }
        });
        edit.addStyleName("tiny");
        User user = getCurrentUser();
        if (user != null && user.isAdmin()) {
            form.addComponent(edit);
        }
        return form;
    }

    private User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
    }

    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {
        //notificationsButton.updateNotificationsCount(null);
    }
}
