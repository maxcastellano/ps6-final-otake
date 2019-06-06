package fr.etudes.ps6_final_otake.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.etudes.ps6_final_otake.R;
import fr.etudes.ps6_final_otake.fragments.FormFragment;
import fr.etudes.ps6_final_otake.models.CustomSpinnerOfficeItem;

public class NewDemandActivity extends AppCompatActivity {

    private Button confirmBtn;
    private Button addDemandBtn;
    private static final String TAG = "MQTT";
    MqttAndroidClient mqttAndroidClient;

    final String serverUri = "tcp://broker.otakedev.com:8080";
    String clientId = "ExampleAndroidClient";

    final String subscriptionTopic = "ticket/post";
    private String targetSupervisor;
    private JSONObject jsonBody = new JSONObject();
    private FormFragment ticketFormFragment;
    private Integer nbTicket = 1;
    public static String STUDENT_ID;

    private View.OnClickListener addDemandBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            addForm();
        }
    };

    private View.OnClickListener confirmBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            JSONArray jsonArray = new JSONArray();
            for (Integer i = 1; i < nbTicket; i++) {
                ticketFormFragment = (FormFragment) getSupportFragmentManager().findFragmentByTag(i.toString());
                View view = (View) ticketFormFragment.getView();
                Spinner spinner = (Spinner) view.findViewById(R.id.officeSpinner);
                // TODO find solution for instanceof
                if (spinner.getSelectedItem() instanceof CustomSpinnerOfficeItem) {
                    targetSupervisor = ((CustomSpinnerOfficeItem) spinner.getSelectedItem()).getOffice();
                }
                Log.d("Selected String >>>>>>>>> ", targetSupervisor);
                try {
                    jsonBody.put("supervisor_id", getCorrespondingSupervisor(targetSupervisor));
                    // TODO @Maxime Change student_id value !
                    String jsonString;
                    File file = new File(NewDemandActivity.this.getFilesDir() + "/login.json");

                    InputStream inputStream = new FileInputStream(file);
                    StringBuilder stringBuilder = new StringBuilder();

                    if (inputStream != null) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString = "";

                        while ((receiveString = bufferedReader.readLine()) != null) {
                            stringBuilder.append(receiveString);
                        }
                        inputStream.close();
                        jsonString = stringBuilder.toString();

                        Log.d("Message", jsonString);

                        JSONObject json = new JSONObject(jsonString);

                        String res = json.getString("id");
                        Log.d("résultat", "onClick: " + res);

                        jsonBody.put("student_id", res);
                        System.out.println(jsonBody.toString() + "1111");
                        jsonArray.put(jsonBody);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            Log.d("body", jsonArray.toString());

            MqttMessage message = new MqttMessage(jsonArray.toString().getBytes());
            try {
                mqttAndroidClient.publish(subscriptionTopic, message);
            } catch (MqttException e) {
                e.printStackTrace();
            }

            Intent demandActivity = new Intent(NewDemandActivity.this, TicketActivity.class);
            startActivity(demandActivity);
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_demand);
        setTitle(R.string.new_demand_title);

        // Confirm form
        confirmBtn = findViewById(R.id.confirmDemandBtn);
        confirmBtn.setOnClickListener(confirmBtnListener);

        addDemandBtn = findViewById(R.id.addDemandBtn);
        addDemandBtn.setOnClickListener(addDemandBtnListener);

        findViewById(R.id.formButtonView).bringToFront();

        addForm();

        // ticketFormFragment = (FormFragment)
        // getSupportFragmentManager().findFragmentByTag("1");

        clientId = clientId + System.currentTimeMillis();

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);

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

    private void addForm() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.formContainerLinear, new FormFragment(), nbTicket.toString()).commit();
        getSupportFragmentManager().executePendingTransactions();
        nbTicket++;
    }

    private int getCorrespondingSupervisor(String str) {
        int supervisor_id = -1;

        // TODO replace by http request
        switch (str) {
        case "RI GE - M.Santisi":
            supervisor_id = 0;
            break;
        case "RI ELEC - M. Bilavran":
            supervisor_id = 1;
            break;
        case "RI GB - Mme. Cupo":
            supervisor_id = 2;
            break;
        case "RI GB - M. Macia":
            supervisor_id = 3;
            break;
        case "RI GE - M. Brigode":
            supervisor_id = 4;
            break;
        case "RI MAM - M. Habbal":
            supervisor_id = 5;
            break;
        case "RI SI - Mme. Pinna":
            supervisor_id = 6;
            break;
        case "BRI - Mme. Maiffret":
            supervisor_id = 7;
            break;
        case "BRI - Mme. winchcombe":
            supervisor_id = 8;
            break;
        }

        return supervisor_id;
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

    private void publishMsgToServer(String msg) {
        try {
            mqttAndroidClient.publish(subscriptionTopic, msg.getBytes(), 0, true, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
