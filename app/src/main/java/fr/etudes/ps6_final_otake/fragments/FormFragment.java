package fr.etudes.ps6_final_otake.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import fr.etudes.ps6_final_otake.R;
import fr.etudes.ps6_final_otake.adapters.CustomSpinnerOfficeItemAdapter;
import fr.etudes.ps6_final_otake.mocks.OfficeItemsMock;
import fr.etudes.ps6_final_otake.models.CustomSpinnerOfficeItem;

public class FormFragment extends Fragment {
    Spinner object;
    Spinner office;
    ArrayAdapter<CharSequence> objectAdapter;
    CustomSpinnerOfficeItemAdapter officeAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.form_new_ticket, container, false);
        object = view.findViewById(R.id.objectSpinner);
        office = view.findViewById(R.id.officeSpinner);

        objectAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.form_object_content, R.layout.my_spinner);
        //TODO fetch model from database a.k.a replace mock
        officeAdapter = new CustomSpinnerOfficeItemAdapter(getActivity(), new OfficeItemsMock().getOfficeItems());

        object.setAdapter(objectAdapter);

        if (office != null){
            office.setAdapter(officeAdapter);
            office.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    CustomSpinnerOfficeItem officeItem = (CustomSpinnerOfficeItem) parent.getSelectedItem();
                    Log.d("Selected Item ||||||||||||||||||||||> ", officeItem.getOffice());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        return view;
    }
}
