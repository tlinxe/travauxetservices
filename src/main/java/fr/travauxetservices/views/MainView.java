package fr.travauxetservices.views;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
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

        /*
        VerticalLayout layout = new VerticalLayout();
        addComponent(layout);
        */

        ComponentContainer content = new CssLayout();
        content.addStyleName("view-content");
        content.setSizeFull();
        /*
        layout.addComponent(content);
        layout.addComponent(buildFooter());
        */
        addComponent(content);
        setExpandRatio(content, 1.0f);

        new CustomNavigator(content);
    }

    private Component buildFooter() {
        HorizontalLayout footer = new HorizontalLayout();
        footer.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        footer.setWidth(100, Unit.PERCENTAGE);
        footer.setSpacing(true);
        footer.addStyleName("dark");
        footer.addStyleName("footer");

        Link link1 = new Link("Infos légales", new ExternalResource("http://www.google.com"));
        link1.addStyleName(ValoTheme.LINK_SMALL);
        link1.setIcon(FontAwesome.INFO_CIRCLE);
        footer.addComponent(link1);

        Link link2 = new Link("Qui sommes-nous ?", new ExternalResource("http://www.google.com"));
        link2.addStyleName(ValoTheme.LINK_SMALL);
        link2.setIcon(FontAwesome.QUESTION);
        footer.addComponent(link2);

        Link link3 = new Link("Règles de diffusions", new ExternalResource("http://www.google.com"));
        link3.addStyleName(ValoTheme.LINK_SMALL);
        link3.setIcon(FontAwesome.THUMBS_UP);
        footer.addComponent(link3);

        Link link4 = new Link("Conditions Générales", new ExternalResource("http://www.google.com"));
        link4.addStyleName(ValoTheme.LINK_SMALL);
        link4.setIcon(FontAwesome.CHECK_CIRCLE_O);
        footer.addComponent(link4);

        Link link5 = new Link("Ajouter à mes favoris", new ExternalResource("http://www.google.com"));
        link5.addStyleName(ValoTheme.LINK_SMALL);
        link5.setIcon(FontAwesome.BOOKMARK);
        footer.addComponent(link5);

        return footer;
    }
}
