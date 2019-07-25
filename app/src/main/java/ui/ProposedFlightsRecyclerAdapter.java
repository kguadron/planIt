package ui;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.planit.R;

import java.util.List;

import model.Flight;
import model.FlightItinerary;
import util.TripApi;

public class ProposedFlightsRecyclerAdapter extends RecyclerView.Adapter<ProposedFlightsRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<FlightItinerary> flightList;
    private ProposedFlightsRecyclerAdapter.OnItemClickListener clickListener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListner(ProposedFlightsRecyclerAdapter.OnItemClickListener listener) {
        clickListener = listener;
    }

    public ProposedFlightsRecyclerAdapter(Context context, List<FlightItinerary> flightList) {
        this.context = context;
        this.flightList = flightList;
    }

    @NonNull
    @Override
    public ProposedFlightsRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.proposed_row, viewGroup, false);

        return new ViewHolder(view, context);

    }

    @Override
    public void onBindViewHolder(@NonNull ProposedFlightsRecyclerAdapter.ViewHolder viewHolder, int position) {
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
        viewHolder.votes.setText(String.valueOf(flightItinerary.getUserVoted().size()));

//        if (flightItinerary.getUserVoted().contains(TripApi.getInstance().getUserId())) {
//            viewHolder.voteButton.setVisibility(View.INVISIBLE);
//        }

        if (flightItinerary.getUserVoted().size() == 1) {
            viewHolder.votes.setTextColor(Color.parseColor("#F3BACF"));
        } else if (flightItinerary.getUserVoted().size() == 2) {
            viewHolder.votes.setTextColor(Color.parseColor("#FFB966"));
        } else if (flightItinerary.getUserVoted().size() == 3) {
            viewHolder.votes.setTextColor(Color.parseColor("#FFE814"));
        } else if (flightItinerary.getUserVoted().size() == 4) {
            viewHolder.votes.setTextColor(Color.parseColor("#9BEFB0"));
        } else if (flightItinerary.getUserVoted().size() == 5) {
            viewHolder.votes.setTextColor(Color.parseColor("#84EBD8"));
        } else if (flightItinerary.getUserVoted().size() == 6) {
            viewHolder.votes.setTextColor(Color.parseColor("#6EB9E7"));
        } else if (flightItinerary.getUserVoted().size() == 7) {
            viewHolder.votes.setTextColor(Color.parseColor("#585FE3"));
        } else if (flightItinerary.getUserVoted().size() == 8) {
            viewHolder.votes.setTextColor(Color.parseColor("#9243DF"));
        } else if (flightItinerary.getUserVoted().size() == 9) {
            viewHolder.votes.setTextColor(Color.parseColor("#DB2ECF"));
        } else if (flightItinerary.getUserVoted().size() >= 10) {
            viewHolder.votes.setTextColor(Color.parseColor("#D81B60"));
        }
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
                inboundDuration,
                votes;
        public Button voteButton;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            context = ctx;
            price = itemView.findViewById(R.id.total_price_proposed);
            airline = itemView.findViewById(R.id.airline_proposed);
            outboundDate = itemView.findViewById(R.id.out_dep_date_proposed);
            outboundIatas = itemView.findViewById(R.id.outbound_iatas_proposed);
            outboundTimes = itemView.findViewById(R.id.out_dep_arri_time_proposed);
            outboundDuration = itemView.findViewById(R.id.outbound_duration_proposed);
            inboundDate = itemView.findViewById(R.id.in_dep_date_proposed);
            inboundIatas = itemView.findViewById(R.id.inbound_iatas_proposed);
            inboundTimes = itemView.findViewById(R.id.in_dep_arri_time_proposed);
            inboundDuration = itemView.findViewById(R.id.inbound_duration_proposed);
            votes = itemView.findViewById(R.id.votes_number);

            voteButton = itemView.findViewById(R.id.vote_button);

            voteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            clickListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
