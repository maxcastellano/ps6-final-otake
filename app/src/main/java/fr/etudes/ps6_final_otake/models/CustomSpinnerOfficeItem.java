package fr.etudes.ps6_final_otake.models;

public class CustomSpinnerOfficeItem {

    private String office;
    private Boolean availability;
    private String waitingDelay;
    private String rank;

    public CustomSpinnerOfficeItem(String office, Boolean availability, String waitingDelay, String rank){
        this.office = office;
        this.availability = availability;
        this.waitingDelay = waitingDelay;
        this.rank = rank;
    }

    public String getOffice() {
        return office;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public String getWaitingDelay() {
        return waitingDelay;
    }

    public String getRank() {
        return rank;
    }
}
