package ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planit.R;

import java.util.List;

import data.FlightList;
import model.Flight;
import model.FlightItinerary;
import model.Trip;

public class SearchResultsRecyclerAdapter extends RecyclerView.Adapter<SearchResultsRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<FlightItinerary> flightList;
    private SearchResultsRecyclerAdapter.OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListner(SearchResultsRecyclerAdapter.OnItemClickListener listener){
        clickListener = listener;
    }

    public SearchResultsRecyclerAdapter(Context context, List<FlightItinerary> flightList) {
        this.context = context;
        this.flightList = flightList;
    }

    @NonNull
    @Override
    public SearchResultsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.results_row, viewGroup, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsRecyclerAdapter.ViewHolder viewHolder, int position) {

        FlightItinerary flightItinerary = flightList.get(position);
        Flight outbound = flightItinerary.getFlights().get(0);
        Flight inbound = flightItinerary.getFlights().get(1);

        viewHolder.price.setText("$" + flightItinerary.getTotalPrice().toString().trim());
        viewHolder.airline.setText(outbound.getAirline());

        viewHolder.outboundDate.setText(outbound.getDepartureDate());
        viewHolder.outboundIatas.setText(outbound.getOrigin() + " - " + outbound.getDestination());
        viewHolder.outboundTimes.setText(outbound.getDepartureTime() + " - " + outbound.getReturnTime());
        viewHolder.outboundDuration.setText(outbound.getDuration());

        viewHolder.inboundDate.setText(inbound.getDepartureDate());
        viewHolder.inboundIatas.setText(inbound.getOrigin() + " - " + inbound.getDestination());
        viewHolder.inboundTimes.setText(inbound.getDepartureTime() + " - " + inbound.getReturnTime());
        viewHolder.inboundDuration.setText(inbound.getDuration());


    }

    @Override
    public int getItemCount() {
        return flightList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView
                price,
                airline,
                outboundDate,
                outboundIatas,
                outboundTimes,
                outboundDuration,
                inboundDate,
                inboundIatas,
                inboundTimes,
                inboundDuration;
        public Button proposeButton;


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            context = ctx;
            price = itemView.findViewById(R.id.total_price);
            airline = itemView.findViewById(R.id.airline);
            outboundDate = itemView.findViewById(R.id.out_dep_date);
            outboundIatas = itemView.findViewById(R.id.outbound_iatas);
            outboundTimes = itemView.findViewById(R.id.out_dep_arri_time);
            outboundDuration = itemView.findViewById(R.id.outbound_duration);
            inboundDate = itemView.findViewById(R.id.in_dep_date);
            inboundIatas = itemView.findViewById(R.id.inbound_iatas);
            inboundTimes = itemView.findViewById(R.id.in_dep_arri_time);
            inboundDuration = itemView.findViewById(R.id.inbound_duration);
            proposeButton = itemView.findViewById(R.id.propose_button);

            proposeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            clickListener.onItemClick(position);
                            proposeButton.setVisibility(View.INVISIBLE);
                        }
                    }

                }
            });
        }
    }
}
