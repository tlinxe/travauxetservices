package fr.travauxetservices.tools;

import com.vaadin.shared.JavaScriptExtensionState;

/**
 * Created by Phobos on 27/12/14.
 */
@SuppressWarnings("serial")
public class BootstrapTooltipState extends JavaScriptExtensionState {

    private String display;

    public String getDisplay() {

        return display;
    }

    public void setDisplay(String display) {

        this.display = display;
    }
}
