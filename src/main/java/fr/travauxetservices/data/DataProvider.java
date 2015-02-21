package fr.travauxetservices.data;

import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import fr.travauxetservices.model.*;

import java.util.Collection;

/**
 * Created by Phobos on 13/12/14.
 */
public interface DataProvider {
    public JPAContainer<Request> getRequestContainer();

    public EntityItem<Request> getRequest(Object itemId);

    public Collection<Request> getRequests();

    public void addRequest(Ad a) throws UnsupportedOperationException;

    public JPAContainer<Offer> getOfferContainer();

    public EntityItem<Offer> getOffer(Object itemId);

    public Collection<Offer> getOffers();

    public void addOffer(Ad a) throws UnsupportedOperationException;

    public void addAd(Ad a) throws UnsupportedOperationException;

    public JPAContainer<Category> getCategoryContainer();

    public Collection<Category> getCategories();

    public Collection<Category> getChildren(Category c);

    public Collection<Notice> getNotices();

    public JPAContainer<Division> getDivisionContainer();

    public EntityItem<Division> getDivition(Object itemId);

    public Collection<Division> getDivisions();

    public Collection<Division> getChildren(Division c);

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
}
