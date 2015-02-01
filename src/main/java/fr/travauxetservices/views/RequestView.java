package fr.travauxetservices.views;

import com.google.common.eventbus.Subscribe;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.util.filter.Compare;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.Request;

import java.util.UUID;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public final class RequestView extends AdView {
    public RequestView() {
        super(ViewType.REQUEST.getViewName());
    }

    @Override
    public String getTitleLabel() {
        return AppUI.I18N.getString("menu.requests");
    }

    @Override
    public JPAContainer getContainer() {
        return AppUI.getDataProvider().getRequestContainer();
    }

    @Override
    public EntityItem getItem(String parameters) {
        try {
            UUID id = UUID.fromString(parameters);
            return AppUI.getDataProvider().getRequest(id);
        } catch (IllegalArgumentException e) {
            //Ignored
        }
        return null;
    }

    @Override
    public boolean isStateful(String navigationState) {
        if (null == navigationState) {
            return true;
        }
        boolean stateful = true;
        if (navigationState.length() > ViewType.REQUEST.getViewName().length() + 1) {
            String parameters = navigationState.substring(ViewType.REQUEST.getViewName().length() + 1);
            try {
                stateful = UUID.fromString(parameters) == null;
            } catch (IllegalArgumentException e) {
                //Ignored
            }
        }
        return stateful;
    }
}
