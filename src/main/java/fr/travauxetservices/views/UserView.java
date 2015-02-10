package fr.travauxetservices.views;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.component.AdTable;
import fr.travauxetservices.component.ValidatedComboBox;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.I18N;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public final class UserView extends Panel implements View {
    private AdTable table;
    private ComboBox validatedField;
    private JPAContainer container;

    public UserView() {
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
        root.addComponent(buildSearchLayout());
        Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1);
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        Label label = new Label(I18N.getString("menu.users"));
        label.setSizeUndefined();
        label.addStyleName(ValoTheme.LABEL_H1);
        label.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(label);

        return header;
    }

    private Component buildSearchLayout() {
        GridLayout layout = new GridLayout(4, 2);
        //HorizontalLayout layout = new HorizontalLayout();
        layout.addStyleName("dark");

        validatedField = new ValidatedComboBox(null);
        validatedField.setInputPrompt("Validated");
        validatedField.setPageLength(20);
        validatedField.setScrollToSelectedItem(true);
        validatedField.addStyleName(ValoTheme.COMBOBOX_TINY);
        validatedField.setValue(Boolean.FALSE);
        validatedField.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                applyFilters();
            }
        });
        layout.addComponent(validatedField, 1, 0);

        Button search = new Button(I18N.getString("button.search"), FontAwesome.SEARCH);
        search.addStyleName(ValoTheme.BUTTON_TINY);
        search.addStyleName(ValoTheme.BUTTON_DANGER);
        search.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                applyFilters();
            }
        });
        layout.addComponent(search, 3, 0);

        return layout;
    }

    private Component buildContent() {
        CssLayout mythemePanels = new CssLayout();
        mythemePanels.addStyleName("mytheme-panels");
        Responsive.makeResponsive(mythemePanels);

        mythemePanels.addComponent(buildTable());

        return mythemePanels;
    }

    private Component buildTable() {
        table = new AdTable(20);
        table.setEditable(true);
        table.addStyleName(ValoTheme.TABLE_SMALL);
        table.setWidth(100, Unit.PERCENTAGE);
        table.setAlwaysRecalculateColumnWidths(true);

        applyFilters();
        table.setContainerDataSource(getContainer());

        table.addGeneratedColumn("created", new AdTable.ValueColumnGenerator());
        table.addGeneratedColumn("email", new AdTable.ValueColumnGenerator());
        table.addGeneratedColumn("lastName", new AdTable.ValueColumnGenerator());
        table.addGeneratedColumn("validated", new Table.ColumnGenerator() {
            public Object generateCell(Table source, Object itemId, Object columnId) {
                final CheckBox field = new CheckBox();
                final Item item = source.getItem(itemId);
                final Property property = item.getItemProperty(columnId);
                field.setValue((Boolean) property.getValue());
                field.setReadOnly((Boolean) property.getValue());
                field.addValueChangeListener(new Property.ValueChangeListener() {
                    public void valueChange(Property.ValueChangeEvent event) {
                        if ((Boolean) event.getProperty().getValue()) {
                            property.setValue(event.getProperty().getValue());

                        }
                    }
                });
                return field;
            }
        });
        table.setTableFieldFactory(new TableFieldFactory() {
            public Field createField(Container container, Object itemId, Object propertyId, Component uiContext) {
                Field field = DefaultFieldFactory.get().createField(container, itemId, propertyId, uiContext);
                if (!"validated".equals(propertyId)) {
                    field.setReadOnly(true);
                }
                return field;
            }
        });
        table.setCellStyleGenerator(new Table.CellStyleGenerator() {
            public String getStyle(Table source, Object itemId, Object propertyId) {
                return "view";
            }
        });
        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            public void itemClick(ItemClickEvent e) {
                UI.getCurrent().getNavigator().navigateTo(ViewType.PROFILE.getViewName() + "/" + e.getItem().getItemProperty("id"));
            }
        });

        table.setVisibleColumns("created", "email", "lastName", "validated");
        table.setColumnHeaders(I18N.getString("user.created"), I18N.getString("user.email"), I18N.getString("user.lastName"), I18N.getString("user.validated"));
        table.setColumnWidth("validated", 60);
        return table;
    }

    private JPAContainer getContainer() {
        if (container == null) {
            container =  AppUI.getDataProvider().getUserContainer();
        }
        return container;
    }

    private void applyFilters() {
        JPAContainer container = getContainer();
        container.removeAllContainerFilters();
        if (validatedField.getValue() != null) {
            container.addContainerFilter(new Compare.Equal("validated", validatedField.getValue()));
        }
        if (table != null) {
            table.refreshRowCache();
            table.setCurrentPage(1);
        }
    }

    private User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
    }

    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {
        final User user = getCurrentUser();
        if (user == null) {
            UI.getCurrent().getNavigator().navigateTo(ViewType.HOME.getViewName());
        }
        applyFilters();
    }
}
