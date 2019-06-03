package fr.etudes.ps6_final_otake.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import fr.etudes.ps6_final_otake.R;
import fr.etudes.ps6_final_otake.adapters.TicketAdapter;
import fr.etudes.ps6_final_otake.mocks.TicketMock;
import fr.etudes.ps6_final_otake.models.Ticket;

public class MainActivity extends AppCompatActivity {

    private ListView listTicket;
    private Button addTicket;
    private TicketMock ticketMock;
    private ArrayList<Ticket> tickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        ticketMock = new TicketMock();

        listTicket = (ListView)findViewById(R.id.ticket_list);
        LayoutInflater inflater = getLayoutInflater();
        tickets = ticketMock.getTickets();
        TicketAdapter adapter = new TicketAdapter(inflater, tickets);
        listTicket.setAdapter(adapter);
    }
}
