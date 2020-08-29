package com.example.dochop;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Represents the Adapter for the RecyclerView in GpHomeUI.
 *
 * @author Everyone
 * @version 1.0
 * @since 2020-03-27
 */
public class RequestListAdapter extends RecyclerView.Adapter<RequestListAdapter.RequestViewHolder> {

    /**
     * ArrayList that stores pending request objects.
     */
    private ArrayList<Request> requestArrayList;

    /**
     * Listener to listen for any clicks on individual items within the RecyclerView.
     */
    final private ListItemClickListener onClickListener;

    /**
     * Context of the current running application.
     */
    private Context context;

    /**
     * Creates a RequestViewHolder object for easier data population when inflating the view with
     * user_gp_history_row layouts.
     * Matches the id of the text items in request_list_item with the data to be provided.
     */
    public class RequestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        /**
         * TextView to be populated with the patient's current address.
         */
        private TextView address;

        /**
         * TextView to be populated with the patient's details.
         * This includes their gender, age, height and weight.
         */
        private TextView patientDetails;

        /**
         * Constructor for RequestViewHolder.
         *
         * @param itemView View object that maps the patient's address and details to
         *                 request_list_item layout.
         */
        RequestViewHolder(View itemView) {
            super(itemView);

            address = itemView.findViewById(R.id.address);
            patientDetails = itemView.findViewById(R.id.patient_details);
            itemView.setOnClickListener(this);
        }

        /**
         * Activates if an item within the RecyclerView is clicked.
         *
         * @param view Single view object that resides within the RecyclerView.
         */
        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            onClickListener.onListItemClick(clickedPosition);
        }
    }

    /**
     * Constructor for RequestListAdapter.
     *
     * @param requestArrayList ArrayList that stores pending request objects.
     * @param listener         Listener to listen for any clicks on individual items within the RecyclerView.
     * @param context          Context of the current running application.
     */
    RequestListAdapter(ArrayList<Request> requestArrayList, ListItemClickListener listener, Context context) {
        this.requestArrayList = requestArrayList;
        onClickListener = listener;
        this.context = context;
    }

    /**
     * Required interface implementation of ListItemClickListener.
     */
    public interface ListItemClickListener {
        void onListItemClick(int clickedItemIndex);
    }

    /**
     * Inflates the view with row items defined in request_list_item layout resource file.
     *
     * @param viewGroup This refers to the parent View of the activity.
     * @param viewType  This refers to the view type for the row item.
     * @return This refers to the final view after row population.
     */
    @NonNull
    @Override
    public RequestListAdapter.RequestViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.request_list_item, viewGroup, false);

        return new RequestViewHolder(view);
    }

    /**
     * Binds the data provided to the text items defined in the request_list_item layout resource
     * file.
     *
     * @param holder   This refers to the layout of the current view for data population.
     * @param position This refers to the the index of the row in which the data should populate.
     */
    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Patient requestPatient = requestArrayList.get(position).getPatient();

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> requestAddress = new ArrayList<>();
        try {
            requestAddress = geocoder.getFromLocation(requestPatient.getLatitude(), requestPatient.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String patientGender = String.valueOf(requestPatient.getGender());
        String patientAge = String.valueOf(requestPatient.getAge());
        String patientHeight = String.valueOf(requestPatient.getHeight());
        String patientWeight = String.valueOf(requestPatient.getWeight());
        String requestPatientDetails = patientGender + ", " + patientAge + ", " + patientHeight + "cm, " + patientWeight + "kg";

        holder.address.setText(requestAddress.get(0).getAddressLine(0));
        holder.patientDetails.setText(requestPatientDetails);
    }

    /**
     * Returns the number of row items to be generated.
     *
     * @return Number of row items.
     */
    @Override
    public int getItemCount() {
        return requestArrayList.size();
    }
}
