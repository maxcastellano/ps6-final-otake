package fr.etudes.ps6_final_otake.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import fr.etudes.ps6_final_otake.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        File myFile = new File(this.getFilesDir() + "/login.json");

        try {
            InputStream inputStream = new FileInputStream(myFile);

            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
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
