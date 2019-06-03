package fr.etudes.ps6_final_otake.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import fr.etudes.ps6_final_otake.R;
import fr.etudes.ps6_final_otake.models.Ticket;

public class TicketAdapter extends BaseAdapter {
    private ArrayList<Ticket> mData;
    private LayoutInflater mInflater;

    private Button delete;
    private TextView rang_title;
    private TextView rang;
    private TextView supervisor;
    private TextView info;

    public TicketAdapter(LayoutInflater inflater, ArrayList<Ticket> data) {
        this.mInflater = inflater;
        this.mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup){
        View v = mInflater.inflate(R.layout.card_ticket,null);
        Ticket ticket = mData.get(position);

        delete =(Button) v.findViewById(R.id.button_delete);
        delete.setTag(position);
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                mData.remove(position);
                notifyDataSetChanged();
            }

        });

        rang_title = (TextView)v.findViewById(R.id.ticket_rang_title);
        rang_title.setTag(position);
        rang_title.setText("Rang");
        rang_title.setTextSize(30);

        rang = (TextView)v.findViewById(R.id.ticket_rang);
        rang.setTag(position);
        rang.setText(Integer.toString(ticket.getRank()));
        rang.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        rang.setTextSize(70);

        supervisor = (TextView)v.findViewById(R.id.ticket_supervisor);
        supervisor.setTag(position);
        supervisor.setText("\n  Ticket - " + ticket.getSupervisor());
        supervisor.setTextSize(22);

        info = (TextView)v.findViewById(R.id.ticket_info);
        info.setTag(position);
        info.setText("    " + ticket.getObject() + "\n" + "    Attente estimé: " + ticket.getWaitingTime() + "min\n" + "    Salle: " + ticket.getOffice());
        info.setTextSize(15);

        return v;
    }

}
