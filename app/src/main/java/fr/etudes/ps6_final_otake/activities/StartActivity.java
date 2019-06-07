package fr.etudes.ps6_final_otake.activities;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.apache.log4j.BasicConfigurator;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import fr.etudes.ps6_final_otake.R;
import io.moquette.BrokerConstants;
import io.moquette.server.config.MemoryConfig;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = "MQTT";
    MqttAndroidClient mqttAndroidClient;
    final String serverUri = "tcp://localhost:8080";
    String clientId = "ExampleAndroidClient";
    final String establishedTopic = "established";
    final String studentIdPostTopic = "student/post";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //1. Run local broker
        //Black Magic le retour
        //START BROKER SERVICE
        BasicConfigurator.configure();

        io.moquette.server.Server server = new io.moquette.server.Server();

        String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};

        int perms = 200;

        if (PermissionChecker.checkSelfPermission(getApplicationContext(), permissions[0]) == PermissionChecker.PERMISSION_GRANTED) {
            MemoryConfig memoryConfig = new MemoryConfig(new Properties());
            memoryConfig.setProperty(BrokerConstants.PERSISTENT_STORE_PROPERTY_NAME,
                    Environment.getExternalStorageDirectory().getAbsolutePath()+
                            File.separator + BrokerConstants.DEFAULT_MOQUETTE_STORE_MAP_DB_FILENAME);

            try {
                Log.d("START SERVICE"," BROKER");
                server.startServer(memoryConfig);
            } catch (IOException e) {
                Log.d("BROKER ", "ERROR");
                e.printStackTrace();
            }
        }
        else
            ActivityCompat.requestPermissions(this, permissions, perms);
        // BROKER SERVICE END

        //2. Subscribe to topic established
        subscribeToEstablished();

    }


    private void subscribeToEstablished(){
        mqttAndroidClient = new MqttAndroidClient(getApplicationContext(), serverUri, clientId);
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
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

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                if (reconnect) {
                    Log.d(TAG, "Reconnected to : " + serverURI);
                    // Because Clean Session is true, we need to re-subscribe
                } else {
                    Log.d(TAG, "Connected to : " + serverURI);
                }
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
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribeToTopic(){
        try {
            mqttAndroidClient.subscribe(establishedTopic, 0, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d(TAG, "onSuccess: Subscribed!");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d(TAG, "onFailure: Failed to subscribe");
                }
            });

            mqttAndroidClient.subscribe(establishedTopic, 0, new IMqttMessageListener() {
                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    // message Arrived!
                    Log.d(TAG, "messageArrived: " + topic + " : " + new String(message.getPayload()));
                    if("true".equals(new String(message.getPayload()))){
                        switchActivity();
                    }
                }
            });

        } catch (MqttException e) {
            Log.d(TAG, "subscribeToTopic: Exception whilst subscribing");
            e.printStackTrace();
        }
    }


    private void switchActivity() {
        File myFile = new File(this.getFilesDir() + "/login.json");

        try {
            InputStream inputStream = new FileInputStream(myFile); //Do not remove

            Intent intent = new Intent(StartActivity.this, NewDemandActivity.class);
            startActivity(intent);
        }
        catch (FileNotFoundException e) {
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
