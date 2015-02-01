package fr.travauxetservices.model;

import java.io.Serializable;

/**
 * Created by Phobos on 02/12/14.
 */
public enum Remuneration implements Serializable {
    TIME("time", true), DAY("day", true), TASK("task", true), PART("part", true), EXCHANGE("exchange", false);

    String name;
    boolean price;

    private Remuneration(String name, boolean price) {
        this.name = name;
        this.price = price;
    }

    public boolean hasPrice() {
        return price;
    }

    @Override
    public String toString() {
        return name;
    }
}
