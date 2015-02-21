package fr.travauxetservices.views;

import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.component.RichText;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.Message;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.I18N;
import fr.travauxetservices.tools.IOToolkit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public abstract class TextPageView extends Panel implements View {
    private RichText text;
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

        HorizontalLayout tools = new HorizontalLayout(buildButtons());
        tools.setSpacing(true);
        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    private Button[] buildButtons() {
        List<Button> buttons = new ArrayList<Button>();
        User user = getCurrentUser();
        if (user != null && user.isAdmin()) {
            Button editButton = new Button();
            editButton.setIcon(FontAwesome.EDIT);
            editButton.addStyleName("notifications");
            editButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
            editButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(final Button.ClickEvent event) {
                    editForm(text.isReadOnly());
                }
            });
            buttons.add(editButton);
        }
        return buttons.toArray(new Button[buttons.size()]);
    }

    private Component buildContent() {
        FormLayout form = new FormLayout();
        form.setSpacing(true);
        form.addStyleName("light");
        form.setSizeFull();

        text = new RichText();
        text.setSizeFull();
        form.addComponent(text);

        editForm(false);

        Item item = AppUI.getDataProvider().getMessage("view." + name);
        if (item == null) {
            String basepath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath();
            FileResource resource = new FileResource(new File(basepath + "/WEB-INF/" + name + ".html"));
            item = new BeanItem<Message>(new Message("view." + name, IOToolkit.getResourceAsText(resource)));
        }

        final FieldGroup binder = new FieldGroup(item);
        binder.setBuffered(true);
        binder.bind(text, "text");

        text.addEditorSavedListener(new RichText.EditorSavedListener() {
            @Override
            public void editorSaved(RichText.EditorSavedEvent event) {
                try {
                    binder.commit();
                    Item item = binder.getItemDataSource();
                    if (item instanceof BeanItem) {
                        AppUI.getDataProvider().addMessage((Message) ((BeanItem) item).getBean());
                    }
                    editForm(false);
                } catch (FieldGroup.CommitException ive) {
                    //Ignored
                }
            }
        });
        return form;
    }

    private void editForm(boolean edit) {
        text.setEdit(edit);
    }

    private User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
    }

    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {
        //notificationsButton.updateNotificationsCount(null);
    }
}
