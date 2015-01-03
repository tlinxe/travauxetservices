package fr.travauxetservices.model;

import java.io.Serializable;

/**
 * Created by Phobos on 02/12/14.
 */
public enum Gender implements Serializable {
    MR("mr"), MS("ms"), MRS("mrs");

    String name;

    private Gender(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
