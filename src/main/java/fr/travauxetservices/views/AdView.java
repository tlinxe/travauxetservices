package fr.travauxetservices.views;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Like;
import com.vaadin.data.util.filter.Or;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.component.*;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.Ad;
import fr.travauxetservices.model.Category;
import fr.travauxetservices.model.Location;
import fr.travauxetservices.tools.I18N;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public abstract class AdView extends Panel implements CustomView {
    private String viewName;
    private PagedTable table;

    private TextField keynwordField;
    private ComboBox categoryField;
    private ComboBox locationField;
    private ComboBox cityField;

    public AdView(String viewName) {
        this.viewName = viewName;
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        addStyleName("content-view");
        CustomEventBus.register(this);

        VerticalLayout root = new VerticalLayout();
        root.setWidth(100, Unit.PERCENTAGE);
        Responsive.makeResponsive(root);
        root.addStyleName("mytheme-view");
        setContent(root);

        root.addComponent(buildHeader());
        root.addComponent(buildSearchLayout());
        Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1);
    }

    protected abstract String getTitleLabel();


    public abstract JPAContainer getContainer();


    public abstract EntityItem<Ad> getItem(String parameters);


    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        Label label = new Label(getTitleLabel());
        label.setSizeUndefined();
        label.addStyleName(ValoTheme.LABEL_H1);
        label.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(label);

        return header;
    }

    private Component buildSearchLayout() {
        GridLayout layout = new GridLayout(4, 2);
        layout.addStyleName("dark");
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setSpacing(true);

        keynwordField = new TextField();
        keynwordField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        keynwordField.setWidth("220px");
        keynwordField.setInputPrompt(I18N.getString("input.keyword.optional"));
        layout.addComponent(keynwordField, 0, 0);

        categoryField = new CategoryComboxBox(null);
        categoryField.setInputPrompt(I18N.getString("input.categories"));
        categoryField.setPageLength(20);
        categoryField.setScrollToSelectedItem(true);
        categoryField.addStyleName(ValoTheme.COMBOBOX_TINY);
        categoryField.setImmediate(true);
        layout.addComponent(categoryField, 1, 0);

        locationField = new LocationComboxBox(null);
        locationField.setInputPrompt(I18N.getString("input.location"));
        locationField.setPageLength(20);
        locationField.setScrollToSelectedItem(true);
        locationField.addStyleName(ValoTheme.COMBOBOX_TINY);
        locationField.setImmediate(true);
        layout.addComponent(locationField, 2, 0);

        cityField = new CityComboBox(null);
        cityField.setInputPrompt(I18N.getString("input.city"));
        //cityField.setPageLength(0);
        cityField.addStyleName(ValoTheme.TEXTFIELD_TINY);
        layout.addComponent(cityField, 2, 1);

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
        table = new AdTable(10, false);
        //table.addStyleName(ValoTheme.TABLE_BORDERLESS);
        table.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        table.addStyleName(ValoTheme.TABLE_NO_STRIPES);
        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.addStyleName(ValoTheme.TABLE_SMALL);
        //table.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
        table.setWidth(100, Unit.PERCENTAGE);

        applyFilters();
        table.setContainerDataSource(getContainer());

        table.setVisibleColumns("user", "location", "title", "created");
        table.setColumnHeaders("", I18N.getString("header.location"), I18N.getString("header.title"), I18N.getString("header.published"));
        table.setColumnExpandRatio("title", 1);

        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            public void itemClick(ItemClickEvent e) {
                UI.getCurrent().getNavigator().navigateTo(viewName + "/" + e.getItem());
            }
        });

        final WrapperLayout wrapperLayout = new WrapperLayout(null, table);
        final HorizontalLayout controlLayout = table.createControls();
        wrapperLayout.addComponent(controlLayout);
        MenuBar.MenuItem root = wrapperLayout.addItem("", FontAwesome.SORT, null);
        root.addItem("Tri", new MenuBar.Command() {
            @Override
            public void menuSelected(final MenuBar.MenuItem selectedItem) {
                Notification.show("Not implemented in this demo");
            }
        });

        table.addListener(new PagedTable.PageChangeListener() {
            public void pageChanged(PagedTable.PagedTableChangeEvent event) {
                PagedTableContainer container = (PagedTableContainer) event.getTable().getContainerDataSource();
                int realSize = container.getRealSize();
                wrapperLayout.setCaption(realSize + " " + getTitleLabel());
                if (realSize < 10) {
                    table.setPageLength(realSize);
                    controlLayout.setVisible(false);
                } else {
                    table.setPageLength(10);
                    controlLayout.setVisible(true);
                }
            }
        });
        return wrapperLayout;
    }


    private void applyFilters() {
        JPAContainer container = getContainer();
        container.removeAllContainerFilters();
        container.addContainerFilter(new Compare.Equal("validated", true));

        if (keynwordField.getValue() != null && keynwordField.getValue().trim().length() > 0) {
            container.addContainerFilter(
                    new Or(new Like("title", "%" + keynwordField.getValue() + "%", false),
                            new Like("description", "%" + keynwordField.getValue() + "%", false))
            );
        }
        Category category = (Category) categoryField.getValue();
        if (category != null) {
            Collection<Category> children = AppUI.getDataProvider().getChildren(category);
            if (children.size() > 0) {
                List<Container.Filter> filters = new ArrayList<Container.Filter>();
                filters.add(new Compare.Equal("category", category));
                for (Category child : children) {
                    filters.add(new Compare.Equal("category", child));
                }
                container.addContainerFilter(new Or(filters.toArray(new Container.Filter[filters.size()])));
            } else container.addContainerFilter(new Compare.Equal("category", category));
        }
        Location location = (Location) locationField.getValue();
        if (location != null) {
            Collection<Location> children = AppUI.getDataProvider().getChildren(location);
            if (children.size() > 0) {
                List<Container.Filter> filters = new ArrayList<Container.Filter>();
                filters.add(new Compare.Equal("location", location));
                for (Location child : children) {
                    filters.add(new Compare.Equal("location", child));
                }
                container.addContainerFilter(new Or(filters.toArray(new Container.Filter[filters.size()])));
            } else container.addContainerFilter(new Compare.Equal("location", location));
        }
        if (cityField.getValue() != null) {
            container.addContainerFilter(new Compare.Equal("city", cityField.getValue()));
        }

        table.refreshRowCache();
        table.setCurrentPage(1);
        table.firePagedChangedEvent();
    }


    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {
        String parameters = event.getParameters();
        EntityItem<Ad> item = null;
        Layout layout;
        if (!parameters.isEmpty()) {
            item = getItem(parameters);
//            System.out.println("AdView.enter parameter: "+parameters + " item: "+item);
        }
        if (item != null) {
            layout = new AdLayout(item, getTitleLabel());
            setContent(layout);
        } else applyFilters();
    }
}
