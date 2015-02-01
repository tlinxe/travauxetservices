package fr.travauxetservices.views;

import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.component.CategoryComboxBox;
import fr.travauxetservices.component.CityComboBox;
import fr.travauxetservices.component.DivisionComboxBox;
import fr.travauxetservices.component.ValidatedComboBox;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.User;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public final class UserView extends Panel implements View {
    public UserView() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        CustomEventBus.register(this);

        setContent(buildListLayout());
    }

    private Layout buildListLayout() {
        VerticalLayout root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.addStyleName("mytheme-view");
        root.addComponent(buildHeader());

        Component component = buildSearchLayout();
        root.addComponent(component);

        Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1);

        Responsive.makeResponsive(root);

        return root;
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        Label label = new Label("Users");
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

        ComboBox validated = new ValidatedComboBox(null);
        validated.setInputPrompt("Validated");
        validated.setPageLength(20);
        validated.setScrollToSelectedItem(true);
        validated.addStyleName(ValoTheme.COMBOBOX_TINY);
        validated.setImmediate(true);
        validated.addValueChangeListener(new Property.ValueChangeListener(){
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                JPAContainer<User> container = AppUI.getDataProvider().getUserContainer();
                Object value = event.getProperty().getValue();
                if (value!= null) {
                    container.addContainerFilter(new Compare.Equal("validated", value));
                    container.applyFilters();
                }
                else container.removeAllContainerFilters();
            }
        });
        validated.setValue(Boolean.FALSE);
        layout.addComponent(validated, 1, 0);

        Button search = new Button(AppUI.I18N.getString("button.search"), FontAwesome.SEARCH);
        search.addStyleName(ValoTheme.BUTTON_TINY);
        search.addStyleName(ValoTheme.BUTTON_DANGER);
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
        JPAContainer<User> container = AppUI.getDataProvider().getUserContainer();

        Table table = new Table() {
            @Override
            protected String formatPropertyValue(final Object rowId, final Object colId, final Property<?> property) {
                String result = super.formatPropertyValue(rowId, colId, property);
                if (colId.equals("created")) {
                    if (property != null && property.getValue() != null) {
                        Date value = (Date) property.getValue();
                        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, getLocale() != null ? getLocale() : Locale.getDefault());
                        result = df.format(value);
                    } else {
                        result = "";
                    }
                }
                return result;
            }
        };
        table.addStyleName(ValoTheme.TABLE_SMALL);
        //table.setRowHeaderMode(Table.RowHeaderMode.INDEX);
        //table.setWidth("100%");
        table.setSizeFull();
        table.setColumnWidth("validated", 65);
        table.setContainerDataSource(container);
        table.setCellStyleGenerator(new Table.CellStyleGenerator() {
            public String getStyle(Table source, Object itemId, Object propertyId) {
                return "view";
            }
        });

        table.setVisibleColumns("created", "email", "validated");
        table.setColumnHeaders("Created", "Email", "Validated");
        table.setColumnExpandRatio("title", 2);

        return table;
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
    }
}
