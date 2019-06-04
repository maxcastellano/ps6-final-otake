package fr.etudes.ps6_final_otake.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import fr.etudes.ps6_final_otake.R;
import fr.etudes.ps6_final_otake.models.CustomSpinnerOfficeItem;

public class CustomSpinnerOfficeItemAdapter extends ArrayAdapter<CustomSpinnerOfficeItem> {
    public CustomSpinnerOfficeItemAdapter(@NonNull Context context, ArrayList<CustomSpinnerOfficeItem> officeItems) {
        super(context, 0, officeItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return customView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return customView(position, convertView, parent);
    }

    public View customView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_spinner_item_layout, parent, false);
        }
        CustomSpinnerOfficeItem items = getItem(position);
        TextView office = convertView.findViewById(R.id.officeTxt);
        TextView availability = convertView.findViewById(R.id.availabilityTxt);
        TextView delay = convertView.findViewById(R.id.waitingDelayTxt);
        TextView rank = convertView.findViewById(R.id.rankNumberTxt);

        if (items != null){
            office.setText(items.getOffice());
            if(!items.getAvailability()){
                availability.setTextColor(Color.RED);
                availability.setText("Absent ⚠");
            }
            delay.setText(items.getWaitingDelay() + " min");
            rank.setText(items.getRank());
        }
        return convertView;
    }
}
