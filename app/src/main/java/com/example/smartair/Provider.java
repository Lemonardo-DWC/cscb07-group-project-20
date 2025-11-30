package com.example.smartair;

public class Provider {

    private String uid;
    private String name;
    private String email;

    public Provider() {}

    public Provider(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
    }

    // getter
    public String getUid() { return uid; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    // setter
    public void setUid(String uid) { this.uid = uid; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
}