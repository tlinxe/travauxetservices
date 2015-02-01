package fr.travauxetservices.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Phobos on 02/12/14.
 */
@MappedSuperclass
public class Ad implements Serializable {
    public enum Type {OFFER, REQUEST}

    ;
    @Id
    @Column(length = 255)
    protected UUID id;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date created;

    @NotNull
    @ManyToOne
    protected User user;

    @NotNull
    @Size(min = 5, max = 255)
    @Column(length = 255)
    protected String title;

    @NotNull
    @Lob
    protected String description;

    @NotNull
    @ManyToOne
    protected Category category;

    @NotNull
    @ManyToOne
    protected Division division;

    @ManyToOne
    protected City city;

    protected double price;

    @NotNull
    protected Remuneration remuneration;

    boolean validated;
    int priority;

    @Transient
    protected Type type;


    public Ad() {
        id = UUID.randomUUID();
        created = new Date(System.currentTimeMillis());
        validated = false;
        priority = 0;
    }


    public Ad(UUID id, Date created, User user, String title, String description, Category category, Division division, City city, double price, Remuneration remuneration, boolean validated, int priority) {
        this.id = id;
        this.created = created;
        this.user = user;
        this.title = title;
        this.description = description;
        this.category = category;
        this.division = division;
        this.city = city;
        this.price = price;
        this.remuneration = remuneration;
        this.validated = validated;
        this.priority = priority;
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

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double d) {
        this.price = d;
    }

    public Remuneration getRemuneration() {
        return remuneration;
    }

    public void setRemuneration(Remuneration remuneration) {
        this.remuneration = remuneration;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean b) {
        this.validated = b;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type t) {
        this.type = t;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int i) {
        this.priority = i;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (o instanceof Ad) {
                return o.hashCode() == this.hashCode();
            }
        }
        return false;
    }
}
