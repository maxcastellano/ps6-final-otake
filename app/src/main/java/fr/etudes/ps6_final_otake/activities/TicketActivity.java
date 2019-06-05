package fr.etudes.ps6_final_otake.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
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
    private String url = "https://nodered.otakedev.com/";
    private JSONArray jsonBody = new JSONArray();
    private ArrayList<Ticket> tickets = new ArrayList<>();

    private static final String TAG = "MQTT";
    MqttAndroidClient mqttAndroidClient;
    final String serverUri = "tcp://broker.otakedev.com:8080";
    String clientId = "ExampleAndroidClient";
    final String subscriptionTopic = "test";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);
        clientId = clientId + System.currentTimeMillis();
        listTicket = (ListView)findViewById(R.id.ticket_list);
        final LayoutInflater inflater = getLayoutInflater();

        updateTicketList();

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    Log.d(TAG, "Reconnected to : " + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
                    subscribeToTopic();
                } else {
                    Log.d(TAG, "Connected to : " + serverURI);
                }
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.d(TAG, "connectionLost: " + cause);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d(TAG, "messageArrived: " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(false);

        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                    disconnectedBufferOptions.setBufferEnabled(true);
                    disconnectedBufferOptions.setBufferSize(100);
                    disconnectedBufferOptions.setPersistBuffer(false);
                    disconnectedBufferOptions.setDeleteOldestMessages(false);
                    mqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    subscribeToTopic();
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "onFailure: " + serverUri);
                }
            });
        } catch (MqttException ex) {
            ex.printStackTrace();
        }

    }

    private void updateTicketList(){
        tickets.clear();
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

    }

    public void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(subscriptionTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "onSuccess: Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "onFailure: Failed to subscribe");
                }
            });

            // THIS DOES NOT WORK!
            mqttAndroidClient.subscribe(subscriptionTopic, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // message Arrived!
                    Log.d(TAG, "messageArrived: " + topic + " : " + new String(message.getPayload()));
                    updateTicketList();
                }
            });

        } catch (MqttException ex) {
            Log.d(TAG, "subscribeToTopic: Exception whilst subscribing");
            ex.printStackTrace();
        }


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
