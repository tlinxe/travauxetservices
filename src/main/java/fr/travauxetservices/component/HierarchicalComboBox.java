package fr.travauxetservices.component;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Container;
import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.ComboBox;

import java.lang.reflect.Method;


/**
 * Created by Phobos on 19/12/14.
 */
public class HierarchicalComboBox extends ComboBox {
    public HierarchicalComboBox(String caption) {
        super(caption);
        addStyleName("hierarchical");
    }

    public HierarchicalComboBox(String caption, Container container) {
        super(caption, container);
        addStyleName("hierarchical");
    }

    public String getItemCaption(Object itemId) {
        boolean parent = false;
        String caption = super.getItemCaption(itemId);
        Item item = this.getItem(itemId);
        Object object = null;
        if (item instanceof EntityItem) {
            object = ((EntityItem) item).getEntity();
        }
        if (item instanceof BeanItem) {
            object = ((BeanItem) item).getBean();
        }
        if (object != null) {
            try {
                Method method = object.getClass().getMethod("getParent", null);
                if (method != null) {
                    Object value = method.invoke(object, null);
                    if (value != null) {
                        parent = true;
                    }
                }
            } catch (Exception e) {
                //Ignored
            }
        }
        return (parent ? "\u0009" : "") + caption;
    }
}
