package fr.etudes.ps6_final_otake.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.ArrayList;

import fr.etudes.ps6_final_otake.R;
import fr.etudes.ps6_final_otake.models.Ticket;

public class TicketAdapter extends BaseAdapter {
    private ArrayList<Ticket> mData;
    private LayoutInflater mInflater;
    private String url = "https://nodered.otakedev.com/";
    private JSONObject jsonBody = new JSONObject();

    private static final String TAG = "MQTT";
    MqttAndroidClient mqttAndroidClient;
    final String serverUri = "tcp://broker.otakedev.com:8080";
    String clientId = "ExampleAndroidClient";
    final String subscriptionTopic = "ticket/delete";

    private Button delete;
    private TextView rang_title;
    private TextView rang;
    private TextView supervisor;
    private TextView info;
    private Context context;

    public TicketAdapter(LayoutInflater inflater, ArrayList<Ticket> data) {
        this.mInflater = inflater;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup viewGroup){
        View v = mInflater.inflate(R.layout.card_ticket,null);
        Ticket ticket = mData.get(position);

        delete =(Button) v.findViewById(R.id.button_delete);
        delete.setTag(position);
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                AlertDialog.Builder altdial = new AlertDialog.Builder(context);
                altdial.setTitle("Supprimer");
                altdial.setMessage("Etes vous sûr de ne plus vouloir participer à cet entretien ?").setCancelable(false)
                        .setNegativeButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                MqttMessage message = new MqttMessage(String.valueOf(mData.get(position).getTicketId()).getBytes());
                                try {
                                    mqttAndroidClient.publish(subscriptionTopic,message);
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .setPositiveButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alert = altdial.create();
                alert.show();
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(0,150,136));
                alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED);
            }

        });

        rang_title = (TextView)v.findViewById(R.id.ticket_rang_title);
        rang_title.setTag(position);
        rang_title.setText("Rang");
        rang_title.setTextSize(30);

        rang = (TextView)v.findViewById(R.id.ticket_rang);
        rang.setTag(position);
        rang.setText(Integer.toString(ticket.getRank()));
        rang.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        rang.setTextSize(70);

        supervisor = (TextView)v.findViewById(R.id.ticket_supervisor);
        supervisor.setTag(position);
        supervisor.setText("Ticket - " + ticket.getSupervisor());
        supervisor.setTextSize(22);

        info = (TextView)v.findViewById(R.id.ticket_info);
        info.setTag(position);
        info.setText(ticket.getObject() + "\n" + "Attente estimé: " + ticket.getWaitingTime() + "min\n" + "Salle: " + ticket.getOffice());
        info.setTextSize(15);

        return v;
    }

    public void setContext(Context context){
        this.context = context;
        creatMqtt();
    }

    public void creatMqtt(){
        clientId = clientId + System.currentTimeMillis();

        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId);

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
                }
            });

        } catch (MqttException ex) {
            Log.d(TAG, "subscribeToTopic: Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

}
