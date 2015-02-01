package fr.travauxetservices.component;

import com.vaadin.ui.ComboBox;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.Gender;

/**
* Created by Phobos on 25/01/15.
*/
public class GenderComboBox extends ComboBox {
    public GenderComboBox(String caption) {
        setCaption(caption);
        addItem(Gender.MR);
        setItemCaption(Gender.MR, AppUI.I18N.getString("gender.mr"));
        addItem(Gender.MRS);
        setItemCaption(Gender.MRS, AppUI.I18N.getString("gender.mrs"));
        addItem(Gender.MS);
        setItemCaption(Gender.MS, AppUI.I18N.getString("gender.ms"));
    }
}
