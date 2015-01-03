package fr.travauxetservices.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by Phobos on 02/12/14.
 */
@Entity
@Table(uniqueConstraints =
@UniqueConstraint(columnNames = {"email"}))
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private Role role;
    @NotNull
    private Gender gender;
    @NotNull
    @Column(length = 255)
    private String firstName;
    @NotNull
    @Column(length = 255)
    private String lastName;
    @NotNull
    @Column(length = 255)
    private String email;
    @NotNull
    @Column(length = 255, name = "password")
    private String password;
    private byte[] picture;

    public User() {

    }

    public User(Role role, Gender gender, String firstName, String lastName, String email, String password, byte[] picture) {
        super();
        this.role = role;
        this.gender = gender;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.picture = picture;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] bytes) {
        this.picture = bytes;
    }

    public boolean isAdmin() {
        return role == Role.ADMIN;
    }

    @Override
    public String toString() {
        return gender.name() + " " + firstName + " " + lastName;
    }
}
