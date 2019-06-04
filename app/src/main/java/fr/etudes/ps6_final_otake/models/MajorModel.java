package fr.etudes.ps6_final_otake.models;

public class MajorModel {

    private int id;
    private String title;

    public MajorModel(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String toString() {
        return title;
    }
}
