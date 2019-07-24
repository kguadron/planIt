package ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planit.PostTripActivity;
import com.example.planit.R;
import com.example.planit.TripListActivity;

import java.util.List;

import model.Trip;

public class TripRecyclerAdapter extends RecyclerView.Adapter<TripRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<Trip> tripList;
    private OnItemClickListener clickListner;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListner(OnItemClickListener listener){
        clickListner = listener;
    }

    public TripRecyclerAdapter(Context context, List<Trip> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public TripRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.trip_row, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull TripRecyclerAdapter.ViewHolder viewHolder, int position) {

        Trip trip = tripList.get(position);

        viewHolder.name.setText(trip.getName());
        viewHolder.description.setText(trip.getDescription());
        viewHolder.tripID.setText("Trip ID: " + trip.getTripId());
    }

    @Override
    public int getItemCount() {
        return tripList.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView
                name,
                description,
                tripID;
        String userId;
        String userName;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;

            name = itemView.findViewById(R.id.trip_name_list);
            description = itemView.findViewById(R.id.trip_description_list);
            tripID = itemView.findViewById(R.id.tripRowId);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListner != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            clickListner.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
