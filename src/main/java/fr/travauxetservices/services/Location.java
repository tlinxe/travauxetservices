package fr.travauxetservices.services;

import com.vaadin.tapio.googlemaps.client.LatLon;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.JavaScriptFunction;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import org.json.JSONArray;
import org.json.JSONObject;

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
                        public void call(JSONArray arguments) {
                            if (arguments != null && arguments.length() > 0) {
                                try {
                                    JSONObject position = arguments.getJSONObject(0);
                                    JSONObject coords = position.getJSONObject("coords");
                                    if (coords != null) {
                                        double longitude = coords.getDouble("longitude");
                                        double latitude = coords.getDouble("latitude");
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
