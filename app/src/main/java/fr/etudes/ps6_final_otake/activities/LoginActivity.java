package fr.etudes.ps6_final_otake.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.etudes.ps6_final_otake.R;
import fr.etudes.ps6_final_otake.adapters.TicketAdapter;
import fr.etudes.ps6_final_otake.models.MajorModel;
import fr.etudes.ps6_final_otake.models.Ticket;
import fr.etudes.ps6_final_otake.models.UserModel;

public class LoginActivity extends AppCompatActivity {
    private Spinner spinner;

    private EditText lastNameInput;
    private EditText firstNameInput;

    private boolean firstNameHasBeenSelected = false;
    private boolean lastNameHasBeenSelected = false;

    private Button confirmButton;

    private RequestQueue queue;

    private String filesDir;
    private ArrayList<MajorModel> majorList = new ArrayList<>();

    private static final String TAG = "MQTT";
    MqttAndroidClient mqttAndroidClient;

    final String serverUri = "tcp://broker.otakedev.com:8080";

    String clientId = "ExampleAndroidClient";
    final String topicMajor = "major/get";
    final String topicStudent = "student/post";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lastNameInput = (EditText) findViewById(R.id.editTextLastName);
        firstNameInput = (EditText) findViewById(R.id.editTextFirstName);

        confirmButton = (Button) findViewById(R.id.confirmButton);

        confirmButton.setEnabled(false);

        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

                if (reconnect) {
                    Log.d(TAG, "Reconnected to : " + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
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
                subscribeToTopic();
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

        lastNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastNameHasBeenSelected = true;
                if (lastNameHasBeenSelected && firstNameHasBeenSelected) {
                    confirmButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        firstNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                firstNameHasBeenSelected = true;
                if (lastNameHasBeenSelected && firstNameHasBeenSelected) {
                    confirmButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        filesDir = this.getFilesDir() + "/login.json";

        spinner = (Spinner) findViewById(R.id.spinnerCurriculum);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MajorModel major = (MajorModel) spinner.getSelectedItem();

                Log.d("debug", "onClick: " + major.getId());

                UserModel user = new UserModel(firstNameInput.getText().toString(), lastNameInput.getText().toString(),
                        major.getId());
                try {

                    writeInternalStorage(user);
                } catch (IOException e) {
                    Log.d("erreur", "erreur écriture");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Intent demandActivity = new Intent(LoginActivity.this, NewDemandActivity.class);
                startActivity(demandActivity);
            }
        });
    }

    private void writeInternalStorage(UserModel userModel) throws IOException, JSONException {
        Gson gson = new Gson();
        final String json = gson.toJson(userModel);
        Log.d("Debug", "writeInternalStorage: " + json);

        JSONObject jsonBody = new JSONObject(json);

        MqttMessage message = new MqttMessage(jsonBody.toString().getBytes());
        try {
            mqttAndroidClient.publish(topicStudent, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        File myFile = new File(filesDir);

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(myFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            final FileOutputStream finalFileOutputStream = fileOutputStream;
            mqttAndroidClient.subscribe(topicMajor, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    finalFileOutputStream.write(message.toString().getBytes());
                    Log.d("Debug", "onResponse: on a écrit");
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }

        try {
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void subscribeToTopic() {
        try {
            mqttAndroidClient.subscribe(topicMajor, 0, null, new IMqttActionListener() {
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
            mqttAndroidClient.subscribe(topicMajor, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // message Arrived!
                    Log.d(TAG, "messageArrived: " + topic + " : " + new String(message.getPayload()));
                    setListMajor(new String(message.getPayload()));
                }
            });

        } catch (MqttException ex) {
            Log.d(TAG, "subscribeToTopic: Exception whilst subscribing");
            ex.printStackTrace();
        }
    }

    public void setListMajor(String str) throws JSONException {
        this.majorList.clear();
        JSONArray majorList = new JSONArray(str);
        System.out.println("Array" + majorList.toString());
        for (int i = 0; i < majorList.length(); i++) {
            try {
                JSONObject jsonObject = (JSONObject) majorList.get(i);
                this.majorList.add(new MajorModel(jsonObject.getInt("id"), jsonObject.getString("title")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        final ArrayAdapter<CharSequence> adapter = new ArrayAdapter(LoginActivity.this,
                android.R.layout.simple_spinner_item, this.majorList);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                spinner.setAdapter(adapter);
            }
        });
    }

}
