package fr.travauxetservices.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Phobos on 05/12/14.
 */
@Entity
public class Division implements Serializable {
    @Id
    @Column(length = 3)
    private String id;

    @NotNull
    @Column(length = 255)
    private String name;

    @ManyToOne
    @OrderBy("name")
    private Division parent;

    public Division() {

    }

    public Division(String id, String name, Division parent) {
        super();
        this.id = id;
        this.name = name;
        this.parent = parent;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Division getParent() {
        return parent;
    }

    public void setRegion(Division parent) {
        this.parent = parent;
    }


    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (o instanceof Division) {
                return o.hashCode() == this.hashCode();
            }
        }
        return false;
    }
}