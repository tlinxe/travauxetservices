package fr.travauxetservices;

import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.PreserveOnRefresh;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.*;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import fr.travauxetservices.data.DataProvider;
import fr.travauxetservices.data.DummyDataGenerator;
import fr.travauxetservices.data.DummyDataProvider;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.User;
import fr.travauxetservices.views.MainView;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.util.Locale;
import java.util.ResourceBundle;

@Theme("mytheme")
@PreserveOnRefresh
@SuppressWarnings("serial")
public class AppUI extends UI {

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = AppUI.class, widgetset = "fr.travauxetservices.AppWidgetSet")
    public static class Servlet extends VaadinServlet {
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

    static {
        DummyDataGenerator.create();
    }

    public static final String PERSISTENCE_UNIT = "h2";
    public static ResourceBundle I18N = ResourceBundle.getBundle("i18n.messages");

    private DataProvider dataProvider;
    private CustomEventBus eventBus = new CustomEventBus();


    @Override
    protected void init(VaadinRequest request) {
        getPage().setTitle(I18N.getString("title"));
        setLocale(Locale.FRANCE);

        System.out.println("I18N " + I18N.getClass().getName());

        CustomEventBus.register(this);
        Responsive.makeResponsive(this);

        dataProvider = new DummyDataProvider();

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
        setContent(new MainView());
        getNavigator().navigateTo(getNavigator().getState());
    }

    @Subscribe
    public void userLoginRequested(final CustomEvent.UserLoginRequestedEvent event) {
        User user = getDataProvider().authenticate(event.getUserName(), event.getPassword());
        VaadinSession.getCurrent().setAttribute(User.class.getName(), user);
        if (user != null) {
            updateContent();
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

    /**
     * @return An instance for accessing the (dummy) services layer.
     */
    public static DataProvider getDataProvider() {
        return ((AppUI) getCurrent()).dataProvider;
    }

    public static CustomEventBus geEventbus() {
        return ((AppUI) getCurrent()).eventBus;
    }

    static public String getEncodedUrl() {
        String host = Page.getCurrent().getLocation().getHost();
        String path = Page.getCurrent().getLocation().getPath();
        int port = Page.getCurrent().getLocation().getPort();
        return "http://" + host + (port != 0 ? ":" + port : "");
    }
}
