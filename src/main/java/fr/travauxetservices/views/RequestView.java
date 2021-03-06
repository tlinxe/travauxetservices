package fr.travauxetservices.views;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.tools.I18N;

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
        return I18N.getString("menu.requests");
    }

    @Override
    public JPAContainer getContainer() {
        return AppUI.getDataProvider().getRequestContainer();
    }

    @Override
    public EntityItem getItem(String parameters) {
        try {
            return AppUI.getDataProvider().getRequestItem(parameters);
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
