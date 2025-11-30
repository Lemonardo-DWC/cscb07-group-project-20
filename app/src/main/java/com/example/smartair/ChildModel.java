package com.example.smartair;

public class ChildModel {

    private String childId;
    private String firstName;
    private String lastName;

    public ChildModel() {}

    public ChildModel(String childId, String firstName, String lastName) {
        this.childId = childId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getChildId() {
        return childId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
