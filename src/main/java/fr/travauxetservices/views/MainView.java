package fr.travauxetservices.views;

import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import fr.travauxetservices.CustomNavigator;
import fr.travauxetservices.component.MainMenu;

/**
 * Created by Phobos on 12/12/14.
 */
@SuppressWarnings("serial")
public class MainView extends HorizontalLayout {

    public MainView() {
        setSizeFull();
        addStyleName("mainview");

        addComponent(new MainMenu());

        ComponentContainer content = new CssLayout();
        content.setPrimaryStyleName("view-content");
        content.addStyleName("v-scrollable");
        content.setSizeFull();
        addComponent(content);
        setExpandRatio(content, 1.0f);

        new CustomNavigator(content);
    }
}
