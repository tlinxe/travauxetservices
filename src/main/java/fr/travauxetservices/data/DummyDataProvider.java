package fr.travauxetservices.data;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.util.filter.Compare;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.*;

import javax.persistence.EntityManager;
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
        categories = JPAContainerFactory.make(Category.class, AppUI.PERSISTENCE_UNIT);
        categories.sort(new String[]{"name"}, new boolean[]{true});
        categories.setParentProperty("parent");

        divisions = JPAContainerFactory.make(Division.class, AppUI.PERSISTENCE_UNIT);
        divisions.sort(new String[]{"name"}, new boolean[]{true});
        divisions.setParentProperty("parent");

        requests = JPAContainerFactory.make(Request.class, AppUI.PERSISTENCE_UNIT);
        requests.sort(new String[]{"created"}, new boolean[]{true});

        offers = JPAContainerFactory.make(Offer.class, AppUI.PERSISTENCE_UNIT);
        offers.sort(new String[]{"created"}, new boolean[]{true});

        users = JPAContainerFactory.make(User.class, AppUI.PERSISTENCE_UNIT);
    }

    @Override
    public JPAContainer<Request> getRequestContainer() {
        requests.removeAllContainerFilters();
        return requests;
    }

    public EntityItem<Request> getRequest(Object itemId) {
        return getRequestContainer().getItem(itemId);
    }

    @Override
    public Collection<Request> getRequests() {
        Collection<Request> values = new ArrayList<Request>();
        for (Object itemId : getRequestContainer().getItemIds()) {
            EntityItem<Request> item = getRequestContainer().getItem(itemId);
            values.add(item.getEntity());
        }
        return values;
    }

    public void addRequest(Ad a) throws UnsupportedOperationException {
        getRequestContainer().addEntity(new Request(a));
    }

    @Override
    public JPAContainer<Offer> getOfferContainer() {
        offers.removeAllContainerFilters();
        return offers;
    }

    public EntityItem<Offer> getOffer(Object itemId) {
        return getOfferContainer().getItem(itemId);
    }

    @Override
    public Collection<Offer> getOffers() {
        Collection<Offer> values = new ArrayList<Offer>();
        for (Object itemId : getOfferContainer().getItemIds()) {
            EntityItem<Offer> item = getOfferContainer().getItem(itemId);
            values.add(item.getEntity());
        }
        return values;
    }

    public void addOffer(Ad a) throws UnsupportedOperationException {
        getOfferContainer().addEntity(new Offer(a));
    }

    @Override
    public JPAContainer<Category> getCategoryContainer() {
        categories.removeAllContainerFilters();
        return categories;
    }

    @Override
    public Collection<Category> getCategories() {
        Collection<Category> values = new ArrayList<Category>();
        for (Object itemId : getCategoryContainer().getItemIds()) {
            EntityItem<Category> item = getCategoryContainer().getItem(itemId);
            values.add(item.getEntity());
        }
        return values;
    }

    @Override
    public Collection<Category> getChildren(Category c) {
        Collection<Category> values = new ArrayList<Category>();
        if (c != null) {
            Collection<Category> categories = getCategories();
            for (Category category : categories) {
                if (c.equals(category.getParent())) {
                    values.add(category);
                }
            }
        }
        return values;
    }

    @Override
    public Collection<Notice> getNotices() {
        return new ArrayList<Notice>(0);
    }

    @Override
    public JPAContainer<Division> getDivisionContainer() {
        divisions.removeAllContainerFilters();
        return divisions;
    }

    @Override
    public EntityItem<Division> getDivition(Object itemId){
        return getDivisionContainer().getItem(itemId);
    }

    @Override
    public Collection<Division> getDivisions() {
        Collection<Division> values = new ArrayList<Division>();
        for (Object itemId : getDivisionContainer().getItemIds()) {
            EntityItem<Division> item = getDivisionContainer().getItem(itemId);
            values.add(item.getEntity());
        }
        return values;
    }

    @Override
    public Collection<Division> getChildren(Division c) {
        Collection<Division> values = new ArrayList<Division>();
        if (c != null) {
            Collection<Division> divisions = getDivisions();
            for (Division division : divisions) {
                if (c.equals(division.getParent())) {
                    values.add(division);
                }
            }
        }
        return values;
    }

    @Override
    public JPAContainer<User> getUserContainer() {
        users.removeAllContainerFilters();
        return users;
    }

    @Override
    public Collection<User> getUsers() {
        Collection<User> values = new ArrayList<User>();
        for (Object itemId : getUserContainer().getItemIds()) {
            EntityItem<User> item = getUserContainer().getItem(itemId);
            values.add(item.getEntity());
        }
        return values;
    }

    public EntityItem<User> getUser(final User user) {
        return getUserContainer().getItem(user.getId());
    }

    public EntityItem<User> getUser(final Object itemId) {
        return getUserContainer().getItem(itemId);
    }

    public void setUser(final User user) {
        EntityItem<User> item = getUserContainer().getItem(user.getId());
        if (item != null) {
            getUserContainer().refreshItem(user.getId());
            item.commit();
        }

    }

    public void addUser(User u) throws UnsupportedOperationException {
        getUserContainer().addEntity(u);
    }

    @Override
    public User authenticate(String email, String password) {
        JPAContainer<User> container = getUserContainer();
        container.addContainerFilter(new Compare.Equal("email", email));
        container.addContainerFilter(new Compare.Equal("password", password));
        Object user = container.firstItemId();
        container.removeAllContainerFilters();
        return user != null ? container.getItem(user).getEntity() : null;
    }
}
