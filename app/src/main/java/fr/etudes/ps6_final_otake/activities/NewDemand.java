package fr.etudes.ps6_final_otake.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import fr.etudes.ps6_final_otake.R;

public class NewDemand extends AppCompatActivity {

    private Button confirmBtn;
    private TextView test;
    private Integer testString = 0;
    private Map<Integer, Integer> newDemands = new HashMap<>();
    private String url = "https://api.otakedev.com/";

    private View.OnClickListener confirmBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final RequestQueue requestQueue = Volley.newRequestQueue(NewDemand.this);
            StringRequest stringRequest = new StringRequest(
                    Request.Method.GET, url + "status", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    test.setText("Http response status: " + response);
                    requestQueue.stop();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    test.setText("That didn't work!");
                    requestQueue.stop();
                }
            }
            );
            requestQueue.add(stringRequest);
//            testString ++;
//            String str = testString.toString();
//            test.setText(str);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        newDemands.put(0,0);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_demand);
        setTitle(R.string.new_demand_title);

        //Confirm form
        confirmBtn = findViewById(R.id.confirmDemandBtn);
        test = findViewById(R.id.textViewTest);
        confirmBtn.setOnClickListener(confirmBtnListener);
    }

}
