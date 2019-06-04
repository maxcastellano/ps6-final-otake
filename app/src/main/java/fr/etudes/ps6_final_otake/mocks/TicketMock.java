package fr.etudes.ps6_final_otake.mocks;

import java.util.ArrayList;

import fr.etudes.ps6_final_otake.models.Ticket;

public class TicketMock {

    private ArrayList<Ticket> tickets = new ArrayList<>();

    public TicketMock(){
        initialization();
    }

    private void initialization(){
        tickets.add(new Ticket(1,"Question sur l'international","E+241","BRI",30,"1",1));
        tickets.add(new Ticket(1,"Question sur l'international","E+235","M.Litovsky",15,"1",2));
        tickets.add(new Ticket(2,"Question sur l'international","E+241","BRI",40,"2",3));
        tickets.add(new Ticket(2,"Signature","E+235","M.Litovsky",20,"2",4));
    }

    public ArrayList<Ticket> getTickets() {
        return this.tickets;
    }

    public void deleteTicket(int ticketNumber){
        this.tickets.remove(ticketNumber);
    }
}
