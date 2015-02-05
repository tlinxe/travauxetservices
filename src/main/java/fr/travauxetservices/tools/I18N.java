package fr.travauxetservices.tools;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 * Created by Phobos on 27/01/15.
 */
public class I18N {
    public static ResourceBundle I18N = ResourceBundle.getBundle("i18n.messages");

    static public String getString(String key, Object... arguments) {
        try {
            String text = I18N.getString(key);
            if (text != null) {
                return MessageFormat.format(text, arguments);
            }
            return text;
        } catch (Exception e) {
            e.printStackTrace();
            return key;
        }
    }

    public static void main(String[] args) {
        System.out.println(fr.travauxetservices.tools.I18N.getString("message.ad.online.text", new String[]{"TITRE", "URL"}));
    }
}
