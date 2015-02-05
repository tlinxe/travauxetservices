package fr.travauxetservices.component;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.data.util.filter.UnsupportedFilterException;
import com.vaadin.ui.ComboBox;
import fr.travauxetservices.model.City;
import fr.travauxetservices.services.Geonames;

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

    public void setValue(Object newValue) {
        if (newValue != null && getValue() == null) {
            addItem(newValue);
        }
        super.setValue(newValue);
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
            return Geonames.getCity(filterPrefix);
        }

        private void filterItems(String filterString) {
            removeAllItems();
            addAll(getCities(filterString));
        }
    }
}
