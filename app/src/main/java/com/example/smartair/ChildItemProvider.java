package com.example.smartair;

public class ChildItemProvider {

    private String firstName;
    private String middleName;
    private String lastName;
    private String dob;
    private String sex;
    private int weeklyRescueCt;
    private String lastRescue;

    public ChildItemProvider() {
        firstName = "Child";
        lastName = String.valueOf(((Double) Math.random()).hashCode());
    }

    public ChildItemProvider(String firstName, String middleName, String lastName, String dob, String sex) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.dob = dob;
        this.sex = sex;
        weeklyRescueCt = 0;
        lastRescue = "dummyString";
    }

    // getters and setters
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public int getWeeklyRescueCt() {
        return weeklyRescueCt;
    }

    public void setWeeklyRescueCt(int weeklyRescueCt) {
        this.weeklyRescueCt = weeklyRescueCt;
    }

    public String getLastRescue() {
        return lastRescue;
    }

    public void setLastRescue(String lastRescue) {
        this.lastRescue = lastRescue;
    }
}
