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

    public OfferView() {
        super(ViewType.OFFER.getViewName());
    }

    @Override
    public String getTitleLabel() {
        return I18N.getString("menu.offers");
    }

    @Override
    public JPAContainer getContainer() {
        return AppUI.getDataProvider().getOfferContainer();
    }


    @Override
    public EntityItem getItem(String parameters) {
        try {
            return AppUI.getDataProvider().getOfferItem(parameters);
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
