package fr.travauxetservices.component;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.Division;

import java.util.Collection;

/**
 * Created by Phobos on 03/01/15.
 */
public class DivisionComboxBox extends HierarchicalComboBox {
    public DivisionComboxBox(String caption) {
        super(caption);
        setContainerDataSource(getContainer());
        setItemCaptionMode(ItemCaptionMode.PROPERTY);
        setItemCaptionPropertyId("name");
    }

    public Container getContainer() {
        BeanItemContainer<Division> container = new BeanItemContainer<Division>(Division.class);
        Collection<Division> collection = AppUI.getDataProvider().getDivisions();
        for (Division element : collection) {
            if (element.getParent() != null)
                continue;
            container.addItem(element);
            for (Division child : collection) {
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
