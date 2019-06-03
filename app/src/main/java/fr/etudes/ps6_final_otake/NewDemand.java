package fr.etudes.ps6_final_otake;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NewDemand extends AppCompatActivity {
    private Button confirmBtn;
    private TextView test;
    private Integer testString = 0;
    private View.OnClickListener confirmBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            testString ++;
            String str = testString.toString();
            test.setText(str);
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
    }

}
