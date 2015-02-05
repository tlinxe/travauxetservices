package fr.travauxetservices.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Phobos on 03/02/15.
 */
@Entity
public class Message implements Serializable {
    @Id
    @Column(length = 255)
    private String name;

    @NotNull
    @Lob
    private String text;

    public Message() {

    }

    public Message(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
