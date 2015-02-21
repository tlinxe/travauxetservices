package fr.travauxetservices.services;

import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import elemental.json.JsonArray;
import elemental.json.JsonObject;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;

/**
 * Created by Phobos on 28/01/15.
 */
public class Location {
    static private LatLon point;

    static public LatLon getLocation() {
        if (point == null) {
            JavaScript.getCurrent().addFunction("onGeolocationFunction",
                    new JavaScriptFunction() {
                        @Override
                        public void call(JsonArray arguments) {
                            if (arguments != null && arguments.length() > 0) {
                                try {
                                    JsonObject position = arguments.getObject(0);
                                    JsonObject coords = position.getObject("coords");
                                    if (coords != null) {
                                        double longitude = coords.getNumber("longitude");
                                        double latitude = coords.getNumber("latitude");
                                        point = new LatLon(latitude, longitude);
                                        CustomEventBus.post(new CustomEvent.currentPostionEvent(point));
                                        //System.out.println("Location.getLocation latitude: " + point.getLat() + " longitude: " + point.getLon());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });

            JavaScript.getCurrent().execute("javascript:navigator.geolocation.getCurrentPosition(onGeolocationFunction)");
        }
        return point;
    }

}
