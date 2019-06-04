package fr.etudes.ps6_final_otake.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import fr.etudes.ps6_final_otake.R;

public class NewDemand extends AppCompatActivity {

    private Button confirmBtn;
    private Button addDemandBtn;
    private TextView test;
    private String url = "https://api.otakedev.com/";
    private JSONObject jsonBody = new JSONObject();

    private View.OnClickListener addDemandBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent demandActivity = new Intent(NewDemand.this, TicketActivity.class);
            startActivity(demandActivity);
        }
    };


    private View.OnClickListener confirmBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                jsonBody.put("supervisor_id", 0);
                jsonBody.put("student_id", "bc06b188-5f63-4bdb-bd1e-481efa8e91a3");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url + "queue/tickets", jsonBody, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    test.setText("HTTP response: " + response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    test.setText("HTTP response error"+ error);
                }
            });
            Volley.newRequestQueue(NewDemand.this).add(jsonObjectRequest);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_demand);
        setTitle(R.string.new_demand_title);

        //Confirm form
        confirmBtn = findViewById(R.id.confirmDemandBtn);
        test = findViewById(R.id.textViewTest);
        confirmBtn.setOnClickListener(confirmBtnListener);

        addDemandBtn = findViewById(R.id.addDemandBtn);
        addDemandBtn.setOnClickListener(addDemandBtnListener);
    }

}
