package fr.travauxetservices.services;

import fr.travauxetservices.model.City;
import org.geonames.PostalCode;
import org.geonames.PostalCodeSearchCriteria;
import org.geonames.WebService;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Phobos on 23/01/15.
 */
public class Geonames {

    private final static Logger logger = Logger.getLogger(Geonames.class.getName());

    static public List<City> getCity(String filterPrefix) {
        List<City> result = new ArrayList<City>();
        try {
            WebService.setUserName("tlinxe"); // add your username here
            PostalCodeSearchCriteria criteria = new PostalCodeSearchCriteria();
            criteria.setCountryCode("FR");
            criteria.setPlaceName(filterPrefix);
            criteria.setMaxRows(20);

            List<PostalCode> values = WebService.postalCodeSearch(criteria);
            for (PostalCode postalCode : values) {
                result.add(new City(postalCode.getPlaceName(), postalCode.getPostalCode(), postalCode.getAdminCode1(), postalCode.getAdminCode2(), postalCode.getLatitude(), postalCode.getLongitude()));
//                System.out.println("CityComboBox name: " + postalCode.getPlaceName()  + " postal: " + postalCode.getPostalCode() + " code1: " + postalCode.getAdminCode1()+ " code2: " + postalCode.getAdminCode2()+ " name1: " + postalCode.getAdminName2());
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error Location", e);
        }
        return result;
    }

    static public String getRegion(double latitude, double longitude) {
        try {
            WebService.setUserName("tlinxe"); // add your username here
            PostalCodeSearchCriteria criteria = new PostalCodeSearchCriteria();
            criteria.setLatitude(latitude);
            criteria.setLongitude(longitude);
            List<PostalCode> values = WebService.findNearbyPostalCodes(criteria);
            for (PostalCode toponym : values) {
                return toponym.getPostalCode().substring(0, 2);
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error Location", e);
        }
        return null;
    }


    public static void main(String[] args) {
        getCity("bordeaux");
        //System.out.println(getRegion(44.8589869, -0.5607687));
    }
}
