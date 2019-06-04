package fr.etudes.ps6_final_otake.models;

public class UserModel {

    private String firstName;
    private String lastName;
    private int major_id;


    public UserModel(String firstName, String lastName, int major_id) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.major_id = major_id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getMajor() {
        return major_id;
    }
}
