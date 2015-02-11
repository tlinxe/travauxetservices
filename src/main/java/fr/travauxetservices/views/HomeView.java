package fr.travauxetservices.views;

import com.google.common.eventbus.Subscribe;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.filter.JoinFilter;
import com.vaadin.data.Container;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Or;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.ClassResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Responsive;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tapio.googlemaps.GoogleMap;
import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.tapio.googlemaps.client.overlays.GoogleMapMarker;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.component.AdTable;
import fr.travauxetservices.component.ConnectionWindow;
import fr.travauxetservices.component.RegistrationWindow;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.City;
import fr.travauxetservices.model.Division;
import fr.travauxetservices.model.Notice;
import fr.travauxetservices.model.Offer;
import fr.travauxetservices.services.Geonames;
import fr.travauxetservices.services.Location;
import fr.travauxetservices.tools.I18N;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public final class HomeView extends Panel implements View {
    private NotificationsButton notificationsButton;
    private CssLayout mythemePanels;
    private final VerticalLayout root;
    private Window notificationsWindow;
    private GoogleMap map;
    private AdTable table;
    private JPAContainer container;

    public HomeView() {
        addStyleName(ValoTheme.PANEL_BORDERLESS);
        setSizeFull();
        CustomEventBus.register(this);

        root = new VerticalLayout();
        root.setSizeFull();
        root.setMargin(true);
        root.addStyleName("mytheme-view");
        setContent(root);
        Responsive.makeResponsive(root);

        root.addComponent(buildHeader());
        root.addComponent(buildSparklines());
        Component content = buildContent();
        root.addComponent(content);
        root.setExpandRatio(content, 1);

        // All the open sub-windows should be closed whenever the root layout
        // gets clicked.
        root.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(final LayoutEvents.LayoutClickEvent event) {
                CustomEventBus.post(new CustomEvent.CloseOpenWindowsEvent());
            }
        });
    }

    public void attach() {
        super.attach();
        setPostion(Location.getLocation());
    }

    private Component buildSparklines() {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidth(100, Unit.PERCENTAGE);
        layout.setSpacing(true);
        layout.addStyleName("sparks");
        Responsive.makeResponsive(layout);

        Image i2 = new Image(null, new ClassResource("/images/demande.jpg"));
        i2.setWidth(180, Unit.PIXELS);
        i2.setHeight(180, Unit.PIXELS);
        layout.addComponent(i2);

        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSpacing(true);
        Label h2 = new Label(I18N.getString("message.about"));
        h2.addStyleName(ValoTheme.LABEL_H2);
        verticalLayout.addComponent(h2);
        Label t2 = new Label(I18N.getString("message.about.text"), ContentMode.HTML);
        t2.addStyleName("small");
        verticalLayout.addComponent(t2);

        final Button registration = new Button(I18N.getString("registration") + "...", new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                RegistrationWindow.open();
            }
        });
        registration.addStyleName(ValoTheme.BUTTON_SMALL);
        registration.addStyleName(ValoTheme.BUTTON_FRIENDLY);
        verticalLayout.addComponent(registration);

        layout.addComponent(verticalLayout);
        layout.setExpandRatio(verticalLayout, 1f);

        return layout;
    }

    private Component buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.addStyleName("viewheader");
        header.setSpacing(true);

        Label titleLabel = new Label(I18N.getString("menu.home"));
        titleLabel.setSizeUndefined();
        titleLabel.addStyleName(ValoTheme.LABEL_H1);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        header.addComponent(titleLabel);

        notificationsButton = buildNotificationsButton();
        HorizontalLayout tools = new HorizontalLayout(notificationsButton);
        tools.setSpacing(true);
        tools.addStyleName("toolbar");
        header.addComponent(tools);

        return header;
    }

    private NotificationsButton buildNotificationsButton() {
        NotificationsButton result = new NotificationsButton();
        result.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent event) {
                openNotificationsPopup(event);
            }
        });
        return result;
    }

    private Component buildContent() {
        mythemePanels = new CssLayout();
        mythemePanels.addStyleName("mytheme-panels");
        Responsive.makeResponsive(mythemePanels);

        mythemePanels.addComponent(buildGoogleMap());
        mythemePanels.addComponent(buildTable());

        return mythemePanels;
    }

    private Component buildGoogleMap() {
        map = new GoogleMap(null, null, null);
        map.setCenter(new LatLon(46.80, 1.70));
        map.setZoom(5);
        map.setSizeFull();
        map.setMinZoom(4);
        map.setMaxZoom(16);
        LatLon postion = Location.getLocation();
        if (postion != null) {
            map.setCenter(postion);
            map.setZoom(11);
        }
        return createContentWrapper(map);
    }

    @Subscribe
    public void currentPostionEvent(final CustomEvent.currentPostionEvent event) {
        setPostion(event.getPostion());
    }

    public void setPostion(LatLon postion) {
        if (postion != null) {
            map.setCenter(postion);
            map.setZoom(11);
            String region = Geonames.getRegion(postion.getLat(), postion.getLon());
            if (region != null) {
                EntityItem<Division> item = AppUI.getDataProvider().getDivition(region);
                applyFilters(item != null ? item.getEntity() : null);
            }
        }
    }

    private Component buildTable() {
        table = new AdTable(10, true);
        table.setCaption("Top 10 des offres près de chez vous");
        table.addStyleName(ValoTheme.TABLE_BORDERLESS);
        table.addStyleName(ValoTheme.TABLE_NO_HORIZONTAL_LINES);
        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.addStyleName(ValoTheme.TABLE_SMALL);
        table.setRowHeaderMode(Table.RowHeaderMode.INDEX);
        table.setColumnHeaderMode(Table.ColumnHeaderMode.HIDDEN);
        table.setSizeFull();

        applyFilters(null);
        table.setContainerDataSource(getContainer());

        table.setVisibleColumns("user", "title", "division");
        table.setColumnHeaders("User", "Title", "Division");
        table.setColumnExpandRatio("title", 1);

        table.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            public void itemClick(ItemClickEvent e) {
                UI.getCurrent().getNavigator().navigateTo(ViewType.OFFER.getViewName() + "/" + e.getItem());
            }
        });

        Component contentWrapper = createContentWrapper(table);
        contentWrapper.addStyleName("top10-ad");
        return contentWrapper;
    }

    private JPAContainer getContainer() {
        if (container == null) {
            container =  AppUI.getDataProvider().getOfferContainer();
        }
        return container;
    }

    public void applyFilters(Division division) {
        JPAContainer container = getContainer();
        container.removeAllContainerFilters();
        container.addContainerFilter(new Compare.Equal("validated", true));

        if (division == null) {
            container.addContainerFilter(new Compare.Equal("division", null));
        }
        else {
            List<Container.Filter> filters = new ArrayList<Container.Filter>();
            filters.add(new Compare.Equal("division", division));
            filters.add(new JoinFilter("city", new Compare.Equal("region", division.getId())));
            container.addContainerFilter(new Or(filters.toArray(new Container.Filter[filters.size()])));
        }

        table.refreshRowCache();
        table.setCurrentPage(1);
        table.firePagedChangedEvent();

        int index = 1;
        for (Object itemId : container.getItemIds()) {
            EntityItem<Offer> item = container.getItem(itemId);
            City city = item.getEntity().getCity();
            if (city != null) {
                double latitude = city.getLatitude();
                double longitude = city.getLongitude();
                if (latitude != 0 && longitude != 0) {
                    GoogleMapMarker marker = new GoogleMapMarker(index + ". " + item.getEntity().getTitle(), new LatLon(latitude, longitude), false, null);
                    if (!map.hasMarker(marker)) map.addMarker(marker);
                }
            }
        }
    }


    private Component createContentWrapper(final Component content) {
        final CssLayout slot = new CssLayout();
        slot.setWidth("100%");
        slot.addStyleName("mytheme-panel-slot");

        CssLayout card = new CssLayout();
        card.setWidth("100%");
        card.addStyleName(ValoTheme.LAYOUT_CARD);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.addStyleName("mytheme-panel-toolbar");
        toolbar.setWidth("100%");

        Label caption = new Label(content.getCaption());
        caption.addStyleName(ValoTheme.LABEL_H4);
        caption.addStyleName(ValoTheme.LABEL_COLORED);
        caption.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        content.setCaption(null);

        MenuBar tools = new MenuBar();
        tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
        MenuBar.MenuItem max = tools.addItem("", FontAwesome.EXPAND, new MenuBar.Command() {

            @Override
            public void menuSelected(final MenuBar.MenuItem selectedItem) {
                if (!slot.getStyleName().contains("max")) {
                    selectedItem.setIcon(FontAwesome.COMPRESS);
                    toggleMaximized(slot, true);
                } else {
                    slot.removeStyleName("max");
                    selectedItem.setIcon(FontAwesome.EXPAND);
                    toggleMaximized(slot, false);
                }
            }
        });
        max.setStyleName("icon-only");
        MenuBar.MenuItem root = tools.addItem("", FontAwesome.COG, null);
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
        slot.addComponent(card);
        return slot;
    }

    private void openNotificationsPopup(final Button.ClickEvent event) {
        VerticalLayout notificationsLayout = new VerticalLayout();
        notificationsLayout.setMargin(true);
        notificationsLayout.setSpacing(true);

        Label title = new Label("Notifications");
        title.addStyleName(ValoTheme.LABEL_H3);
        title.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        notificationsLayout.addComponent(title);

        Collection<Notice> notices = AppUI.getDataProvider().getNotices();
        CustomEventBus.post(new CustomEvent.NotificationsCountUpdatedEvent());

        for (Notice notice : notices) {
            VerticalLayout notificationLayout = new VerticalLayout();
            notificationLayout.addStyleName("notification-item");

            Label titleLabel = new Label(notice.getFirstName() + " "
                    + notice.getLastName() + " "
                    + notice.getAction());
            titleLabel.addStyleName("notification-title");

            Label timeLabel = new Label(notice.getPrettyTime());
            timeLabel.addStyleName("notification-time");

            Label contentLabel = new Label(notice.getContent());
            contentLabel.addStyleName("notification-content");

            notificationLayout.addComponents(titleLabel, timeLabel,
                    contentLabel);
            notificationsLayout.addComponent(notificationLayout);
        }

        HorizontalLayout footer = new HorizontalLayout();
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR);
        footer.setWidth("100%");
        Button showAll = new Button("View All Notifications",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(final Button.ClickEvent event) {
                        Notification.show("Not implemented in this demo");
                    }
                });
        showAll.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        showAll.addStyleName(ValoTheme.BUTTON_SMALL);
        footer.addComponent(showAll);
        footer.setComponentAlignment(showAll, Alignment.TOP_CENTER);
        notificationsLayout.addComponent(footer);

        if (notificationsWindow == null) {
            notificationsWindow = new Window();
            notificationsWindow.setWidth(300.0f, Unit.PIXELS);
            notificationsWindow.addStyleName("notifications");
            notificationsWindow.setClosable(false);
            notificationsWindow.setResizable(false);
            notificationsWindow.setDraggable(false);
            notificationsWindow.setCloseShortcut(ShortcutAction.KeyCode.ESCAPE, null);
            notificationsWindow.setContent(notificationsLayout);
        }

        if (!notificationsWindow.isAttached()) {
            notificationsWindow.setPositionY(event.getClientY()
                    - event.getRelativeY() + 40);
            getUI().addWindow(notificationsWindow);
            notificationsWindow.focus();
        } else {
            notificationsWindow.close();
        }
    }

    private void toggleMaximized(final Component panel, final boolean maximized) {
        for (Iterator<Component> it = root.iterator(); it.hasNext(); ) {
            it.next().setVisible(!maximized);
        }
        mythemePanels.setVisible(true);

        for (Iterator<Component> it = mythemePanels.iterator(); it.hasNext(); ) {
            Component c = it.next();
            c.setVisible(!maximized);
        }

        if (maximized) {
            panel.setVisible(true);
            panel.addStyleName("max");
        } else {
            panel.removeStyleName("max");
        }
    }

    @Override
    public void enter(final ViewChangeListener.ViewChangeEvent event) {
        notificationsButton.updateNotificationsCount(null);
    }


    public static final class NotificationsButton extends Button {
        private static final String STYLE_UNREAD = "unread";
        public static final String ID = "mytheme-notifications";

        public NotificationsButton() {
            setIcon(FontAwesome.BELL);
            setId(ID);
            addStyleName("notifications");
            addStyleName(ValoTheme.BUTTON_ICON_ONLY);
            CustomEventBus.register(this);
        }

        @Subscribe
        public void updateNotificationsCount(final CustomEvent.NotificationsCountUpdatedEvent event) {
            //setUnreadCount(MyVaadinUI.getDataProvider().getUnreadNotificationsCount());
        }

        public void setUnreadCount(final int count) {
            setCaption(String.valueOf(count));

            String description = "Notifications";
            if (count > 0) {
                addStyleName(STYLE_UNREAD);
                description += " (" + count + " unread)";
            } else {
                removeStyleName(STYLE_UNREAD);
            }
            setDescription(description);
        }
    }
}
