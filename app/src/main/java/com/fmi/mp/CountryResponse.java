package com.fmi.mp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryResponse {
    @JsonProperty("data")
    private List<CountryContainer> data = null;
    public CountryResponse() {}
    public CountryResponse(List<CountryContainer> data) {
        this.data = data;
    }

    public List<CountryContainer> getData() {
        return data;
    }

    public void setData(List<CountryContainer> data) {
        this.data = data;
    }
}
