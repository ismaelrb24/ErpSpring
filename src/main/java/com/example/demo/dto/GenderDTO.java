package com.example.demo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenderDTO {
    @JsonProperty("name")
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
