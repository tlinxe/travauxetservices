package fr.travauxetservices.component;

import com.vaadin.ui.ComboBox;
import fr.travauxetservices.model.Role;

/**
* Created by Phobos on 25/01/15.
*/
public class RoleComboBox extends ComboBox {
    public RoleComboBox(String caption) {
        setCaption(caption);
        addItem(Role.ADMIN);
        setItemCaption(Role.ADMIN, "Admin");
        addItem(Role.CUSTOMER);
        setItemCaption(Role.CUSTOMER, "Customer.");
    }
}
