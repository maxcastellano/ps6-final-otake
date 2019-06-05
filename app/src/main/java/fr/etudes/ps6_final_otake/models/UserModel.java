package fr.etudes.ps6_final_otake.models;

public class UserModel {

    private String first_name;
    private String last_name;
    private int major_id;


    public UserModel(String first_name, String last_name, int major_id) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.major_id = major_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public int getMajor() {
        return major_id;
    }
}
