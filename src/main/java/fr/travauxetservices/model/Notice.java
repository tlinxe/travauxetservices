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
    @Column(length = 36)
    protected String id;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date created;

    @NotNull
    @ManyToOne
    protected User user;

    @NotNull
    @Size(min = 5, max = 255)
    @Column(length = 255)
    protected String action;

    @NotNull
    @Lob
    protected String content;

    boolean reading;

    public Notice() {
        id = UUID.randomUUID().toString();
        created = new Date(System.currentTimeMillis());
        reading = false;
    }

    public Notice(UUID id, Date created, User user, String action, String content, boolean reading) {
        this.id = id.toString();
        this.created = created;
        this.user = user;
        this.action = action;
        this.content = content;
        this.reading = reading;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public boolean isReading() {
        return reading;
    }

    public void setReading(boolean b) {
        this.reading = b;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (o instanceof Notice) {
                return o.hashCode() == this.hashCode();
            }
        }
        return false;
    }
}
