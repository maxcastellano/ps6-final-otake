package fr.etudes.ps6_final_otake.activities;

import android.content.Intent;
import android.support.v4.app.Fragment;
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

import fr.etudes.ps6_final_otake.R;
import fr.etudes.ps6_final_otake.fragments.FormFragment;
import fr.etudes.ps6_final_otake.models.CustomSpinnerOfficeItem;

public class NewDemand extends AppCompatActivity {

    private Button confirmBtn;
    private Button addDemandBtn;
    private String url = "https://api.otakedev.com/";
    private String targetSupervisor;
    private JSONObject jsonBody = new JSONObject();
    private FormFragment ticketFormFragment;

    private View.OnClickListener addDemandBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

//            holder.mMyFragment = new (mActivity, this);
//            int id = View.generateViewId();
//            findViewByTag("abc").setId(id);
//            mActivity.getSupportFragmentManager().beginTransaction().add(id, holder.mMyFragment).commit();
        }
    };


    private View.OnClickListener confirmBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            View view = (View)ticketFormFragment.getView();
            Spinner spinner = (Spinner) view.findViewById(R.id.officeSpinner);
            //TODO find solution for instanceof
            if (spinner.getSelectedItem() instanceof CustomSpinnerOfficeItem){
                targetSupervisor = ((CustomSpinnerOfficeItem) spinner.getSelectedItem()).getOffice();
            }
            Log.d("Selected String >>>>>>>>> ", targetSupervisor);
            try {
                jsonBody.put("supervisor_id", getCorrespondingSupervisor(targetSupervisor));
                jsonBody.put("student_id", "bc06b188-5f63-4bdb-bd1e-481efa8e91a3");
            } catch (JSONException e) {
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
            Volley.newRequestQueue(NewDemand.this).add(jsonObjectRequest);
            Intent demandActivity = new Intent(NewDemand.this, TicketActivity.class);
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

        ticketFormFragment = (FormFragment) getSupportFragmentManager().findFragmentById(R.id.form_fragment);
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
