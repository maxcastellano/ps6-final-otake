package fr.etudes.ps6_final_otake.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import fr.etudes.ps6_final_otake.R;
import fr.etudes.ps6_final_otake.models.MajorModel;
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

    private final String url = "https://api.otakedev.com/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        lastNameInput = (EditText) findViewById(R.id.editTextLastName);
        firstNameInput = (EditText) findViewById(R.id.editTextFirstName);

        confirmButton = (Button) findViewById(R.id.confirmButton);

        confirmButton.setEnabled(false);

        lastNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                lastNameHasBeenSelected = true;
                if(lastNameHasBeenSelected && firstNameHasBeenSelected) {
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
                if(lastNameHasBeenSelected && firstNameHasBeenSelected) {
                    confirmButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        filesDir = this.getFilesDir() + "/login.json";

        final ArrayList<MajorModel> majorList = new ArrayList<>();

        spinner = (Spinner) findViewById(R.id.spinnerCurriculum);

        queue = Volley.newRequestQueue(this);

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

                                ArrayAdapter<CharSequence> adapter = new ArrayAdapter(LoginActivity.this,
                                        android.R.layout.simple_spinner_item,majorList);
                                spinner.setAdapter(adapter);
                            } catch (JSONException e) {
                                Log.d("Erreur", "onResponse: problème conversion");
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

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MajorModel major = (MajorModel)spinner.getSelectedItem();

                Log.d("debug", "onClick: "+major.getId());

                UserModel user = new UserModel(firstNameInput.getText().toString(),
                                                lastNameInput.getText().toString(),
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
        Log.d("Debug", "writeInternalStorage: "+json);

        JSONObject jsonBody = new JSONObject(json);

        JsonObjectRequest postUserRequest = new JsonObjectRequest(Request.Method.POST, url + "queue/student", jsonBody, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.d("It worked ",response.toString());
                File myFile = new File(filesDir);

                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = new FileOutputStream(myFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fileOutputStream.write(response.toString().getBytes());
                } catch (IOException e) {
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
                try {
                    fileOutputStream = new FileOutputStream(myFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    fileOutputStream.write(json.getBytes());
                } catch (IOException e) {
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("It didn't worked ",error.toString());
            }
        });
        queue.add(postUserRequest);

    }

}
