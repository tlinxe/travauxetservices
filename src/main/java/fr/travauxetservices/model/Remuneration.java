package fr.travauxetservices.model;

import fr.travauxetservices.tools.I18N;

import java.io.Serializable;

/**
 * Created by Phobos on 02/12/14.
 */
public enum Remuneration implements Serializable {
    HALF("half", true), TIME("time", true), DAY("day", true), WEEK("week", true), MONTH("month", true), TASK("task", true), PART("part", true), EXCHANGE("exchange", false);

    String name;
    boolean price;

    private Remuneration(String name, boolean price) {
        this.name = name;
        this.price = price;
    }

    public boolean hasPrice() {
        return price;
    }

    public String getLabel() {
        return I18N.getString("remuneration." + name);
    }

    public String getShortLabel() {
        return I18N.getString("remuneration."  + name + ".short");
    }

    @Override
    public String toString() {
        return name;
    }
}
