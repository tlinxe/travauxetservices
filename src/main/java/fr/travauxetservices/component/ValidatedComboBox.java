package fr.travauxetservices.component;

import com.vaadin.ui.ComboBox;

/**
 * Created by Phobos on 25/01/15.
 */
public class ValidatedComboBox extends ComboBox {
    public ValidatedComboBox(String caption) {
        setCaption(caption);
        addItem(Boolean.TRUE);
        setItemCaption(Boolean.TRUE, "True");
        addItem(Boolean.FALSE);
        setItemCaption(Boolean.FALSE, "False");
    }
}
