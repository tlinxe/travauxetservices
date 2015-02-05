package fr.travauxetservices.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Phobos on 01/02/15.
 */
@Entity
public class Rating {
    @Id
    @Column(length = 255)
    private UUID id;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date created;

    @NotNull
    @ManyToOne
    protected User user;

    @Column(length = 255)
    protected String title;

    @NotNull
    @Lob
    protected String description;

    int overall;
    int reception;
    int advice;
    int availability;
    int quality;
    int price;

    public Rating() {
        id = UUID.randomUUID();
    }

    public Rating(UUID id, Date created, User user, String title, String description, int overall, int reception, int advice, int availability, int quality, int price) {
        this.id = id;
        this.created = created;
        this.user = user;
        this.title = title;
        this.description = description;
        this.overall = overall;
        this.reception = reception;
        this.advice = advice;
        this.availability = availability;
        this.quality = quality;
        this.price = price;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String s) {
        this.title = s;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String s) {
        this.description = s;
    }

    public int getOverall() {
        return overall;
    }

    public void setOverall(int i) {
        this.overall = i;
    }

    public int getReception() {
        return reception;
    }

    public void setReception(int i) {
        this.reception = i;
    }

    public int getAdvice() {
        return advice;
    }

    public void setAdvice(int i) {
        this.advice = i;
    }

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int i) {
        this.availability = i;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int i) {
        this.quality = i;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int i) {
        this.price = i;
    }
}
