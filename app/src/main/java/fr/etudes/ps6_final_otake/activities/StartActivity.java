package fr.etudes.ps6_final_otake.activities;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.apache.log4j.BasicConfigurator;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Black Magic le retour
        //START BROKER SERVICE
        BasicConfigurator.configure();

        io.moquette.server.Server server = new io.moquette.server.Server();

        String[] permissions = {"android.permission.WRITE_EXTERNAL_STORAGE"};

        int perms = 200;

        ActivityCompat.requestPermissions( this ,permissions, perms);

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

        File myFile = new File(this.getFilesDir() + "/login.json");

        try {
            InputStream inputStream = new FileInputStream(myFile);

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
