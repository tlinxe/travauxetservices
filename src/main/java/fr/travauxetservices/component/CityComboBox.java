package fr.travauxetservices.component;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.filter.UnsupportedFilterException;
import com.vaadin.ui.ComboBox;
import fr.travauxetservices.model.City;
import org.geonames.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Phobos on 19/12/14.
 */
public class CityComboBox extends ComboBox {
    public CityComboBox(String caption) {
        super(caption, new CityContainer());
        setItemCaptionMode(ItemCaptionMode.PROPERTY);
        setItemCaptionPropertyId("name");
    }


    static public class CityContainer extends BeanItemContainer<City> {
        public CityContainer() throws IllegalArgumentException {
            super(City.class);
        }

        @Override
        protected void addFilter(Filter filter) throws UnsupportedFilterException {
            filterItems(((SimpleStringFilter) filter).getFilterString());
        }

        private List<City> getCities(String filterPrefix) {
            if ("".equals(filterPrefix) || filterPrefix == null) {
                return Collections.emptyList();
            }
            List<City> result = new ArrayList<City>();

            try {
                WebService.setUserName("tlinxe"); // add your username here
                ToponymSearchCriteria searchCriteria = new ToponymSearchCriteria();
                searchCriteria.setCountryCode("FR");
                searchCriteria.setStyle(Style.FULL);
                searchCriteria.setQ(filterPrefix);
                ToponymSearchResult searchResult = WebService.search(searchCriteria);
                for (Toponym toponym : searchResult.getToponyms()) {
                    System.out.println("CityComboBox id: " + toponym.getGeoNameId() + " name: " + toponym.getName() + " code: " + toponym.getAdminCode1() + " code: " + toponym.getAdminCode2());
                    result.add(new City(toponym.getGeoNameId(), toponym.getName()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return result;
        }

        private void filterItems(String filterString) {
            removeAllItems();
            addAll(getCities(filterString));
        }
    }
}
