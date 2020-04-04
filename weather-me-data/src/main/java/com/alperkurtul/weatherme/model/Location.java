package com.alperkurtul.weatherme.model;

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
    @Column(name = "State", length = 50)
    private String state;
    @Column(name = "Country", length = 50)
    private String country;
    @Column(name = "Longitude")
    private String longitude;
    @Column(name = "Latitude")
    private String latitude;

}
