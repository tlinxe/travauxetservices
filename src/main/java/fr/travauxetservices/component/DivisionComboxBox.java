package fr.travauxetservices.component;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import fr.travauxetservices.MyVaadinUI;
import fr.travauxetservices.model.Division;

import java.util.Collection;

/**
 * Created by Phobos on 03/01/15.
 */
public class DivisionComboxBox extends HierarchicalComboBox {
    public DivisionComboxBox(String caption) {
        super(caption);
        BeanItemContainer<Division> divisionBeanItemContainer = new BeanItemContainer<Division>(Division.class);
        Collection<Division> divisions = MyVaadinUI.getDataProvider().getDivisions();
        for (Division element : divisions) {
            if (element.getParent() != null)
                continue;
            divisionBeanItemContainer.addItem(element);
            for (Division child : divisions) {
                if (child.getParent() == null)
                    continue;
                if (element.getId().equals(child.getParent().getId())) {
                    divisionBeanItemContainer.addItem(child);
                }
            }
        }
        setContainerDataSource(divisionBeanItemContainer);
        setItemCaptionMode(AbstractSelect.ItemCaptionMode.PROPERTY);
        //region.setContainerDataSource(MyVaadinUI.getDataProvider().getDivisionContainer());
        setItemCaptionPropertyId("name");
    }
}
