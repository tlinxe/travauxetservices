package fr.travauxetservices.component;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ClickLabel extends VerticalLayout {
    Label label;

    public ClickLabel(String value) {
        label = new Label("<span style='cursor:pointer;'>" + value + "</span>", ContentMode.HTML);
        addComponent(label);
    }

    @Override
    public void addStyleName(String style) {
        label.addStyleName(style);
    }
}
