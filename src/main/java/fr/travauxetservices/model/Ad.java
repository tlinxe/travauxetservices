package fr.travauxetservices.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Phobos on 02/12/14.
 */
@MappedSuperclass
public class Ad implements Serializable {
    public enum Type {OFFER, REQUEST}

    ;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @Temporal(TemporalType.TIMESTAMP)
    protected Date created;
    @NotNull
    @ManyToOne
    protected User user;
    @NotNull
    @Column(length = 255)
    protected String title;
    @NotNull
    protected String description;
    @NotNull
    @ManyToOne
    protected Category category;
    @NotNull
    @ManyToOne
    protected Division division;
    protected double price;
    @NotNull
    protected Rate rate;
    @Transient
    private Type type;


    public Ad() {

    }


    public Ad(Date created, User user, String title, String description, Category category, Division division, double price, Rate rate) {
        super();
        this.created = created;
        this.user = user;
        this.title = title;
        this.description = description;
        this.category = category;
        this.division = division;
        this.price = price;
        this.rate = rate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Rate getRate() {
        return rate;
    }

    public void setRate(Rate rate) {
        this.rate = rate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type t) {
        this.type = t;
    }
}
