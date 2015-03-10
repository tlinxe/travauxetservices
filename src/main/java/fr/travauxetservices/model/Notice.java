package fr.travauxetservices.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Phobos on 05/03/15.
 */
@Entity
public class Notice implements Serializable {
    @Id
    @Column(length = 255)
    protected UUID id;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date created;

    @NotNull
    @ManyToOne(cascade = CascadeType.REMOVE)
    protected User user;

    @NotNull
    @Size(min = 5, max = 255)
    @Column(length = 255)
    protected String action;

    @NotNull
    @Lob
    protected String content;

    boolean read;

    public Notice() {
        id = UUID.randomUUID();
        created = new Date(System.currentTimeMillis());
        read = false;
    }

    public Notice(UUID id, Date created, User user, String action, String content, boolean read) {
        this.id = id;
        this.created = created;
        this.user = user;
        this.action = action;
        this.content = content;
        this.read = read;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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

    public String getAction() {
        return action;
    }

    public void setAction(String s) {
        this.action = s;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String s) {
        this.content = s;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean b) {
        this.read = b;
    }
}
