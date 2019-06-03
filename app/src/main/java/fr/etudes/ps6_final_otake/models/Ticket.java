package fr.etudes.ps6_final_otake.models;

public class Ticket {
    private int rank;
    private String object;
    private String office;
    private String supervisor;
    private int waitingTime;

    public Ticket(int rank, String object, String office, String supervisor, int waitingTime){
        this.rank = rank;
        this.object = object;
        this.office = office;
        this.supervisor = supervisor;
        this.waitingTime = waitingTime;
    }

    public int getRank(){
        return this.rank;
    }

    public String getObject(){
        return this.object;
    }

    public String getOffice(){
        return this.office;
    }

    public String getSupervisor(){ return this.supervisor; }

    public int getWaitingTime(){ return this.waitingTime; }

    public void setRank(int rank){
        this.rank = rank;
    }

    public void setObject(String object){
        this.object = object;
    }

    public void setOffice(String office){
        this.office = office;
    }

    public void setSupervisor(String supervisor){ this.supervisor = supervisor; }

    public void setWaitingTime(int waitingTime){ this.waitingTime = waitingTime; }
}
