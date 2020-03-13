package com.alperkurtul.weatherme.model;

import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class WeatherId implements Serializable {
    private String locationId;
    private String language;
    private String units;

    public WeatherId() {
    }

    public WeatherId(String locationId, String language, String units) {
        this.locationId = locationId;
        this.language = language;
        this.units = units;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getLanguage() {
        return language;
    }

    public String getUnits() {
        return units;
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
