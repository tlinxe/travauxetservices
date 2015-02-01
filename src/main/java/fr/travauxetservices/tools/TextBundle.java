package fr.travauxetservices.tools;

import fr.travauxetservices.AppUI;

import java.text.MessageFormat;

/**
 * Created by Phobos on 27/01/15.
 */
public class TextBundle {

    static public String getString(String key, Object... arguments) {
        try {
            String text = AppUI.I18N.getString(key);
            if (text != null) {
                return MessageFormat.format(text, arguments);
            }
            return text;
        } catch (Exception e) {
            return key;
        }
    }
}
