package fr.travauxetservices.model;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by Phobos on 02/12/14.
 */
@Entity
public class Offer extends Ad {
    public Offer() {

    }

    public Offer(Date created, User user, String title, String description, Category category, Division division, double price, Rate rate) {
        super(created, user, title, description, category, division, price, rate);
    }
}
