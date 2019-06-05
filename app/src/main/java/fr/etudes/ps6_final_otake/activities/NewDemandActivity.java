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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

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
    private String url = "https://api.otakedev.com/";
    private String targetSupervisor;
    private JSONObject jsonBody = new JSONObject();
    private FormFragment ticketFormFragment;
    private Integer nbTicket = 1;

    private View.OnClickListener addDemandBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            addForm();
        }
    };


    private View.OnClickListener confirmBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            for(Integer i = 1; i < nbTicket; i++){
                ticketFormFragment = (FormFragment) getSupportFragmentManager().findFragmentByTag(i.toString());
                View view = (View)ticketFormFragment.getView();
                Spinner spinner = (Spinner) view.findViewById(R.id.officeSpinner);
                //TODO find solution for instanceof
                if (spinner.getSelectedItem() instanceof CustomSpinnerOfficeItem){
                    targetSupervisor = ((CustomSpinnerOfficeItem) spinner.getSelectedItem()).getOffice();
                }
                Log.d("Selected String >>>>>>>>> ", targetSupervisor);
                try {
                    jsonBody.put("supervisor_id", getCorrespondingSupervisor(targetSupervisor));
                    //TODO @Maxime Change student_id value !
                    String jsonString;
                    File file = new File(NewDemandActivity.this.getFilesDir()+"/login.json");

                    InputStream inputStream = new FileInputStream(file);
                    StringBuilder stringBuilder = new StringBuilder();

                    if (inputStream != null) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString = "";

                        while ((receiveString = bufferedReader.readLine()) != null){
                            stringBuilder.append(receiveString);
                        }
                        inputStream.close();
                        jsonString = stringBuilder.toString();

                        Log.d("Message", jsonString);

                        JSONObject json = new JSONObject(jsonString);

                        String res = json.getString("id");
                        Log.d("résultat", "onClick: "+res);

                        jsonBody.put("student_id", "res");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "queue/tickets", jsonBody, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("It worked ",response.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("It didn't worked ",error.toString());
                    }
                });
                Volley.newRequestQueue(NewDemandActivity.this).add(jsonObjectRequest);
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

        //Confirm form
        confirmBtn = findViewById(R.id.confirmDemandBtn);
        confirmBtn.setOnClickListener(confirmBtnListener);

        addDemandBtn = findViewById(R.id.addDemandBtn);
        addDemandBtn.setOnClickListener(addDemandBtnListener);

        findViewById(R.id.formButtonView).bringToFront();

        addForm();

//        ticketFormFragment = (FormFragment) getSupportFragmentManager().findFragmentByTag("1");
    }

    private void addForm(){
        getSupportFragmentManager().beginTransaction().add(R.id.formContainerLinear, new FormFragment(), nbTicket.toString()).commit();
        getSupportFragmentManager().executePendingTransactions();
        nbTicket++;
    }

    private int getCorrespondingSupervisor(String str) {
        int supervisor_id = -1;

        //TODO replace by http request
        switch (str){
            case "RI GE - M.Santisi": supervisor_id = 0;break;
            case "RI ELEC - M. Bilavran": supervisor_id = 1; break;
            case "RI GB - Mme. Cupo": supervisor_id = 2; break;
            case "RI GB - M. Macia": supervisor_id = 3; break;
            case "RI GE - M. Brigode": supervisor_id = 4; break;
            case "RI MAM - M. Habbal": supervisor_id = 5; break;
            case "RI SI - Mme. Pinna": supervisor_id = 6; break;
            case "BRI - Mme. Maiffret": supervisor_id = 7; break;
            case "BRI - Mme. winchcombe": supervisor_id = 8; break;
        }

        return supervisor_id;
    }

}
