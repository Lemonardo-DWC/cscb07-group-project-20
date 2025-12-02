package com.example.smartair;

public class Child {
    private String uid;
    private BasicInformationProvider basicInformation;

    public Child() {
        // Required for Firebase
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public BasicInformationProvider getBasicInformation() {
        return basicInformation;
    }

    public void setBasicInformation(BasicInformationProvider basicInformation) {
        this.basicInformation = basicInformation;
    }
}


