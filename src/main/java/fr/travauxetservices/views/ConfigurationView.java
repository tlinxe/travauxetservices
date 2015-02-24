package fr.travauxetservices.views;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.Configuration;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.I18N;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public final class ConfigurationView extends Panel implements View {

    public ConfigurationView() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        CustomEventBus.register(this);

        VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        setContent(root);
        Responsive.makeResponsive(root);
        root.addStyleName("mytheme-view");

        root.addComponent(buildHeader());
        Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1.0f);
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        Label label = new Label(I18N.getString("menu.configuration"));
        label.setSizeUndefined();
        label.addStyleName(ValoTheme.LABEL_H1);
        label.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(label);

        return header;
    }


    private Component buildContent() {
        final Configuration configuration = AppUI.getConfiguration();
        FormLayout form = new FormLayout();
        final FieldGroup binder = new FieldGroup(new BeanItem<Configuration>(configuration));

        Label email = new Label("Mail");
        email.addStyleName("h2");
        email.addStyleName("colored");
        form.addComponent(email);
        form.addComponent(binder.buildAndBind("Host", "mailSmtpHost"));
        form.addComponent(binder.buildAndBind("Address", "mailSmtpAddress"));
        form.addComponent(binder.buildAndBind("Port", "mailSmtpPort"));
        ((TextField)binder.getField("mailSmtpHost")).setColumns(30);
        ((TextField)binder.getField("mailSmtpAddress")).setColumns(30);

        Label database = new Label("Database");
        database.addStyleName("h2");
        database.addStyleName("colored");
        form.addComponent(database);
        form.addComponent(binder.buildAndBind("Platform", "jdbcPlatform"));
        form.addComponent(binder.buildAndBind("Driver", "jdbcDriver"));
        form.addComponent(binder.buildAndBind("Url", "jdbcUrl"));
        form.addComponent(binder.buildAndBind("User", "jdbcUser"));
        form.addComponent(binder.buildAndBind("Password", "jdbcPassword"));
        form.addComponent(binder.buildAndBind("Level", "loggingLevel"));
//        form.addComponent(binder.buildAndBind("DDL", "ddlGeneration"));
//        form.addComponent(binder.buildAndBind("Output Mode", "ddlGenerationOutputMode"));

        ((TextField)binder.getField("jdbcPlatform")).setColumns(30);
        ((TextField)binder.getField("jdbcDriver")).setColumns(30);
        ((TextField)binder.getField("jdbcUrl")).setColumns(30);

        for (Field field : binder.getFields()) {
            field.addStyleName("tiny");
        }

        Button edit = new Button("Save", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    binder.commit();
                    configuration.save();
                    AppUI.getDataProvider().make();
                } catch (FieldGroup.CommitException ive) {
                    //Ignored
                }
            }
        });
        edit.addStyleName(ValoTheme.BUTTON_PRIMARY);
        edit.addStyleName(ValoTheme.BUTTON_SMALL);

        Label home = new Label("Directory: "+Configuration.getHomePath());
        form.addComponent(home);

        HorizontalLayout footer = new HorizontalLayout();
        footer.setMargin(new MarginInfo(true, false, true, false));
        footer.setSpacing(true);
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        form.addComponent(footer);
        footer.addComponent(edit);
        return form;
    }


    private User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
    }

    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {
        final User user = getCurrentUser();
        if (user == null || !user.isAdmin()) {
            UI.getCurrent().getNavigator().navigateTo(ViewType.HOME.getViewName());
            return;
        }
    }
}
