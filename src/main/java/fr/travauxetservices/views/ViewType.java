package fr.travauxetservices.views;

import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import fr.travauxetservices.MyVaadinUI;

/**
 * Created by Phobos on 12/12/14.
 */
public enum ViewType {
    HOME("menu.welcome", HomeView.class, FontAwesome.HOME, null, true, false, false, false),
    OFFER("menu.offers", OfferView.class, FontAwesome.SHARE_SQUARE, null, false, false, false, false),
    REQUEST("menu.requests", RequestView.class, FontAwesome.SHARE_SQUARE_O, null, false, false, false, false),
    DEPOSI("menu.deposit", DepositView.class, FontAwesome.FILE_TEXT_O, null, true, false, false, false),
    PROFILE("menu.profile", ProfileView.class, FontAwesome.USER, "submenu.my.account", false, false, true, false),
    PREFERENCES("menu.preferences", PreferencesView.class, FontAwesome.COGS, null, false, false, true, false),
    VALIDATION("menu.validation", ValidationView.class, FontAwesome.CHECK, "submenu.administration", false, true, false, false),
    USER("menu.users", UserView.class, FontAwesome.USERS, null, false, true, false, false),
    INFORMATION("menu.legal.information", InformationView.class, FontAwesome.INFO_CIRCLE, "submenu.abount", false, false, false, false),
    ABOUT("menu.about", AboutView.class, FontAwesome.QUESTION, null, false, false, false, false),
    RULES("menu.rules.broadcasts", RulesView.class, FontAwesome.THUMBS_UP, null, false, false, false, false),
    TERMS("menu.terms", TermsView.class, FontAwesome.CHECK_CIRCLE_O, null, false, false, false, false);


    private final String viewName;
    private final Class<? extends View> viewClass;
    private final Resource icon;
    private final String submenu;
    private final boolean stateful;
    private final boolean admin;
    private final boolean connected;
    private final boolean disabled;

    private ViewType(final String viewName, final Class<? extends View> viewClass, final Resource icon, final String submenu, final boolean stateful, final boolean admin, final boolean connected, final boolean disabled) {
        this.viewName = viewName;
        this.viewClass = viewClass;
        this.icon = icon;
        this.submenu = submenu;
        this.stateful = stateful;
        this.admin = admin;
        this.connected = connected;
        this.disabled = disabled;
    }

    public boolean isStateful() {
        return stateful;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public String getViewName() {
        return MyVaadinUI.I18N.getString(viewName);
    }

    public String getSubmenu() {
        return submenu != null ? MyVaadinUI.I18N.getString(submenu) : null;
    }

    public Class<? extends View> getViewClass() {
        return viewClass;
    }

    public Resource getIcon() {
        return icon;
    }

    public static ViewType getByViewName(final String viewName) {
        ViewType result = null;
        for (ViewType viewType : values()) {
            if (viewType.getViewName().equals(viewName)) {
                result = viewType;
                break;
            }
        }
        return result;
    }

}
