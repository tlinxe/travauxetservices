package fr.travauxetservices.component;

import com.vaadin.data.Container;
import com.vaadin.data.util.BeanItemContainer;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.Category;

import java.util.Collection;

/**
 * Created by Phobos on 03/01/15.
 */
public class CategoryComboxBox extends HierarchicalComboBox {
    public CategoryComboxBox(String caption) {
        super(caption);
        setContainerDataSource(getContainer());
        setItemCaptionMode(ItemCaptionMode.PROPERTY);
        setItemCaptionPropertyId("name");
    }

    public Container getContainer() {
        BeanItemContainer<Category> container = new BeanItemContainer<Category>(Category.class);
        Collection<Category> collection = AppUI.getDataProvider().getCategories();
        for (Category element : collection) {
            if (element.getParent() != null)
                continue;
            container.addItem(element);
            for (Category child : collection) {
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
