package fr.travauxetservices.component;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.ui.Tree;
import fr.travauxetservices.MyVaadinUI;
import fr.travauxetservices.model.Category;

/**
 * Created by Phobos on 19/12/14.
 */
public class CategoryTree extends Tree {
    public CategoryTree(String caption) {
        super(caption);
        JPAContainer<Category> container = MyVaadinUI.getDataProvider().getCategoryContainer();
        setItemCaptionMode(ItemCaptionMode.PROPERTY);
        setItemCaptionPropertyId("name");
        setContainerDataSource(container);
        setSelectable(true);
        setMultiSelect(false);
        // Expand whole tree
        for (final Object id : rootItemIds()) {
            //expandItemsRecursively(id);
        }
    }

    public EntityItem<Category> getItem(Property<Category> property) {
        JPAContainer<Category> container = MyVaadinUI.getDataProvider().getCategoryContainer();
        return container.getItem(property);
    }
}
