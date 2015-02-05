package fr.travauxetservices.component;

import com.vaadin.ui.ComboBox;
import fr.travauxetservices.model.Gender;
import fr.travauxetservices.tools.I18N;

/**
 * Created by Phobos on 25/01/15.
 */
public class GenderComboBox extends ComboBox {
    public GenderComboBox(String caption) {
        setCaption(caption);
        addItem(Gender.MR);
        setItemCaption(Gender.MR, I18N.getString("gender.mr"));
        addItem(Gender.MRS);
        setItemCaption(Gender.MRS, I18N.getString("gender.mrs"));
        addItem(Gender.MS);
        setItemCaption(Gender.MS, I18N.getString("gender.ms"));
    }
}
