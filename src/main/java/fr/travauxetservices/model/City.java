package fr.travauxetservices.model;

import java.io.Serializable;

/**
 * Created by Phobos on 24/12/14.
 */
public class City implements Serializable {
    Long id;
    String name;

    public City() {

    }

    public City(long id, String name) {
        super();
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
