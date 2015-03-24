package fr.travauxetservices.data;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import fr.travauxetservices.model.*;
import fr.travauxetservices.model.Notice;

import java.util.Collection;
import java.util.UUID;

/**
 * Created by Phobos on 13/12/14.
 */
public interface DataProvider {
    public JPAContainer<Request> getRequestContainer();

    public EntityItem<Request> getRequestItem(Object itemId);

    public Collection<Request> getRequests();

    public Object addRequest(Ad a) throws UnsupportedOperationException;

    public boolean removeRequest(final Object itemId) throws UnsupportedOperationException;

    public JPAContainer<Offer> getOfferContainer();

    public EntityItem<Offer> getOfferItem(Object itemId);

    public Collection<Offer> getOffers();

    public Object addOffer(Ad a) throws UnsupportedOperationException;

    public boolean removeOffer(final Object itemId) throws UnsupportedOperationException;

    public Object addAd(Ad a) throws UnsupportedOperationException;

    public JPAContainer<Category> getCategoryContainer();

    public Collection<Category> getCategories();

    public Collection<Category> getChildren(Category c);

    public JPAContainer<Notice> getNoticeContainer();

    public Object addNotice(Notice n) throws UnsupportedOperationException;

    public boolean removeNotice(final Object itemId) throws UnsupportedOperationException;

    public int getUnreadNotificationsCount(User u);

    public Collection<Notice> getNotices(User u);

    public JPAContainer<Location> getLocationContainer();

    public EntityItem<Location> getLocationItem(Object itemId);

    public Collection<Location> getLocations();

    public Collection<Location> getChildren(Location c);

    public JPAContainer<User> getUserContainer();

    public Collection<User> getUsers();

    public void refreshUser(User user);

    public Object addUser(User u) throws UnsupportedOperationException;

    public EntityItem<User> getUserItem(Object itemId);

    public boolean removeUser(Object itemId) throws UnsupportedOperationException;

    public User authenticate(String email, String password);

    public User findUser(String email);

    public boolean hasUser(String email);

    public JPAContainer<Message> getMessageContainer();

    public EntityItem<Message> getMessageItem(final Object itemId);

    public Object addMessage(Message m) throws UnsupportedOperationException;

    public JPAContainer<Rating> getRatingContainer();

    public Object addRating(Rating r) throws UnsupportedOperationException;

    public boolean removeRating(final Object itemId) throws UnsupportedOperationException;
}
