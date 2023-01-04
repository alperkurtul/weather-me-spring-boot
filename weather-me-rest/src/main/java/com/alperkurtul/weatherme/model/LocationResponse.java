package com.alperkurtul.weatherme.model;

import java.util.List;

public class LocationResponse {

    private String searchedKeyword;
    private List<LocationResp> locationRespList;

    public String getSearchedKeyword() {
        return searchedKeyword;
    }

    public void setSearchedKeyword(String searchedKeyword) {
        this.searchedKeyword = searchedKeyword;
    }

    public List<LocationResp> getLocationRespList() {
        return locationRespList;
    }

    public void setLocationRespList(List<LocationResp> locationRespList) {
        this.locationRespList = locationRespList;
    }

}
