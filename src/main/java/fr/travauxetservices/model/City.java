package fr.travauxetservices.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
    @Column(length = 2)
    String department;

    @NotNull
    @Column(length = 2)
    String region;

    double latitude;
    double longitude;

    public City() {

    }

    public City(int id, String name, String department, String region, double latitude, double longitude) {
        super();
        this.id = id;
        this.name = name;
        this.department = department;
        this.region = region;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
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

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return name + " (" + region + ")";
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
