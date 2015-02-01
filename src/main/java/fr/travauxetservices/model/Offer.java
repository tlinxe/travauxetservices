package fr.travauxetservices.model;

import javax.persistence.Entity;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Phobos on 02/12/14.
 */
@Entity
public class Offer extends Ad {
    public Offer() {
        super();
        type = Type.OFFER;
    }

    public Offer(UUID id, Date created, User user, String title, String description, Category category, Division division, City city, double price, Remuneration remuneration, boolean validated, int priority) {
        super(id, created, user, title, description, category, division, city, price, remuneration, validated, priority);
    }

    public Offer(Ad ad) {
        super(ad.getId(), ad.getCreated(), ad.getUser(), ad.getTitle(), ad.getDescription(), ad.getCategory(), ad.getDivision(), ad.getCity(), ad.getPrice(), ad.getRemuneration(), ad.isValidated(), ad.getPriority());
    }
}
