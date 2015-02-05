package fr.travauxetservices.component;

import fr.travauxetservices.tools.I18N;
import org.vaadin.teemu.ratingstars.RatingStars;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Phobos on 03/02/15.
 */
public class CustomRatingStars extends RatingStars {
    private static Map<Integer, String> valueCaptions = new HashMap<Integer, String>(5);

    static {
        valueCaptions.put(1, I18N.getString("rating.poor"));
        valueCaptions.put(2, I18N.getString("rating.below"));
        valueCaptions.put(3, I18N.getString("rating.average"));
        valueCaptions.put(4, I18N.getString("rating.above"));
        valueCaptions.put(5, I18N.getString("rating.excellent"));
    }

    public CustomRatingStars() {
        setMaxValue(5);
        setValueCaption(valueCaptions.values().toArray(new String[5]));
    }
}
