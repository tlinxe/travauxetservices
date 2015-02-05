package fr.travauxetservices.client;

import com.vaadin.client.ui.VFilterSelect;

public class HierarchicalComboBoxWidget extends VFilterSelect {

    public void setTextboxText(final String text) {
        super.setTextboxText(text != null ? text.replace("\u0009", "") : text);
    }
}
