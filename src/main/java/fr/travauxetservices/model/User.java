package fr.travauxetservices.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Phobos on 02/12/14.
 */
@Entity
@Table(uniqueConstraints =
@UniqueConstraint(columnNames = {"email"}))
public class User implements Serializable {
    @Id
    @Column(length = 255)
    private UUID id;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date created;

    @NotNull
    private Role role;

    private Gender gender;

    @Column(length = 255)
    private String firstName;

    @NotNull
    @Size(min=1, max=255)
    @Column(length = 255)
    private String lastName;

    @NotNull
    @Size(min=1, max=255)
    @Column(length = 255)
    private String email;

    @NotNull
    @Size(min=6,max=16)
    @Column(length = 16)
    private String password;

    @Lob
    private byte[] picture;

    @Pattern(regexp="^((\\+|00)33\\s?|0)[1-9](\\s?\\d{2}){4}$")
    String phone;

    protected boolean professional;
    boolean validated;

    public User() {
        id = UUID.randomUUID();
        created = new Date(System.currentTimeMillis());
        role = Role.CUSTOMER;
    }

    public User(UUID id, Date created, Role role, Gender gender, String firstName, String lastName, String email, String password, String phone, byte[] picture, boolean professional, boolean validated) {
        this.id = id;
        this.created = created;
        this.role = role;
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.picture = picture;
        this.professional = professional;
        this.validated = validated;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role r) {
        this.role = r;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] bytes) {
        this.picture = bytes;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    public boolean isProfessional() {
        return professional;
    }

    public void setProfessional(boolean professional) {
        this.professional = professional;
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean b) {
        this.validated = b;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }

    @Override
    public String toString() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (o instanceof User) {
                return o.hashCode() == this.hashCode();
            }
        }
        return false;
    }
}
