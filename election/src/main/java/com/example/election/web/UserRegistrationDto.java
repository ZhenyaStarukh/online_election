package com.example.election.web;

import javax.validation.constraints.NotEmpty;

public class UserRegistrationDto {

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    private Boolean admin;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }
}
