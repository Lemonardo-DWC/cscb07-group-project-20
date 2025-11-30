package com.example.smartair;

public class BasicInformation {
    private String firstName;
    private String lastName;

    public BasicInformation() {
        // Required for Firebase
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
