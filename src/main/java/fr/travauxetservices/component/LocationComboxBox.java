package fr.travauxetservices.component;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.Location;

import java.util.Collection;

/**
 * Created by Phobos on 03/01/15.
 */
public class LocationComboxBox extends HierarchicalComboBox {
    public LocationComboxBox(String caption) {
        super(caption);
        setContainerDataSource(getContainer());
        setItemCaptionMode(ItemCaptionMode.PROPERTY);
        setItemCaptionPropertyId("name");
    }

    public Container getContainer() {
        BeanItemContainer<Location> container = new BeanItemContainer<Location>(Location.class);
        Collection<Location> collection = AppUI.getDataProvider().getLocations();
        for (Location element : collection) {
            if (element.getParent() != null)
                continue;
            container.addItem(element);
            for (Location child : collection) {
                if (child.getParent() == null)
                    continue;
                if (element.getId().equals(child.getParent().getId())) {
                    container.addItem(child);
                }
            }
        }
        return container;
    }
}
