package com.alperkurtul.weatherme.model;

import org.hibernate.annotations.Index;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Location {

    @Id
    @Column(name = "LocationId")
    private int locationId;

    @Column(name = "LocationName", length = 50)
    private String locationName;

    @Column(name = "LowerCaseLocationName", length = 50)
    @Index(name = "LowerCaseLocationNameIndex")
    private String lowerCaseLocationName;

    @Column(name = "State", length = 50)
    private String state;

    @Column(name = "Country", length = 50)
    private String country;

    @Column(name = "Longitude")
    private String longitude;

    @Column(name = "Latitude")
    private String latitude;

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLowerCaseLocationName() {
        return lowerCaseLocationName;
    }

    public void setLowerCaseLocationName(String lowerCaseLocationName) {
        this.lowerCaseLocationName = lowerCaseLocationName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
