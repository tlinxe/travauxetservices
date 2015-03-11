package fr.travauxetservices.data;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.util.filter.Compare;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.event.CustomEvent;
import fr.travauxetservices.event.CustomEventBus;
import fr.travauxetservices.model.*;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceException;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Phobos on 13/12/14.
 */
public class AppDataProvider implements DataProvider {

    private boolean ready;

    private EntityManager em;
    private JPAContainer<Category> categories;
    private JPAContainer<Location> locations;
    private JPAContainer<Request> requests;
    private JPAContainer<Offer> offers;
    private JPAContainer<User> users;
    private JPAContainer<Message> messages;
    private JPAContainer<Rating> ratings;
    private JPAContainer<Notice> notices;

    public AppDataProvider() {
        DummyDataGenerator.create();
        make();
    }

    public void make() {
        try {
            Configuration configuration = AppUI.getConfiguration();
            em = Persistence.createEntityManagerFactory(AppUI.PERSISTENCE_UNIT, configuration.getProperties()).createEntityManager();
            categories = JPAContainerFactory.make(Category.class, em);
            categories.sort(new String[]{"name"}, new boolean[]{true});
            categories.setParentProperty("parent");

            locations = JPAContainerFactory.make(Location.class, em);
            locations.sort(new String[]{"name"}, new boolean[]{true});
            locations.setParentProperty("parent");

            requests = JPAContainerFactory.make(Request.class, em);
            requests.sort(new String[]{"created"}, new boolean[]{false});

            offers = JPAContainerFactory.make(Offer.class, em);
            offers.sort(new String[]{"created"}, new boolean[]{false});

            users = JPAContainerFactory.make(User.class, em);
            messages = JPAContainerFactory.make(Message.class, em);
            ratings = JPAContainerFactory.make(Rating.class, em);
            notices = JPAContainerFactory.make(Notice.class, em);
            ready = true;
        } catch (PersistenceException e) {
            e.printStackTrace();
            ready = false;
        }
    }

