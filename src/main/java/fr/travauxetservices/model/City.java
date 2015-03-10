package fr.travauxetservices.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Created by Phobos on 24/12/14.
 */
@Entity
public class City implements Serializable {
    @Id
    int id;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(length = 255)
    String name;

    @NotNull
    @Column(length = 10)
    String postalCode;

    @NotNull
    @Column(length = 2)
    String department;

    @NotNull
    @Column(length = 2)
    String region;

    double latitude;
    double longitude;

    @Transient
    String fullName;

    private static final long serialVersionUID = 1L;

    public City() {
        super();
    }

    public City(String name, String postalCode, String department, String region, double latitude, double longitude) {
        super();
        this.name = name;
        this.postalCode = postalCode;
        this.department = department;
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return hashCode();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String detDepartment() {
        return department;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String setRegion() {
        return region;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double d) {
        this.latitude = d;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double d) {
        this.longitude = d;
    }

    public String getFullName() {
        return name + " - " + postalCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hash = 17;
        hash = hash * prime + this.getName().hashCode();
        hash = hash * prime + this.getPostalCode().hashCode();

        return hash;
    }

    @Override
    public String toString() {
        return getFullName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o != null) {
            if (o instanceof City) {
                return o.hashCode() == this.hashCode();
            }
        }
        return false;
    }
}
