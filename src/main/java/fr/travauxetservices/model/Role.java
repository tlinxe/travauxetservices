package fr.travauxetservices.model;

import java.io.Serializable;

/**
 * Created by Phobos on 02/12/14.
 */
public enum Role implements Serializable {
    ADMIN("admin"), CUSTOMER("customer");

    String name;

    private Role(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
