package fr.travauxetservices;

import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.*;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import fr.travauxetservices.data.AppDataProvider;
import fr.travauxetservices.data.DataProvider;
import fr.travauxetservices.data.DummyDataGenerator;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.Configuration;
import fr.travauxetservices.model.User;
import fr.travauxetservices.tools.I18N;
import fr.travauxetservices.views.ConfigurationView;
import fr.travauxetservices.views.HomeView;
import fr.travauxetservices.views.MainView;

import javax.persistence.PersistenceException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import java.util.Locale;

@Theme("mytheme")
@PreserveOnRefresh
@SuppressWarnings("serial")
public class AppUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = AppUI.class, widgetset = "fr.travauxetservices.AppWidgetSet")
    static public class Servlet extends VaadinServlet {
        private static class MySystemMessagesProvider implements SystemMessagesProvider {
            @Override
            public SystemMessages getSystemMessages(SystemMessagesInfo systemMessagesInfo) {
                CustomizedSystemMessages messages = new CustomizedSystemMessages();
                messages.setCommunicationErrorCaption("Problème de communication");
                messages.setCommunicationErrorMessage("Prenez note de toutes les données non sauvegardées, et <u>Cliquez ici</ u> ou appuyez sur Echap pour continuer.");
                messages.setSessionExpiredCaption("Session expirée");
                messages.setSessionExpiredMessage("Prenez note de toutes les données non sauvegardées, et <u>Cliquez ici</u> ou appuyez sur Echap pour continuer.");
                return messages;
            }
        }

        @Override
        public void servletInitialized() throws ServletException {
            super.servletInitialized();
            getService().addSessionInitListener(new AppSessionInitListener());
            getService().addSessionInitListener(new SessionInitListener() {
                public void sessionInit(SessionInitEvent event) throws ServiceException {
                    event.getService().setSystemMessagesProvider(new MySystemMessagesProvider());
                }
            });
        }
    }

//    static {
//        SLF4JBridgeHandler.install();
//    }

    static public final String USERNAME_COOKIE = "username";
    static public final String PASSWORD_COOKIE = "password";
    static public final String REMEMBER_COOKIE = "remember";
    static public final String PERSISTENCE_UNIT = "h2";

    private Configuration configuration;
    private AppDataProvider dataProvider;
    private CustomEventBus eventBus = new CustomEventBus();


    @Override
    protected void init(VaadinRequest request) {
        getPage().setTitle(I18N.getString("application.title"));
        setLocale(Locale.FRANCE);

        CustomEventBus.register(this);
        Responsive.makeResponsive(this);
        addStyleName(ValoTheme.UI_WITH_MENU);

        configuration = new Configuration();
        dataProvider = new AppDataProvider();

        String remember = getValueCookie(REMEMBER_COOKIE);
        if ("TRUE".equalsIgnoreCase(remember)) {
            signin(getValueCookie(USERNAME_COOKIE), getValueCookie(PASSWORD_COOKIE));
        }

        updateContent();

        // Some views need to be aware of browser resize events so a
        // BrowserResizeEvent gets fired to the event but on every occasion.
        Page.getCurrent().addBrowserWindowResizeListener(
                new Page.BrowserWindowResizeListener() {
                    @Override
                    public void browserWindowResized(final Page.BrowserWindowResizeEvent event) {
                        CustomEventBus.post(new CustomEvent.BrowserResizeEvent());
                    }
                });
    }

    private void updateContent() {
        if (!getConfiguration().isSaved() || !getDataProvider().isReady()) {
            setContent(new ConfigurationView());
        }
        else {
            setContent(new MainView());
            getNavigator().navigateTo(getNavigator().getState());
            getNavigator().setErrorView(HomeView.class);
        }
    }

    @Subscribe
    public void userLoginRequested(final CustomEvent.UserLoginRequestedEvent event) {
        User user = event.getUser();
        if (user != null) {
            VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
            setCookie(USERNAME_COOKIE, user.getEmail());
            setCookie(PASSWORD_COOKIE, user.getPassword());
            setCookie(REMEMBER_COOKIE, Boolean.toString(event.isRemember()));
            updateContent();
        }
    }

    public void signin(String username, String password) {
        if (username != null && password != null) {
            User user = getDataProvider().authenticate(username, password);
            VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
            if (user != null) {
                updateContent();
            }
        }
    }

    @Subscribe
    public void userLoggedOut(final CustomEvent.UserLoggedOutEvent event) {
        // When the user logs out, current VaadinSession gets closed and the
        // page gets reloaded on the login screen. Do notice the this doesn't
        // invalidate the current HttpSession.
        VaadinSession.getCurrent().close();
        Page.getCurrent().reload();
    }

    @Subscribe
    public void closeOpenWindows(final CustomEvent.CloseOpenWindowsEvent event) {
        for (Window window : getWindows()) {
            window.close();
        }
    }

    private void setCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(60 * 60 * 24 * 30);
        // Set the cookie path.
        cookie.setPath(VaadinService.getCurrentRequest().getContextPath());

        // Save cookie
        VaadinService.getCurrentResponse().addCookie(cookie);
    }

    static private Cookie getCookie(String name) {
        // Fetch all cookies from the request
        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();

        // Iterate to find cookie by its name
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie;
            }
        }

        return null;
    }

    static public String getValueCookie(String name) {
        Cookie cookie = getCookie(name);
        return cookie != null ? cookie.getValue() : null;
    }

    static public Configuration getConfiguration() {
        return ((AppUI) getCurrent()).configuration;
    }

    static public AppDataProvider getDataProvider() {
        return ((AppUI) getCurrent()).dataProvider;
    }

    static public CustomEventBus geEventbus() {
        return ((AppUI) getCurrent()).eventBus;
    }

    static public String getEncodedUrl() {
        String host = Page.getCurrent().getLocation().getHost();
        String path = Page.getCurrent().getLocation().getPath();
        int port = Page.getCurrent().getLocation().getPort();
        return "http://" + host + (port != 0 ? ":" + port : "");
    }
}
