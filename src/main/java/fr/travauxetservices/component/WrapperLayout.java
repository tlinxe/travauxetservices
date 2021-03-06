package fr.travauxetservices.component;

import com.vaadin.server.Resource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

/**
 * Created by Phobos on 26/01/15.
 */
public class WrapperLayout extends CssLayout {
    private Label label;
    private HorizontalLayout panelCaption;

    public WrapperLayout(String caption, Component content) {
        setWidth(100, Unit.PERCENTAGE);
        addStyleName(ValoTheme.LAYOUT_CARD);

        panelCaption = new HorizontalLayout();
        panelCaption.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
        panelCaption.addStyleName("v-panel-caption");
        panelCaption.setWidth(100, Unit.PERCENTAGE);

        label = new Label(caption);
        label.addStyleName(ValoTheme.LABEL_H4);
        label.addStyleName(ValoTheme.LABEL_COLORED);
        label.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        panelCaption.addComponent(label);
        panelCaption.setExpandRatio(label, 1);
        panelCaption.setComponentAlignment(label, Alignment.MIDDLE_LEFT);


        addComponent(panelCaption);
        addComponent(content);
    }

    public void setCaption(String caption) {
        label.setValue(caption);
    }

    public void addCationComponent(Component component) {
        panelCaption.addComponent(component);
    }

    public MenuBar.MenuItem addItem(String caption, Resource icon, MenuBar.Command command) {
        MenuBar dropdown = new MenuBar();
        dropdown.addStyleName("borderless");
        dropdown.addStyleName("small");
        addCationComponent(dropdown);
        return dropdown.addItem(caption, icon, command);
    }
}
