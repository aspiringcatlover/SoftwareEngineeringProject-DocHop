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
 * Represents the Adapter for the PatientGPHistory java file.
 * Displays the data through a Recycler View.
 *
 * @author Everyone
 * version 1.0
 * @since 2020-03-27
 */
public class PatientGpHistoryAdapter extends RecyclerView.Adapter<PatientGpHistoryAdapter.UserGpHistoryViewHolder> {

    /**
     * The ArrayList of Request objects to be referenced for data display.
     */
    private ArrayList<Request> requests;

    /**
     * The context of the fragment.
     */
    private Context context;

    /**
     * Constructor for the PatientGpHistoryAdapter method.
     *
     * @param ct       This refers to the context of the activity.
     * @param requests This refers to the ArrayList of Request objects.
     */
    PatientGpHistoryAdapter(Context ct, ArrayList<Request> requests) {
        this.context = ct;
        this.requests = requests;
    }

    /**
     * Inflates the view with row items defined in user_gp_history_row layout resource file.
     *
     * @param parent   This refers to the parent View of the activity.
     * @param viewType This refers to the view type for the row item.
     * @return This refers to the final view after row population.
     */
    @NonNull
    @Override
    public UserGpHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_gp_history_row, parent, false);
        return new UserGpHistoryViewHolder(view);
    }

    /**
     * Binds the data provided to the text items defined in the user_gp_history_row layout
     * resource file.
     * @param holder   This refers to the layout of the current view for data population.
     * @param position This refers to the the index of the row in which the data should populate.
     */
    @Override
    public void onBindViewHolder(@NonNull UserGpHistoryViewHolder holder, int position) {
        holder.doctor.setText(String.format("%s %s", requests.get(position).getGp().getFirstName(), requests.get(position).getGp().getLastName()));
        holder.acceptTime.setText(requests.get(position).getAcceptanceTime());
        holder.completeTime.setText(requests.get(position).getCompletionTime());
        holder.contact.setText(requests.get(position).getGp().getPhoneNumber());
        holder.date.setText(requests.get(position).getDate());

    }

    /**
     * Returns the number of row items to be generated.
     *
     * @return number of row items.
     */
    @Override
    public int getItemCount() {
        return requests.size();
    }

    /**
     * Creates a UserGPHistoryViewHolder object for easier data population when inflating the view
     * with user_gp_history_row layouts.
     * Matches the id of the text items in user_gp_history_row with the data to be provided.
     */
    static class UserGpHistoryViewHolder extends RecyclerView.ViewHolder {

        /**
         * TextView objects containing the gp name, gp acceptance time, gp completion time, gp
         * contact and gp request date.
         */
        private TextView doctor, acceptTime, completeTime, contact, date;

        /**
         * UserGPHistoryViewHolder constructor method.
         *
         * @param itemView View object that maps the ambulance acceptance time data and ambulance
         *                 date data to user_gp_history_row layout.
         */
        UserGpHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            doctor = itemView.findViewById(R.id.doctor);
            acceptTime = itemView.findViewById(R.id.gpaccepttime);
            completeTime = itemView.findViewById(R.id.gpcompletetime);
            contact = itemView.findViewById(R.id.contact);
            date = itemView.findViewById(R.id.gpdate);
        }
    }
}
