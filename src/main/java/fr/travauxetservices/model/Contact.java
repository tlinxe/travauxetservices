package fr.travauxetservices.model;

import javax.persistence.Column;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by Phobos on 09/03/15.
 */
public class Contact implements Serializable {
    @NotNull
    @Size(min = 1, max = 255)
    @Column(length = 255)
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(length = 255)
    private String email;

    @Pattern(regexp = "^((\\+|00)33\\s?|0)[1-9](\\s?\\d{2}){4}$")
    private String phone;

    @Lob
    private String message;

    public Contact() {

    }

    public Contact(String name, String email, String phone, String message) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String s) {
        this.message = s;
    }
}
