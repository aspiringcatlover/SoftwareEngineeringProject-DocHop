package com.example.dochop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

/**
 * Represents the Adapter for the GPHistoryUI java file.
 * Displays the data through a Recycler View.
 *
 * @author Everyone
 * version 1.0
 * @since 2020-03-27
 */
public class GpHistoryAdapter extends RecyclerView.Adapter<GpHistoryAdapter.MyViewHolder> {

    /**
     * The ArrayList of Request objects to be referenced for data display.
     */
    private ArrayList<Request> sampleRequest;

    /**
     * The context of the activity.
     */
    private Context context;

    /**
     * Constructor for the GPHistoryAdapter method.
     *
     * @param ct            This refers to the context of the activity.
     * @param sampleRequest This refers to the ArrayList of Request objects.
     */
    GpHistoryAdapter(Context ct, ArrayList<Request> sampleRequest) {
        context = ct;
        this.sampleRequest = sampleRequest;
    }

    /**
     * Inflates the view with row items defined in gp_row layout resource file.
     *
     * @param parent   This refers to the parent View of the activity.
     * @param viewType This refers to the view type for the row item.
     * @return This refers to the final view after ow population.
     */
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.gp_row, parent, false);
        return new MyViewHolder(view);
    }

    /**
     * Binds the data provided to the text items defined in the gp_row layout resource file.
     *
     * @param holder   This refers to the layout of the current view for data population.
     * @param position This refers to the the index of the row in which the data should populate.
     */
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.patient.setText(String.format("%s %s", sampleRequest.get(position).getPatient().getFirstName(), sampleRequest.get(position).getPatient().getLastName()));
        holder.date.setText(sampleRequest.get(position).getDate());
        holder.acceptanceTimeData.setText(sampleRequest.get(position).getAcceptanceTime());
        holder.completionTimeData.setText(sampleRequest.get(position).getCompletionTime());
        holder.contactInfoData.setText(sampleRequest.get(position).getPatient().getPhoneNumber());
    }

    /**
     * Returns the number of row items to be generated.
     */
    @Override
    public int getItemCount() {
        return sampleRequest.size();
    }

    /**
     * Creates a MyViewHolder object for easier data population when inflating the view with gp_row
     * layouts.
     * Matches the id of the text items in gp_row with the data to be provided.
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView patient, date, acceptanceTimeData, completionTimeData, contactInfoData;

        private MyViewHolder(@NonNull View itemView) {
            super(itemView);
            patient = itemView.findViewById((R.id.patient));
            date = itemView.findViewById((R.id.date));
            acceptanceTimeData = itemView.findViewById((R.id.acceptancetimedata));
            completionTimeData = itemView.findViewById((R.id.completiontimedata));
            contactInfoData = itemView.findViewById((R.id.contactinfodata));
        }
    }
}
