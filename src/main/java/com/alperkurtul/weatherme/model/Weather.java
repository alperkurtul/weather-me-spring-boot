package com.alperkurtul.weatherme.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.sql.Timestamp;

@Entity
public class Weather {

    @EmbeddedId
    //@Column(name="WeatherId")
    private WeatherId weatherId;
    //@Column(name="WeatherJson")
    private String weatherJson;
    //@Column(name="CreateTime")
    //private Timestamp createTime;

    public WeatherId getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(WeatherId weatherId) {
        this.weatherId = weatherId;
    }

    public String getWeatherJson() {
        return weatherJson;
    }

    public void setWeatherJson(String weatherJson) {
        this.weatherJson = weatherJson;
    }

/*    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }*/
}
