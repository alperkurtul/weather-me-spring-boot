package com.alperkurtul.weatherme.model;

import java.util.List;

public class LocationDto {

    private String searchedKeyword;
    private List<LocationModel> locationModelList;

    public String getSearchedKeyword() {
        return searchedKeyword;
    }

    public void setSearchedKeyword(String searchedKeyword) {
        this.searchedKeyword = searchedKeyword;
    }

    public List<LocationModel> getLocationModelList() {
        return locationModelList;
    }

    public void setLocationModelList(List<LocationModel> locationModelList) {
        this.locationModelList = locationModelList;
    }

}
