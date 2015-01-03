package fr.travauxetservices.views;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import fr.travauxetservices.MyVaadinUI;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public final class RequestView extends AdView {
    public RequestView() {
        super(MyVaadinUI.I18N.getString("menu.requests"));
    }

    public JPAContainer getContainer() {
        return MyVaadinUI.getDataProvider().getRequestContainer();
    }

    public EntityItem getItem(String parameters) {
        return MyVaadinUI.getDataProvider().getRequest(Long.parseLong(parameters));
    }
}
