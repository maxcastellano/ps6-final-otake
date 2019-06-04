package fr.etudes.ps6_final_otake.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import fr.etudes.ps6_final_otake.R;

public class FormFragment extends Fragment {
    Spinner object;
    Spinner office;
    ArrayAdapter<CharSequence> objectAdapter;
    ArrayAdapter<CharSequence> officeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.form_new_ticket, container, false);
        object = view.findViewById(R.id.objectSpinner);
        office = view.findViewById(R.id.officeSpinner);

        objectAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.form_object_content, R.layout.my_spinner);
        officeAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.form_office_content, R.layout.my_spinner);

        object.setAdapter(objectAdapter);
        office.setAdapter(officeAdapter);

        return view;
    }
}
