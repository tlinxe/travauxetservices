package fr.travauxetservices.views;

import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import fr.travauxetservices.tools.I18N;

/**
 * Created by Phobos on 12/12/14.
 */
public enum ViewType {
    HOME("home", HomeView.class, FontAwesome.HOME, null, true, false, false, false),
    OFFER("offers", OfferView.class, FontAwesome.TABLE, null, true, false, false, false),
    REQUEST("requests", RequestView.class, FontAwesome.NAVICON, null, true, false, false, false),
    POST("post", PostView.class, FontAwesome.EDIT, null, true, false, false, false),
    PROFILE("profile", ProfileView.class, FontAwesome.USER, "submenu.my.account", true, false, true, false),
    PREFERENCES("preferences", PreferencesView.class, FontAwesome.COGS, null, true, false, true, false),
    CONFIGURATION("configuration", ConfigurationView.class, FontAwesome.FILE_CODE_O, "submenu.administration", true, true, false, false),
    VALIDATION("validation", ValidationView.class, FontAwesome.CHECK, null, true, true, false, false),
    USER("users", UserView.class, FontAwesome.USERS, null, true, true, false, false),
    INFORMATION("information", InformationView.class, FontAwesome.INFO_CIRCLE, "submenu.abount", false, false, false, false),
    ABOUT("about", AboutView.class, FontAwesome.QUESTION, null, false, false, false, false),
    RULES("rules", RulesView.class, FontAwesome.THUMBS_UP, null, false, false, false, false),
    TERMS("terms", TermsView.class, FontAwesome.CHECK_CIRCLE_O, null, false, false, false, false);


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
        return viewName;
    }

    public String getSubmenu() {
        return submenu != null ? I18N.getString(submenu) : null;
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
