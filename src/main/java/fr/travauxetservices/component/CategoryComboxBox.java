package fr.travauxetservices.component;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import fr.travauxetservices.MyVaadinUI;
import fr.travauxetservices.model.Category;

import java.util.Collection;

/**
 * Created by Phobos on 03/01/15.
 */
public class CategoryComboxBox extends HierarchicalComboBox {
    public CategoryComboxBox(String caption) {
        super(caption);
        BeanItemContainer<Category> categoryBeanItemContainer = new BeanItemContainer<Category>(Category.class);
        Collection<Category> categories = MyVaadinUI.getDataProvider().getCategories();
        for (Category element : categories) {
            if (element.getParent() != null)
                continue;
            categoryBeanItemContainer.addItem(element);
            for (Category child : categories) {
                if (child.getParent() == null)
                    continue;
                if (element.getId().equals(child.getParent().getId())) {
                    categoryBeanItemContainer.addItem(child);
                }
            }
        }
        setContainerDataSource(categoryBeanItemContainer);
        setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        //category.setContainerDataSource(MyVaadinUI.getDataProvider().getCategoryContainer());
        setItemCaptionPropertyId("name");
    }
}
