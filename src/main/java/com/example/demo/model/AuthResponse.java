package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthResponse {

    private String message;

    @JsonProperty("home_page")
    private String homePage;

    @JsonProperty("full_name")
    private String fullName;

    @JsonProperty("access_token")
    private String accessToken;

    // Champ ajouté manuellement (pas via JSON)
    private String sid;

    // Getters et setters

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
