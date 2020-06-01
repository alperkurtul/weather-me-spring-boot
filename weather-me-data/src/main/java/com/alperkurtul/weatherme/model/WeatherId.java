package com.alperkurtul.weatherme.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WeatherId implements Serializable {
    @Column(name = "LocationId")
    private int locationId;
    @Column(name = "Language", length = 10)
    private String language;
    @Column(name = "Units", length = 10)
    private String units;

    public WeatherId() {
    }

    public WeatherId(int locationId, String language, String units) {
        this.locationId = locationId;
        this.language = language;
        this.units = units;
    }

    public int getLocationId() {
        return locationId;
    }

    public void setLocationId(int locationId) {
        this.locationId = locationId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeatherId weatherId = (WeatherId) o;
        return Objects.equals(locationId, weatherId.locationId) &&
                Objects.equals(language, weatherId.language) &&
                Objects.equals(units, weatherId.units);
    }

    @Override
    public int hashCode() {
        return Objects.hash(locationId, language, units);
    }

}
