package com.example.peterboncheff.coursework;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private OnItemClickListener onItemClickListener;
    private ArrayList<Location> locations;
    private Context myContext;

    public MyAdapter(Context context, ArrayList<Location> locations) {
        this.myContext = context;
        this.locations = locations;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(myContext).inflate(R.layout.module_spinner_item, parent, false);
        return new MyViewHolder(v, onItemClickListener);
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView nameOfLocation, temperature, lastUpdated;

        public MyViewHolder(View v, final OnItemClickListener listener) {
            super(v);
//            this.nameOfLocation = v.findViewById(R.id.nameOfLocationRecycleVIew);
//            this.temperature = v.findViewById(R.id.currentTemperatureRecycleVIew);
//            this.lastUpdated = v.findViewById(R.id.lastUpdatedRecycleView);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int currentPosition = getAdapterPosition();
                        if(currentPosition != RecyclerView.NO_POSITION){
                            listener.onItemClick(currentPosition);
                        }
                    }
                }
            });
        }
    }


    public void insert(int position,Location location){
        locations.add(position, location);
        notifyDataSetChanged();

    }

    public void delete(int position){
        locations.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d("hallo", "hello from onBindViewHolder");
        //Location currentItem = locations.get(position);
     //   holder.itemView.setLongClickable(true);
     //   holder.nameOfLocation.setText(currentItem.getNameOfLocation());
     //   holder.temperature.setText(String.format("%s°", currentItem.getCurrentTemperature()));
     //   holder.lastUpdated.setText(currentItem.getLastUpdated());
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return locations.size();
    }
}
