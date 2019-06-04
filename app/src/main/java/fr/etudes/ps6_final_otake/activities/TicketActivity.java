package fr.etudes.ps6_final_otake.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.etudes.ps6_final_otake.R;
import fr.etudes.ps6_final_otake.adapters.TicketAdapter;
import fr.etudes.ps6_final_otake.models.Ticket;

public class TicketActivity extends AppCompatActivity {
    private ListView listTicket;
    private Button addTicket;
    private String url = "https://api.otakedev.com/";
    private JSONArray jsonBody = new JSONArray();
    private ArrayList<Ticket> tickets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        listTicket = (ListView)findViewById(R.id.ticket_list);
        final LayoutInflater inflater = getLayoutInflater();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url + "queue/tickets",jsonBody,new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONArray ticketArrayList = response;
                for(int i = 0; i < ticketArrayList.length(); i++){
                    try {
                        JSONObject jsonObject = (JSONObject)ticketArrayList.get(i);
                        Ticket ticket = new Ticket(jsonObject.getInt("rank"),jsonObject.getString("major"),jsonObject.getString("room"),jsonObject.getString("supervisor"),jsonObject.getInt("rank")*10,jsonObject.getString("student_id"),jsonObject.getInt("id"));
                        tickets.add(ticket);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                TicketAdapter adapter = new TicketAdapter(inflater, tickets);
                adapter.setContext(TicketActivity.this);
                listTicket.setAdapter(adapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(TicketActivity.this).add(request);

        addTicket = findViewById(R.id.button_add_ticket);
        addTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent demandActivity = new Intent(TicketActivity.this, NewDemand.class);
                startActivity(demandActivity);
            }
        });
    }

}
