package fr.travauxetservices;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.views.CustomView;
import fr.travauxetservices.views.ViewType;
import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

@SuppressWarnings("serial")
public class CustomNavigator extends Navigator {

    // Provide a Google Analytics tracker id here
    private static final String TRACKER_ID = null;// "UA-658457-6";
    private GoogleAnalyticsTracker tracker;

    private static final ViewType ERROR_VIEW = ViewType.HOME;
    private ViewProvider errorViewProvider;

    public CustomNavigator(final ComponentContainer container) {
        super(UI.getCurrent(), container);

        if (TRACKER_ID != null) {
            initGATracker(TRACKER_ID);
        }
        initViewChangeListener();
        initViewProviders();

    }

    private void initGATracker(final String trackerId) {
        tracker = new GoogleAnalyticsTracker(trackerId, "none");

        // GoogleAnalyticsTracker is an extension add-on for UI so it is
        // initialized by calling .extend(UI)
        tracker.extend(UI.getCurrent());
    }

    private void initViewChangeListener() {
        addViewChangeListener(new ViewChangeListener() {

            @Override
            public boolean beforeViewChange(final ViewChangeEvent event) {
                // Since there's no conditions in switching between the views
                // we can always return true.
                return true;
            }

            @Override
            public void afterViewChange(final ViewChangeEvent event) {
                ViewType view = ViewType.getByViewName(event.getViewName());
                // Appropriate events get fired after the view is changed.
                CustomEventBus.post(new CustomEvent.PostViewChangeEvent(view));
                CustomEventBus.post(new CustomEvent.BrowserResizeEvent());
                CustomEventBus.post(new CustomEvent.CloseOpenWindowsEvent());

                if (tracker != null) {
                    // The view change is submitted as a pageview for GA tracker
                    tracker.trackPageview("/" + event.getViewName());
                }
            }
        });
    }

    private void initViewProviders() {
        // A dedicated view provider is added for each separate view type
        for (final ViewType viewType : ViewType.values()) {
            ViewProvider viewProvider = new ClassBasedViewProvider(viewType.getViewName(), viewType.getViewClass()) {

                // This field caches an already initialized view instance if the
                // view should be cached (stateful views).
                private View cachedInstance;
                private String viewAndParameters;

                @Override
                public String getViewName(String viewAndParameters) {
                    this.viewAndParameters = viewAndParameters;
                    return super.getViewName(viewAndParameters);
                }

                @Override
                public View getView(final String viewName) {
                    View result = null;
                    if (viewType.getViewName().equals(viewName)) {
                        result = super.getView(viewType.getViewName());
                        boolean stateFul = viewType.isStateful();
                        if (result instanceof CustomView) {
                            stateFul = ((CustomView)result).isStateful(viewAndParameters);
                        }
                        if (stateFul) {
                            // Stateful views get lazily instantiated
                            if (cachedInstance == null) {
                                cachedInstance = result;
                            }
                            result = cachedInstance;
                        }
                    }
                    return result;
                }
            };

            if (viewType == ERROR_VIEW) {
                errorViewProvider = viewProvider;
            }

            addProvider(viewProvider);
        }

        setErrorProvider(new ViewProvider() {
            @Override
            public String getViewName(final String viewAndParameters) {
                return ERROR_VIEW.getViewName();
            }

            @Override
            public View getView(final String viewName) {
                return errorViewProvider.getView(ERROR_VIEW.getViewName());
            }
        });
    }
}
