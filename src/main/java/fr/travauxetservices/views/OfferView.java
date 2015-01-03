package fr.travauxetservices.views;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import fr.travauxetservices.MyVaadinUI;
import fr.travauxetservices.model.Ad;
import fr.travauxetservices.model.Offer;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public final class OfferView extends AdView {
    public OfferView() {
        super(MyVaadinUI.I18N.getString("menu.offers"));
    }

    public JPAContainer getContainer() {
        return MyVaadinUI.getDataProvider().getOfferContainer();
    }

    public EntityItem getItem(String parameters) {
        return MyVaadinUI.getDataProvider().getOffer(Long.parseLong(parameters));
    }
}
