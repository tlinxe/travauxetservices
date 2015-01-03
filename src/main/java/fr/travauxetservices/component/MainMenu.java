package fr.travauxetservices.component;

import com.google.common.eventbus.Subscribe;
import com.vaadin.server.*;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.MyVaadinUI;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.IOToolkit;
import fr.travauxetservices.views.ViewType;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public final class MainMenu extends CustomComponent {

    public static final String ID = "dashboard-menu";
    public static final String REQUESTS_BADGE_ID = "requests-badge";
    public static final String OFFERS_BADGE_ID = "offers-badge";
    private static final String STYLE_VISIBLE = "valo-menu-visible";
    private Label offersBadge;
    private Label requestsBadge;
    private MenuBar.MenuItem settingsItem;

    public MainMenu() {
        addStyleName("valo-menu");
        setId(ID);
        setSizeUndefined();

        // There's only one DashboardMenu per UI so this doesn't need to be
        // unregistered from the UI-scoped DashboardEventBus.
        CustomEventBus.register(this);

        setCompositionRoot(buildContent());
    }

    private Component buildContent() {
        final CssLayout menuContent = new CssLayout();
        menuContent.addStyleName("sidebar");
        menuContent.addStyleName(ValoTheme.MENU_PART);
        menuContent.addStyleName("no-vertical-drag-hints");
        menuContent.addStyleName("no-horizontal-drag-hints");
        menuContent.setWidth(null);
        menuContent.setHeight("100%");

        menuContent.addComponent(buildTitle());
        menuContent.addComponent(buildUserMenu());
        menuContent.addComponent(buildToggleButton());
        menuContent.addComponent(buildMenuItems());

        return menuContent;
    }

    private Component buildTitle() {
        Label logo = new Label("<strong>Travaux</strong> et <strong>Services</strong>", ContentMode.HTML);
        logo.setSizeUndefined();
        HorizontalLayout logoWrapper = new HorizontalLayout(logo);
        logoWrapper.setComponentAlignment(logo, Alignment.MIDDLE_CENTER);
        logoWrapper.addStyleName("valo-menu-title");
        return logoWrapper;
    }

    private User getCurrentUser() {
        return (User) VaadinSession.getCurrent().getAttribute(User.class.getName());
    }

    private Component buildUserMenu() {
        final MenuBar settings = new MenuBar();
        settings.addStyleName("user-menu");
        settings.addStyleName("color1");
        final User user = getCurrentUser();
        settingsItem = settings.addItem("", null, null);
        updateUserName(null);
        return settings;
    }

    private Component buildToggleButton() {
        Button valoMenuToggleButton = new Button("Menu", new Button.ClickListener() {
            @Override
            public void buttonClick(final Button.ClickEvent event) {
                if (getCompositionRoot().getStyleName().contains(STYLE_VISIBLE)) {
                    getCompositionRoot().removeStyleName(STYLE_VISIBLE);
                } else {
                    getCompositionRoot().addStyleName(STYLE_VISIBLE);
                }
            }
        });
        valoMenuToggleButton.setIcon(FontAwesome.LIST);
        valoMenuToggleButton.addStyleName("valo-menu-toggle");
        valoMenuToggleButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        valoMenuToggleButton.addStyleName(ValoTheme.BUTTON_SMALL);
        return valoMenuToggleButton;
    }

    private Component buildMenuItems() {
        CssLayout menuItemsLayout = new CssLayout();
        menuItemsLayout.addStyleName("valo-menuitems");
        menuItemsLayout.setHeight(100.0f, Unit.PERCENTAGE);

        final User user = getCurrentUser();

        for (final ViewType view : ViewType.values()) {
            if (view.isDisabled()) continue;
            if (view.isAdmin() && (user == null || !user.isAdmin())) continue;
            if (view.isConnected() && user == null) continue;

            String submenu = view.getSubmenu();
            if (submenu != null) {
                Label label = new Label(submenu, ContentMode.HTML);
                label.setPrimaryStyleName("valo-menu-subtitle");
                label.addStyleName("h4");
                label.setSizeUndefined();
                menuItemsLayout.addComponent(label);
            }

            Component menuItemComponent = new ValoMenuItemButton(view);
            if (view == ViewType.OFFER) {
                offersBadge = new Label();
                offersBadge.setId(OFFERS_BADGE_ID);
                menuItemComponent = buildBadgeWrapper(menuItemComponent, offersBadge);
            }
            if (view == ViewType.REQUEST) {
                requestsBadge = new Label();
                requestsBadge.setId(REQUESTS_BADGE_ID);
                menuItemComponent = buildBadgeWrapper(menuItemComponent, requestsBadge);
            }

            menuItemsLayout.addComponent(menuItemComponent);
        }

        return menuItemsLayout;

    }

    private Component buildBadgeWrapper(final Component menuItemButton, final Component badgeLabel) {
        CssLayout dashboardWrapper = new CssLayout(menuItemButton);
        dashboardWrapper.addStyleName("badgewrapper");
        dashboardWrapper.addStyleName(ValoTheme.MENU_ITEM);
        dashboardWrapper.setWidth(100.0f, Unit.PERCENTAGE);
        badgeLabel.addStyleName(ValoTheme.MENU_BADGE);
        badgeLabel.setWidthUndefined();
        badgeLabel.setVisible(false);
        dashboardWrapper.addComponent(badgeLabel);
        return dashboardWrapper;
    }

    @Override
    public void attach() {
        super.attach();
        updateOffersCount(null);
        updateRequestsCount(null);
    }

    @Subscribe
    public void postViewChange(final CustomEvent.PostViewChangeEvent event) {
        // After a successful view change the menu can be hidden in mobile view.
        getCompositionRoot().removeStyleName(STYLE_VISIBLE);
    }

    @Subscribe
    public void updateOffersCount(final CustomEvent.NotificationsCountUpdatedEvent event) {
        int count = MyVaadinUI.getDataProvider().getOfferContainer().size();
        offersBadge.setValue(String.valueOf(count));
        offersBadge.setVisible(count > 0);
    }

    @Subscribe
    public void updateRequestsCount(final CustomEvent.ReportsCountUpdatedEvent event) {
        int count = MyVaadinUI.getDataProvider().getRequestContainer().size();
        requestsBadge.setValue(String.valueOf(count));
        requestsBadge.setVisible(count > 0);
    }

    @Subscribe
    public void updateUserName(final CustomEvent.ProfileUpdatedEvent event) {
        Resource resource = new ClassResource("/images/profile-pic-300px.jpg");
        final User user = getCurrentUser();
        settingsItem.removeChildren();
        if (user != null) {
            if (user.getPicture() != null) {
                resource = new StreamResource(new IOToolkit.ByteArraySource(user.getPicture()), "picture.png");
            }
            settingsItem.setIcon(resource);
            settingsItem.setText(user.getFirstName() + " " + user.getLastName());
            settingsItem.addItem("Sign Out", new MenuBar.Command() {
                @Override
                public void menuSelected(final MenuBar.MenuItem selectedItem) {
                    CustomEventBus.post(new CustomEvent.UserLoggedOutEvent());
                }
            });
        } else {
            settingsItem.setIcon(resource);
            settingsItem.setText("Se connecter");
            settingsItem.setCommand(new MenuBar.Command() {
                @Override
                public void menuSelected(final MenuBar.MenuItem selectedItem) {
                    ConnectionWindow.open();
                }
            });
        }
    }

    public final class ValoMenuItemButton extends Button {

        private static final String STYLE_SELECTED = "selected";

        private final ViewType view;

        public ValoMenuItemButton(final ViewType view) {
            this.view = view;
            setPrimaryStyleName("valo-menu-item");
            setIcon(view.getIcon());
            setCaption(view.getViewName().substring(0, 1).toUpperCase()
                    + view.getViewName().substring(1));
            CustomEventBus.register(this);
            addClickListener(new ClickListener() {
                @Override
                public void buttonClick(final ClickEvent event) {
                    UI.getCurrent().getNavigator()
                            .navigateTo(view.getViewName());
                }
            });

        }

        @Subscribe
        public void postViewChange(final CustomEvent.PostViewChangeEvent event) {
            removeStyleName(STYLE_SELECTED);
            if (event.getView() == view) {
                addStyleName(STYLE_SELECTED);
            }
        }
    }
}
