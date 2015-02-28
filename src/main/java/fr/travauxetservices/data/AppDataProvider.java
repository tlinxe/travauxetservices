package fr.travauxetservices.data;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.util.filter.Compare;
import fr.travauxetservices.AppUI;
import fr.travauxetservices.model.*;
import fr.travauxetservices.services.Mail;
import fr.travauxetservices.tools.I18N;
import fr.travauxetservices.views.ViewType;

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

    private JPAContainer<Category> categories;
    private JPAContainer<Division> divisions;
    private JPAContainer<Request> requests;
    private JPAContainer<Offer> offers;
    private JPAContainer<User> users;
    private JPAContainer<Message> messages;
    private JPAContainer<Rating> ratings;

    public AppDataProvider() {
        DummyDataGenerator.create();
        make();
    }

    public void make() {
        try {
            Configuration configuration = AppUI.getConfiguration();
            EntityManager em = Persistence.createEntityManagerFactory(AppUI.PERSISTENCE_UNIT, configuration.getProperties()).createEntityManager();
            categories = JPAContainerFactory.make(Category.class, em);
            categories.sort(new String[]{"name"}, new boolean[]{true});
            categories.setParentProperty("parent");

            divisions = JPAContainerFactory.make(Division.class, em);
            divisions.sort(new String[]{"name"}, new boolean[]{true});
            divisions.setParentProperty("parent");

            requests = JPAContainerFactory.make(Request.class, em);
            requests.sort(new String[]{"created"}, new boolean[]{false});

            offers = JPAContainerFactory.make(Offer.class, em);
            offers.sort(new String[]{"created"}, new boolean[]{false});

            users = JPAContainerFactory.make(User.class, em);
            messages = JPAContainerFactory.make(Message.class, em);
            ratings = JPAContainerFactory.make(Rating.class, em);
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

    public void addOffer(Ad a) throws UnsupportedOperationException, IllegalStateException {
        getOfferContainer().addEntity(new Offer(a));
    }

    public void addAd(Ad a) throws UnsupportedOperationException, IllegalStateException {
        if (a instanceof Offer) {
            AppUI.getDataProvider().addOffer(a);
        }
        if (a instanceof Request) {
            AppUI.getDataProvider().addRequest(a);
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
    public EntityItem<Division> getDivition(Object itemId) {
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

    public void addUser(User u) throws UnsupportedOperationException, IllegalStateException {
        getUserContainer().addEntity(u);

        String url = AppUI.getEncodedUrl() + "/#!" + ViewType.PROFILE.getViewName() + "/" + u.getId();
        String subject = I18N.getString("message.user.account.subject");
        String text = I18N.getString("message.user.account.text", new String[]{u.toString(), url});
        Mail.sendMail(u.toString(), subject, text, false);
    }

    public void removeUser(final Object itemId) throws UnsupportedOperationException {
        getUserContainer().removeItem(itemId);
    }

    @Override
    public User authenticate(String email, String password) {
        JPAContainer<User> container = getUserContainer();
        container.addContainerFilter(new Compare.Equal("email", email));
        container.addContainerFilter(new Compare.Equal("password", password));
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
        getMessageContainer().addEntity(m);
    }

    public JPAContainer<Rating> getRatingContainer() {
        ratings.removeAllContainerFilters();
        return ratings;
    }
}
