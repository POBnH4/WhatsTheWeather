package com.example.peterboncheff.coursework;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ForecastRecycleViewAdapter extends RecyclerView.Adapter<ForecastRecycleViewAdapter.ForecastLocationViewHolder> {


    // holder for the data to be displayed
    private List<ForecastLocation> forecastLocations;

    // will be used to inflate the Views that will be displayed by a RecyclerView
    private LayoutInflater inflater;


    public ForecastRecycleViewAdapter(Context context, List<ForecastLocation> data){
        // instantiate the member variables
        inflater = LayoutInflater.from(context);
        forecastLocations = data;
    }

    @NonNull
    @Override
    public ForecastLocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new view by inflating the layout defined in forecastLocation_list_item.xml
        View itemView = inflater.inflate(R.layout.forecast_location_list_item, parent, false );
        return new ForecastLocationViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastLocationViewHolder forecastLocationViewHolder, int position) {
        // retrieve the ForecastLocation at position from the data list
        ForecastLocation currentForecastLocation = forecastLocations.get(position);

        // retrieve the TextView from the View that forecastLocationViewHolder is holding
        TextView forecastLocationTextView = forecastLocationViewHolder.forecastLocationItemView.findViewById(R.id.textView);
        forecastLocationTextView.setText(String.format("%s", currentForecastLocation.getName()));
    }

    /**
     * Returns the number of data items held by this adapter
     */
    @Override
    public int getItemCount() {
        return forecastLocations.size();
    }

    /**
     * Called to set the data that is being held by this adapter
     * @param forecastLocations
     */
    public void setData(List<ForecastLocation> forecastLocations){
        this.forecastLocations = forecastLocations;
        // this will force the contents of the Recyclerview to be updated for the new data
        notifyDataSetChanged();
    }

    class ForecastLocationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View forecastLocationItemView;
        private ForecastRecycleViewAdapter adapter;

        public ForecastLocationViewHolder(View itemView, ForecastRecycleViewAdapter adapter){
            super(itemView);
            forecastLocationItemView = itemView;
            this.adapter = adapter;
            forecastLocationItemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // get the clicked item's position
            int position = getAdapterPosition();

            // get the ForecastLocation that was clicked
            ForecastLocation clickedForecastLocation = forecastLocations.get(position);

            Toast toast = Toast.makeText(v.getContext(), "You clicked " + clickedForecastLocation.getName(), Toast.LENGTH_LONG);
            toast.show();
        }
    }

}
