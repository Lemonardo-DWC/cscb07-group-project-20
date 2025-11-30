package com.example.smartair;

public class Child {
    private String uid;
    private BasicInformation basicInformation;

    public Child() {
        // Required for Firebase
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public BasicInformation getBasicInformation() {
        return basicInformation;
    }

    public void setBasicInformation(BasicInformation basicInformation) {
        this.basicInformation = basicInformation;
    }
}


