package com.project.back_end.DTO;

public class Login {

    // Unique identifier (email or username)
    private String identifier;

    // Password entered by the user
    private String password;


    // ✅ Default constructor (needed for @RequestBody)
    public Login() {
    }


    // ✅ Getters

    public String getIdentifier() {
        return identifier;
    }

    public String getPassword() {
        return password;
    }


    // ✅ Setters

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}