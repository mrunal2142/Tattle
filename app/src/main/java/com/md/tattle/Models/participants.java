package com.md.tattle.Models;

public class participants {

    String name , id , role = "participant";

    public participants() {
    }

    public participants(String name, String id, String role) {
        this.name = name;
        this.id = id;
        this.role = role;
    }

    public participants(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
