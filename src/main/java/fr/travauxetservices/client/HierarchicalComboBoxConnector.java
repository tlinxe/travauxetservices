package fr.travauxetservices.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.ui.combobox.ComboBoxConnector;
import com.vaadin.shared.ui.Connect;
import fr.travauxetservices.component.HierarchicalComboBox;


@Connect(HierarchicalComboBox.class)
public class HierarchicalComboBoxConnector extends ComboBoxConnector {
    private static final long serialVersionUID = 1L;

    @Override
    protected Widget createWidget() {
        return GWT.create(HierarchicalComboBoxWidget.class);
    }

    @Override
    public HierarchicalComboBoxWidget getWidget() {
        return (HierarchicalComboBoxWidget) super.getWidget();
    }
}
