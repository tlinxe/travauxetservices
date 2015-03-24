package fr.travauxetservices.model;

import javax.persistence.Entity;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Phobos on 02/12/14.
 */
@Entity
public class Request extends Ad {
    public Request() {
        super();
        type = Type.REQUEST;
    }

    public Request(UUID id, Date created, User user, String title, String description, Category category, Location location, City city, double price, Remuneration remuneration, boolean validated, int priority) {
        super(id.toString(), created, user, title, description, category, location, city, price, remuneration, validated, priority);
    }

    public Request(Ad ad) {
        super(ad.getId(), ad.getCreated(), ad.getUser(), ad.getTitle(), ad.getDescription(), ad.getCategory(), ad.getLocation(), ad.getCity(), ad.getPrice(), ad.getRemuneration(), ad.isValidated(), ad.getPriority());
    }
}
