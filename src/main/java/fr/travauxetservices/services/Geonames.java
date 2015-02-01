package fr.travauxetservices.services;

import fr.travauxetservices.model.City;
import org.geonames.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Phobos on 23/01/15.
 */
public class Geonames {

    static public List<City> getCity(String filterPrefix) {
        List<City> result = new ArrayList<City>();
        try {
            WebService.setUserName("tlinxe"); // add your username here
            ToponymSearchCriteria criteria = new ToponymSearchCriteria();
            criteria.setCountryCode("FR");
            criteria.setStyle(Style.FULL);
            criteria.setQ(filterPrefix);
            criteria.setMaxRows(20);

            ToponymSearchResult searchResult = WebService.search(criteria);
            for (Toponym toponym : searchResult.getToponyms()) {
                System.out.println("CityComboBox id: " + toponym.getGeoNameId() + " name: " + toponym.getName() + " code: " + toponym.getAdminCode1() + " code: " + toponym.getAdminCode2() + " latitude: "+toponym.getLatitude() + " latitude: "+toponym.getLongitude());
                result.add(new City(toponym.getGeoNameId(), toponym.getName(), toponym.getAdminCode1(), toponym.getAdminCode2(), toponym.getLatitude(), toponym.getLongitude()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    static public String getRegion(double latitude, double longitude) {
        List<City> result = new ArrayList<City>();
        try {
            WebService.setUserName("tlinxe"); // add your username here
            PostalCodeSearchCriteria criteria = new PostalCodeSearchCriteria();
            criteria.setLatitude(latitude);
            criteria.setLongitude(longitude);
            List<PostalCode> toponyms = WebService.findNearbyPostalCodes(criteria);
            for (PostalCode toponym : toponyms) {
                return toponym.getPostalCode().substring(0,2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void main(String[] args) {
        getCity("aquitaine");
        //System.out.println(getRegion(44.8589869, -0.5607687));
    }
}
