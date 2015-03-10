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
    private String content;

    public Message() {

    }

    public Message(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setContent(String text) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
