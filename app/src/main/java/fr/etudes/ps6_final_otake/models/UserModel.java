package fr.etudes.ps6_final_otake.models;

public class UserModel {

    private String firstName;
    private String lastName;
    private String major;


    public UserModel(String firstName, String lastName, String major) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMajor() {
        return major;
    }
}