    public boolean isReady() {
        return ready;
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

    public void addRequest(Ad a) throws UnsupportedOperationException, IllegalStateException {
        requests.addEntity(new Request(a));
        requests.commit();
    }

    public void removeRequest(final Object itemId) throws UnsupportedOperationException {
        requests.removeItem(itemId);
        requests.commit();
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

    public void addOffer(Ad a) throws UnsupportedOperationException, IllegalStateException {
        offers.addEntity(new Offer(a));
        offers.commit();
    }

    public void removeOffer(final Object itemId) throws UnsupportedOperationException {
        offers.removeItem(itemId);
        offers.commit();
    }

    public void addAd(Ad a) throws UnsupportedOperationException, IllegalStateException {
        if (a instanceof Offer || a.getType().equals(Ad.Type.OFFER)) {
            addOffer(a);
        }
        if (a instanceof Request || a.getType().equals(Ad.Type.REQUEST)) {
            addRequest(a);
        }
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

    public JPAContainer<Notice> getNoticeContainer() {
        notices.removeAllContainerFilters();
        return notices;
    }

    public void addNotice(Notice n) throws UnsupportedOperationException {
        notices.addEntity(n);
        notices.commit();
        CustomEventBus.post(new CustomEvent.NotificationsCountUpdatedEvent());
    }

    public void removeNotice(final Object itemId) throws UnsupportedOperationException {
        notices.removeItem(itemId);
        notices.commit();
    }

    public int getUnreadNotificationsCount(User u) {
        if (u != null) {
            JPAContainer<Notice> container = getNoticeContainer();
            container.addContainerFilter(new Compare.Equal("user", u));
            return container.getItemIds().size();
        }
        return 0;
    }

    @Override
    public Collection<Notice> getNotices(User u) {
        Collection<Notice> values = new ArrayList<Notice>();
        if (u != null) {
            JPAContainer<Notice> container = getNoticeContainer();
            container.addContainerFilter(new Compare.Equal("user", u));
            for (Object itemId : container.getItemIds()) {
                EntityItem<Notice> item = container.getItem(itemId);
                values.add(item.getEntity());
            }
        }
        return values;
    }

    @Override
    public JPAContainer<Location> getLocationContainer() {
        locations.removeAllContainerFilters();
        return locations;
    }

    @Override
    public EntityItem<Location> getLocation(Object itemId) {
        return getLocationContainer().getItem(itemId);
    }

    @Override
    public Collection<Location> getLocations() {
        Collection<Location> values = new ArrayList<Location>();
        for (Object itemId : getLocationContainer().getItemIds()) {
            EntityItem<Location> item = getLocationContainer().getItem(itemId);
            values.add(item.getEntity());
        }
        return values;
    }

    @Override
    public Collection<Location> getChildren(Location c) {
        Collection<Location> values = new ArrayList<Location>();
        if (c != null) {
            Collection<Location> locations = getLocations();
            for (Location location : locations) {
                if (c.equals(location.getParent())) {
                    values.add(location);
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

    public void addUser(final User u) throws UnsupportedOperationException, IllegalStateException {
        users.addEntity(u);
        users.commit();

        Post.addedUser(u);
    }

    public void removeUser(final Object itemId) throws UnsupportedOperationException {
        EntityItem<User> user = getUser(itemId);
        if (user != null) {
            JPAContainer<Rating> ratingContainer = getRatingContainer();
            ratingContainer.addContainerFilter(new Compare.Equal("user", user.getEntity()));
            for (Object id : ratingContainer.getItemIds()) {
                removeRating(id);
            }

            JPAContainer<Notice> noticeContainer = getNoticeContainer();
            noticeContainer.addContainerFilter(new Compare.Equal("user", user.getEntity()));
            for (Object id : noticeContainer.getItemIds()) {
                removeNotice(id);
            }

            JPAContainer<Offer> offerContainer = getOfferContainer();
            offerContainer.addContainerFilter(new Compare.Equal("user", user.getEntity()));
            for (Object id : offerContainer.getItemIds()) {
                removeOffer(id);
            }

            JPAContainer<Request> requestContainer = getRequestContainer();
            requestContainer.addContainerFilter(new Compare.Equal("user", user.getEntity()));
            for (Object id : requestContainer.getItemIds()) {
                removeRequest(id);
            }

            users.removeItem(itemId);
            users.commit();
        }
    }

    @Override
    public User authenticate(String email, String password) {
        JPAContainer<User> container = getUserContainer();
        container.addContainerFilter(new Compare.Equal("email", email));
        container.addContainerFilter(new Compare.Equal("password", password));
        container.addContainerFilter(new Compare.Equal("validated", true));
        Object user = container.firstItemId();
        return user != null ? container.getItem(user).getEntity() : null;
    }

    public User findUser(String email) {
        JPAContainer<User> container = getUserContainer();
        container.addContainerFilter(new Compare.Equal("email", email));
        Object user = container.firstItemId();
        return user != null ? container.getItem(user).getEntity() : null;
    }

    public boolean hasUser(String email) {
        return findUser(email) != null;
    }

    public JPAContainer<Message> getMessageContainer() {
        messages.removeAllContainerFilters();
        return messages;
    }

    public EntityItem<Message> getMessage(final Object itemId) {
        return getMessageContainer().getItem(itemId);
    }

    public void addMessage(Message m) throws UnsupportedOperationException {
        messages.addEntity(m);
        messages.commit();
    }

    public JPAContainer<Rating> getRatingContainer() {
        ratings.removeAllContainerFilters();
        return ratings;
    }

    public void addRating(Rating r) throws UnsupportedOperationException {
        ratings.addEntity(r);
        ratings.commit();
    }

    public void removeRating(final Object itemId) throws UnsupportedOperationException {
        ratings.removeItem(itemId);
        ratings.commit();
    }
}
