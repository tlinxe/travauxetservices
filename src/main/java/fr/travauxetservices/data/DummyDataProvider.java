package fr.travauxetservices.data;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.util.filter.Compare;
import fr.travauxetservices.MyVaadinUI;
import fr.travauxetservices.model.*;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Phobos on 13/12/14.
 */
public class DummyDataProvider implements DataProvider {

    private JPAContainer<Category> categories;
    private JPAContainer<Division> divisions;
    private JPAContainer<Request> requests;
    private JPAContainer<Offer> offers;
    private JPAContainer<User> users;

    public DummyDataProvider() {
        categories = JPAContainerFactory.make(Category.class, MyVaadinUI.PERSISTENCE_UNIT);
        categories.sort(new String[]{"name"}, new boolean[]{true});
        categories.setParentProperty("parent");

        divisions = JPAContainerFactory.make(Division.class, MyVaadinUI.PERSISTENCE_UNIT);
        divisions.sort(new String[]{"name"}, new boolean[]{true});
        divisions.setParentProperty("parent");

        requests = JPAContainerFactory.make(Request.class, MyVaadinUI.PERSISTENCE_UNIT);
        offers = JPAContainerFactory.make(Offer.class, MyVaadinUI.PERSISTENCE_UNIT);
        users = JPAContainerFactory.make(User.class, MyVaadinUI.PERSISTENCE_UNIT);
    }

    @Override
    public JPAContainer<Request> getRequestContainer() {
        return requests;
    }

    public EntityItem<Request> getRequest(Object itemId) {
        return requests.getItem(itemId);
    }

    @Override
    public Collection<Request> getRequests() {
        Collection<Request> values = new ArrayList<Request>();
        for (Object itemId : requests.getItemIds()) {
            EntityItem<Request> item = requests.getItem(itemId);
            values.add(item.getEntity());
        }
        return values;
    }

    @Override
    public JPAContainer<Offer> getOfferContainer() {
        return offers;
    }

    public EntityItem<Offer> getOffer(Object itemId) {
        return offers.getItem(itemId);
    }

    @Override
    public Collection<Offer> getOffers() {
        Collection<Offer> values = new ArrayList<Offer>();
        for (Object itemId : offers.getItemIds()) {
            EntityItem<Offer> item = offers.getItem(itemId);
            values.add(item.getEntity());
        }
        return values;
    }

    @Override
    public JPAContainer<Category> getCategoryContainer() {
        return categories;
    }

    @Override
    public Collection<Category> getCategories() {
        Collection<Category> values = new ArrayList<Category>();
        for (Object itemId : categories.getItemIds()) {
            EntityItem<Category> item = categories.getItem(itemId);
            values.add(item.getEntity());
        }
        return values;
    }

    @Override
    public Collection<Notice> getNotices() {
        return new ArrayList<Notice>(0);
    }

    @Override
    public JPAContainer<Division> getDivisionContainer() {
        return divisions;
    }

    @Override
    public Collection<Division> getDivisions() {
        Collection<Division> values = new ArrayList<Division>();
        for (Object itemId : divisions.getItemIds()) {
            EntityItem<Division> item = divisions.getItem(itemId);
            values.add(item.getEntity());
        }
        return values;
    }

    @Override
    public JPAContainer<User> getUserContainer() {
        return users;
    }

    @Override
    public Collection<User> getUsers() {
        Collection<User> values = new ArrayList<User>();
        for (Object itemId : users.getItemIds()) {
            EntityItem<User> item = users.getItem(itemId);
            values.add(item.getEntity());
        }
        return values;
    }

    public EntityItem<User> getUser(final User user) {
        return users.getItem(user.getId());
    }

    public void setUser(final User user) {
        EntityItem<User> item = users.getItem(user.getId());
        if (item != null) {
            users.refreshItem(user.getId());
            item.commit();
            System.out.println("DummyDataProvider.updateUserName user: " + user + " picture: " + item.getEntity().getPicture());
        }

    }

    @Override
    public User authenticate(String email, String password) {
        users.removeAllContainerFilters();
        users.addContainerFilter(new Compare.Equal("email", email));
        users.addContainerFilter(new Compare.Equal("password", password));
        users.applyFilters();
        return users.size() > 0 ? users.getItem(users.firstItemId()).getEntity() : null;
    }
}
