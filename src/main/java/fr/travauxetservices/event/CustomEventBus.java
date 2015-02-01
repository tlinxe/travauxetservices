package fr.travauxetservices.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;
import fr.travauxetservices.AppUI;

/**
 * Created by Phobos on 12/12/14.
 */
public class CustomEventBus implements SubscriberExceptionHandler {

    private final EventBus eventBus = new EventBus(this);

    public static void post(final Object event) {
        AppUI.geEventbus().eventBus.post(event);
    }

    public static void register(final Object object) {
        AppUI.geEventbus().eventBus.register(object);
    }

    public static void unregister(final Object object) {
        AppUI.geEventbus().eventBus.unregister(object);
    }

    @Override
    public final void handleException(final Throwable exception, final SubscriberExceptionContext context) {
        exception.printStackTrace();
    }
}
