package fr.travauxetservices.views;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.fieldfactory.FieldFactory;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.MyVaadinUI;
import fr.travauxetservices.component.*;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.Ad;
import fr.travauxetservices.model.Category;
import fr.travauxetservices.model.Division;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public abstract class AdView extends Panel implements View {
    private String titleLabel;
    private CssLayout dashboardPanels;

    private Table table;

    public AdView(String titleLabel) {
        this.titleLabel = titleLabel;
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        CustomEventBus.register(this);

        // All the open sub-windows should be closed whenever the root layout
        // gets clicked.
//        root.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
//            @Override
//            public void layoutClick(final LayoutEvents.LayoutClickEvent event) {
//                CustomEventBus.post(new CustomEvent.CloseOpenWindowsEvent());
//            }
//        });
    }

    private Layout buildDetailLayout(EntityItem item) {
        VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.addStyleName("dashboard-view");
        root.addComponent(buildHeader());

//        AdFormLayout form = new AdFormLayout();
//        form.getFieldGroup().setReadOnly(true);
//        form.getFieldGroup().setItemDataSource((Ad)item.getEntity());
        Form form = new AdFormFactory(item);
        form.setReadOnly(true);
        root.addComponent(form);
        root.setExpandRatio(form, 1);

        return root;
    }

    private Layout buildListLayout() {
        VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.addStyleName("dashboard-view");
        root.addComponent(buildHeader());

        Component component = buildSearchLayout();
        root.addComponent(component);

        Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1);

        Responsive.makeResponsive(root);

        return root;
    }

    public abstract JPAContainer getContainer();


    public abstract EntityItem getItem(String parameters);


    private Component buildSearchLayout() {
        GridLayout layout = new GridLayout(4, 2);
        //HorizontalLayout layout = new HorizontalLayout();
        layout.addStyleName("dark");
        //layout.setMargin(true);
        layout.setSpacing(true);

        TextField keynword = new TextField();
        keynword.addStyleName(ValoTheme.TEXTFIELD_TINY);
        keynword.setWidth("250px");
        keynword.setInputPrompt(MyVaadinUI.I18N.getString("input.keyword.optional"));
        layout.addComponent(keynword, 0, 0);

        HierarchicalComboBox category = new CategoryComboxBox(null);
        category.setInputPrompt(MyVaadinUI.I18N.getString("input.categories"));
        category.setPageLength(20);
        category.setScrollToSelectedItem(true);
        category.addStyleName(ValoTheme.COMBOBOX_TINY);
                category.setImmediate(true);
        layout.addComponent(category, 1, 0);

        ComboBox division = new DivisionComboxBox(null);
        division.setInputPrompt(MyVaadinUI.I18N.getString("input.regions"));
        division.setPageLength(20);
        division.setScrollToSelectedItem(true);
        division.addStyleName(ValoTheme.COMBOBOX_TINY);
        division.setImmediate(true);
        layout.addComponent(division, 2, 0);

        final ComboBox city = new CityComboBox(null);
        city.setInputPrompt(MyVaadinUI.I18N.getString("input.city"));
        //city.setPageLength(0);
        city.addStyleName(ValoTheme.TEXTFIELD_TINY);
        layout.addComponent(city, 2, 1);

        Button search = new Button(MyVaadinUI.I18N.getString("button.search"), FontAwesome.SEARCH);
        search.addStyleName(ValoTheme.BUTTON_TINY);
        search.addStyleName(ValoTheme.BUTTON_DANGER);
        layout.addComponent(search, 3, 0);

        return layout;
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        Label label = new Label(titleLabel);
        label.setSizeUndefined();
        label.addStyleName(ValoTheme.LABEL_H1);
        label.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(label);

        return header;
    }

    private Component buildContent() {
        dashboardPanels = new CssLayout();
        dashboardPanels.addStyleName("dashboard-panels");
        Responsive.makeResponsive(dashboardPanels);

        dashboardPanels.addComponent(buildTable());

        return dashboardPanels;
    }

    private Component buildTable() {
        JPAContainer container = getContainer();

        table = new AdTable(60);
        table.setCaption(container.size() + " " + titleLabel);
        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
        table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.addStyleName(ValoTheme.TABLE_SMALL);
        //table.setRowHeaderMode(Table.RowHeaderMode.INDEX);
        //table.setWidth("100%");
        table.setSizeFull();
        table.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
        table.setColumnWidth("user", 65);
        table.setContainerDataSource(container);
        table.setCellStyleGenerator(new Table.CellStyleGenerator() {
            public String getStyle(Table source, Object itemId, Object propertyId) {
                return "view";
            }
        });

        table.setVisibleColumns("user", "title", "rate", "created");
        table.setColumnHeaders("User", "Title", "Rate", "Created");
        table.setColumnExpandRatio("title", 2);

        return createContentWrapper(table);
    }

    public Table getTable() {
        return table;
    }

    private Component createContentWrapper(final Component content) {
        CssLayout card = new CssLayout();
        card.setWidth(800, Unit.PIXELS);
        card.addStyleName(ValoTheme.LAYOUT_CARD);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addStyleName("dashboard-panel-toolbar");
        toolbar.setWidth("100%");

        Label caption = new Label(content.getCaption());
        caption.addStyleName(ValoTheme.LABEL_H4);
        caption.addStyleName(ValoTheme.LABEL_COLORED);
        caption.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        content.setCaption(null);

        MenuBar tools = new MenuBar();
        tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        MenuBar.MenuItem root = tools.addItem("", FontAwesome.SORT, null);
        root.addItem("Configure", new MenuBar.Command() {
            @Override
            public void menuSelected(final MenuBar.MenuItem selectedItem) {
                Notification.show("Not implemented in this demo");
            }
        });
        root.addSeparator();
        root.addItem("Close", new MenuBar.Command() {
            @Override
            public void menuSelected(final MenuBar.MenuItem selectedItem) {
                Notification.show("Not implemented in this demo");
            }
        });

        toolbar.addComponents(caption, tools);
        toolbar.setExpandRatio(caption, 1);
        toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT);

        card.addComponents(toolbar, content);
        return card;
    }


    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {
        String parameters = event.getParameters();
        EntityItem item = null;
        Layout layout;
        if (!parameters.isEmpty()) {
            item = getItem(parameters);
        }

        if (item != null) {
            layout = buildDetailLayout(item);
        } else {
            layout = buildListLayout();
            // Handle selection change.
            getTable().addValueChangeListener(new Property.ValueChangeListener() {
                public void valueChange(Property.ValueChangeEvent e) {
                    UI.getCurrent().getNavigator().navigateTo(event.getViewName() + "/" + getTable().getValue());
                }
            });
        }
        setContent(layout);
    }
}
