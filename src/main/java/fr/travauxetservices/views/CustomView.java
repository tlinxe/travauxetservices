package fr.travauxetservices.views;

import com.vaadin.navigator.View;

/**
 * Created by Phobos on 27/01/15.
 */
public interface CustomView extends View {
    public boolean isStateful(String navigationState);
}
