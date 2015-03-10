package fr.travauxetservices.data;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import fr.travauxetservices.model.*;
import fr.travauxetservices.model.Notice;

import java.util.Collection;

/**
 * Created by Phobos on 13/12/14.
 */
public interface DataProvider {
    public JPAContainer<Request> getRequestContainer();

    public EntityItem<Request> getRequest(Object itemId);

    public Collection<Request> getRequests();

    public void addRequest(Ad a) throws UnsupportedOperationException;

    public void removeRequest(final Object itemId) throws UnsupportedOperationException;

    public JPAContainer<Offer> getOfferContainer();

    public EntityItem<Offer> getOffer(Object itemId);

    public Collection<Offer> getOffers();

    public void addOffer(Ad a) throws UnsupportedOperationException;

    public void removeOffer(final Object itemId) throws UnsupportedOperationException;

    public void addAd(Ad a) throws UnsupportedOperationException;

    public JPAContainer<Category> getCategoryContainer();

    public Collection<Category> getCategories();

    public Collection<Category> getChildren(Category c);

    public JPAContainer<Notice> getNoticeContainer();

    public void addNotice(Notice n) throws UnsupportedOperationException;

    public void removeNotice(final Object itemId) throws UnsupportedOperationException;

    public int getUnreadNotificationsCount(User u);

    public Collection<Notice> getNotices(User u);

    public JPAContainer<Location> getLocationContainer();

    public EntityItem<Location> getLocation(Object itemId);

    public Collection<Location> getLocations();

    public Collection<Location> getChildren(Location c);

    public JPAContainer<User> getUserContainer();

    public Collection<User> getUsers();

    public void setUser(final User user);

    public void addUser(User u) throws UnsupportedOperationException;

    public EntityItem<User> getUser(Object itemId);

    public void removeUser(final Object itemId) throws UnsupportedOperationException;

    public User authenticate(String email, String password);

    public User findUser(String email);

    public boolean hasUser(String email);

    public JPAContainer<Message> getMessageContainer();

    public EntityItem<Message> getMessage(final Object itemId);

    public void addMessage(Message m) throws UnsupportedOperationException;

    public JPAContainer<Rating> getRatingContainer();

    public void addRating(Rating r) throws UnsupportedOperationException;

    public void removeRating(final Object itemId) throws UnsupportedOperationException;
}
