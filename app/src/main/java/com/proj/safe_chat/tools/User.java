package com.proj.safe_chat.tools;

public class User {
    private String name;
    private String unique_id;
    private String email;
    private String token;

    public User(String name, String unique_id, String email, String token) {
        this.name = name;
        this.unique_id = unique_id;
        this.email = email;
        this.token = token;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}