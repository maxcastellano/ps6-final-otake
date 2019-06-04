package fr.etudes.ps6_final_otake.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;

import fr.etudes.ps6_final_otake.R;
import fr.etudes.ps6_final_otake.models.Ticket;

public class TicketAdapter extends BaseAdapter {
    private ArrayList<Ticket> mData;
    private LayoutInflater mInflater;
    private String url = "https://api.otakedev.com/";
    private JSONObject jsonBody = new JSONObject();

    private Button delete;
    private TextView rang_title;
    private TextView rang;
    private TextView supervisor;
    private TextView info;
    private Context context;

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
    public View getView(final int position, View convertView, final ViewGroup viewGroup){
        View v = mInflater.inflate(R.layout.card_ticket,null);
        Ticket ticket = mData.get(position);

        delete =(Button) v.findViewById(R.id.button_delete);
        delete.setTag(position);
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                AlertDialog.Builder altdial = new AlertDialog.Builder(context);
                altdial.setTitle("Supprimer");
                altdial.setMessage("Etes vous sûr de ne plus vouloir participer à cet entretien ?").setCancelable(false)
                        .setNegativeButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url + "queue/tickets/" + mData.get(position).getStudentId() + "/" + mData.get(position).getTicketId(), jsonBody, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {

                                    }
                                },new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                                mData.remove(position);
                                Volley.newRequestQueue(context).add(request);
                                notifyDataSetChanged();
                            }
                        })
                        .setPositiveButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

                AlertDialog alert = altdial.create();
                alert.show();
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(0,150,136));
                alert.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.RED);
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
        supervisor.setText("Ticket - " + ticket.getSupervisor());
        supervisor.setTextSize(22);

        info = (TextView)v.findViewById(R.id.ticket_info);
        info.setTag(position);
        info.setText(ticket.getObject() + "\n" + "Attente estimé: " + ticket.getWaitingTime() + "min\n" + "Salle: " + ticket.getOffice());
        info.setTextSize(15);

        return v;
    }

    public void setContext(Context context){
        this.context = context;
    }

}
