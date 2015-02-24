package fr.travauxetservices.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by Phobos on 05/12/14.
 */
@Entity
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(length = 255)
    private String name;

    @ManyToOne
    @OrderBy("name")
    private Category parent;

    public Category() {

    }

    public Category(String name, Category parent) {
        super();
        this.name = name;
        this.parent = parent;
    }

    public Category(long id, String name, Category parent) {
        super();
        this.id = id;
        this.name = name;
        this.parent = parent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }


    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        if (parent != null) {
            text.append(parent.getName()).append("/");
        }
        text.append(name);
        return text.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (o instanceof Category) {
                return o.hashCode() == this.hashCode();
            }
        }
        return false;
    }
}