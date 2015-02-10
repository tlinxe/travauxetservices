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
public final class OfferView extends AdView {
    private JPAContainer container;

    public OfferView() {
        super(ViewType.OFFER.getViewName());
    }

    @Override
    public String getTitleLabel() {
        return I18N.getString("menu.offers");
    }

    @Override
    public JPAContainer getContainer() {
        if (container == null) {
            container =  AppUI.getDataProvider().getOfferContainer();
        }
        return container;
    }


    @Override
    public EntityItem getItem(String parameters) {
        try {
            UUID id = UUID.fromString(parameters);
            return AppUI.getDataProvider().getOffer(id);
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
        if (navigationState.length() > ViewType.OFFER.getViewName().length() + 1) {
            String parameters = navigationState.substring(ViewType.OFFER.getViewName().length() + 1);
            try {
                stateful = UUID.fromString(parameters) == null;
            } catch (IllegalArgumentException e) {
                //Ignored
            }
        }
        return stateful;
    }
}
