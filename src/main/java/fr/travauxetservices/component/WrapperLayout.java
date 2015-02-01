package fr.travauxetservices.component;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by Phobos on 26/01/15.
 */
public class WrapperLayout extends CssLayout {
    private Label label;
    private MenuBar dropdown;
    public WrapperLayout(String caption, Component content) {
        setWidth(800, Unit.PIXELS);
        addStyleName(ValoTheme.LAYOUT_CARD);

        HorizontalLayout panelCaption = new HorizontalLayout();
        panelCaption.addStyleName("wrapperLayout");
        panelCaption.setWidth("100%");
        panelCaption.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        label = new Label(caption);
        label.addStyleName(ValoTheme.LABEL_H4);
        label.addStyleName(ValoTheme.LABEL_COLORED);
        label.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        panelCaption.addComponent(label);
        panelCaption.setExpandRatio(label, 1);
        //panelCaption.setComponentAlignment(label, Alignment.MIDDLE_LEFT);

        dropdown = new MenuBar();
        dropdown.addStyleName("borderless");
        dropdown.addStyleName("small");
        panelCaption.addComponent(dropdown);

        addComponent(panelCaption);
        addComponent(content);
    }

    public void setCaption(String caption) {
        label.setValue(caption);
    }

    public MenuBar.MenuItem addItem(String caption, Resource icon, MenuBar.Command command) {
        return dropdown.addItem(caption, icon, command);
    }
}
