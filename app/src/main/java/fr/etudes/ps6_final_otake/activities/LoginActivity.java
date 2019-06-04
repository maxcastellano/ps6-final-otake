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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import fr.etudes.ps6_final_otake.R;
import fr.etudes.ps6_final_otake.models.MajorModel;
import fr.etudes.ps6_final_otake.models.UserModel;

public class LoginActivity extends AppCompatActivity {
    Spinner spinner;

    EditText lastNameInput;
    EditText firstNameInput;

    Button confirmButton;

    private final String url = "https://api.otakedev.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final ArrayList<MajorModel> majorList = new ArrayList<>();

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET,
                url + "universities/majors", null,
                new Response.Listener<JSONArray>()
                {
                    @Override
                    public void onResponse(JSONArray response) {
                        // display response
                        Log.d("Response", response.toString());

                        for(int i = 0; i < response.length(); i++){
                            JSONObject item = null;
                            try {
                                item = response.getJSONObject(i);
                            } catch (JSONException e) {
                                Log.d("Erreur", "onResponse: erreur getJSONObject");
                            }
                            try {
                                majorList.add(new MajorModel(item.getInt("id"),
                                        item.getString("title")));
                            } catch (JSONException e) {
                                Log.d("Erreur", "onResponse: problème conversion");;
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

// add it to the RequestQueue
        queue.add(getRequest);

        confirmButton = (Button) findViewById(R.id.confirmButton);

        spinner = (Spinner) findViewById(R.id.spinnerCurriculum);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(this,
                android.R.layout.simple_spinner_item,majorList);
        spinner.setAdapter(adapter);

        lastNameInput = (EditText) findViewById(R.id.editTextLastName);
        firstNameInput = (EditText) findViewById(R.id.editTextFirstName);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MajorModel major = (MajorModel)spinner.getSelectedItem();

                UserModel user = new UserModel(firstNameInput.getText().toString(),
                                                lastNameInput.getText().toString(),
                                                1);
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
