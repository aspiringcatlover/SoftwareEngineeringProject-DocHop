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
 * Represents the Adapter for the PatientAmbulanceHistory java file.
 * Displays the data through a Recycler View.
 *
 * @author Everyone
 * version 1.0
 * @since 2020-03-27
 */
public class PatientAmbulanceHistoryAdapter extends RecyclerView.Adapter<PatientAmbulanceHistoryAdapter.UserAmbulanceHistoryViewHolder> {

    /**
     * The ArrayList of Request objects to be referenced for data display.
     */
    private ArrayList<Request> ambulanceRequests;

    /**
     * The context of the fragment.
     */
    private Context context;

    /**
     * Constructor for the PatientAmbulanceHistoryAdapter method.
     *
     * @param ct                This refers to the context of the activity.
     * @param ambulanceRequests This refers to the ArrayList of Request objects.
     */
    PatientAmbulanceHistoryAdapter(Context ct, ArrayList<Request> ambulanceRequests) {
        this.context = ct;
        this.ambulanceRequests = ambulanceRequests;
    }

    /**
     * Inflates the view with row items defined in user_ambulance_history_row layout resource file.
     *
     * @param parent   This refers to the parent View of the activity.
     * @param viewType This refers to the view type for the row item.
     * @return This refers to the final view after row population.
     */
    @NonNull
    @Override
    public UserAmbulanceHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.user_ambulance_history_row, parent, false);
        return new UserAmbulanceHistoryViewHolder(view);
    }

    /**
     * Binds the data provided to the text items defined in the user_ambulance_history_row layout
     * resource file.
     * @param holder   This refers to the layout of the current view for data population.
     * @param position This refers to the the index of the row in which the data should populate.
     */
    @Override
    public void onBindViewHolder(@NonNull PatientAmbulanceHistoryAdapter.UserAmbulanceHistoryViewHolder holder, int position) {
        holder.ambulanceAcceptTime.setText(ambulanceRequests.get(position).getAcceptanceTime());
        holder.ambulanceDate.setText(ambulanceRequests.get(position).getDate());

    }

    /**
     * Returns the number of row items to be generated.
     *
     * @return number of row items
     */
    @Override
    public int getItemCount() {
        return ambulanceRequests.size();
    }

    /**
     * Creates a UserAmbulanceHistoryViewHolder object for easier data population when inflating the
     * view with user_ambulance_history_row layouts.
     * Matches the id of the text items in user_ambulance_history_row with the data to be provided.
     */
    static class UserAmbulanceHistoryViewHolder extends RecyclerView.ViewHolder {

        /**
         * TextView objects that contains the ambulance acceptance time and acceptance date.
         */
        private TextView ambulanceAcceptTime, ambulanceDate;

        /**
         * UserAmbulanceHistoryViewHolder constructor method.
         *
         * @param itemView View object that maps the ambulance acceptance time data and ambulance
         *                 date data to user_ambulance_history_row layout.
         */
        UserAmbulanceHistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ambulanceAcceptTime = itemView.findViewById(R.id.gpaccepttime);
            ambulanceDate = itemView.findViewById(R.id.gpdate);
        }
    }
}
