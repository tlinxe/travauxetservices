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

    public JPAContainer<Offer> getOfferContainer();

    public EntityItem<Offer> getOffer(Object itemId);

    public Collection<Offer> getOffers();

    public JPAContainer<Category> getCategoryContainer();

    public Collection<Category> getCategories();

    public Collection<Notice> getNotices();

    public JPAContainer<Division> getDivisionContainer();

    public Collection<Division> getDivisions();

    public JPAContainer<User> getUserContainer();

    public Collection<User> getUsers();

    public void setUser(final User user);

    public EntityItem<User> getUser(final User user);

    public User authenticate(String email, String password);
}
