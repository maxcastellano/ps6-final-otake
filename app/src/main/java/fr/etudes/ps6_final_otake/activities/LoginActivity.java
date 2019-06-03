package fr.etudes.ps6_final_otake.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

                Log.d("debug", "onClick: prénom : " + user.getFirstName());
                Log.d("debug", "onClick: nom : " + user.getLastName());
                Log.d("debug", "onClick: cursus : " + user.getMajor());
            }
        });
    }
}
