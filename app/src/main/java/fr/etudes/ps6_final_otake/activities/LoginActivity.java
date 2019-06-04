package fr.etudes.ps6_final_otake.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import fr.etudes.ps6_final_otake.R;
import fr.etudes.ps6_final_otake.models.UserModel;

public class LoginActivity extends AppCompatActivity {
    Spinner spinner;

    EditText lastNameInput;
    EditText firstNameInput;

    Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        confirmButton = (Button) findViewById(R.id.confirmButton);

        spinner = (Spinner) findViewById(R.id.spinnerCurriculum);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.curriculum_array, android.R.layout.simple_spinner_item);
        spinner.setAdapter(adapter);

        lastNameInput = (EditText) findViewById(R.id.editTextLastName);
        firstNameInput = (EditText) findViewById(R.id.editTextFirstName);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModel user = new UserModel(firstNameInput.getText().toString(),
                                                lastNameInput.getText().toString(),
                                                spinner.getSelectedItem().toString());

                try {
                    writeInternalStorage(user);
                } catch (IOException e) {
                    Log.d("erreur", "erreur écriture");
                }

                readInternalStorage();

                Intent demandActivity = new Intent(LoginActivity.this, NewDemand.class);
                startActivity(demandActivity);
            }
        });
    }

    private void writeInternalStorage(Object obj) throws IOException {
        Gson gson = new Gson();
        String json = gson.toJson(obj);

        String myFilePath = this.getFilesDir() + "/login.json";
        File myFile = new File(myFilePath);

        FileOutputStream fileOutputStream = new FileOutputStream(myFile);
        fileOutputStream.write(json.getBytes());

        fileOutputStream.flush();
        fileOutputStream.close();
    }

    private void readInternalStorage() {
        Gson gson = new Gson();
        String text = "";

        try {
            String myFilePath = this.getFilesDir() + "/login.json";
            File myFile = new File(myFilePath);

            InputStream inputStream = new FileInputStream(myFile);
            StringBuilder stringBuilder = new StringBuilder();

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";

                while ((receiveString = bufferedReader.readLine()) != null){
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                text = stringBuilder.toString();

                Log.d("Message", text);
            }
        } catch (FileNotFoundException e) {
            Log.d("Debug", "readInternalStorage: erreur fichier introuvable");
        } catch (IOException e) {
            Log.d("Debug", "readInternalStorage: erreur lecture");
        }
    }
}
