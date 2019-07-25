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

import model.FlightItinerary;

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
    public void onBindViewHolder(@NonNull ProposedFlightsRecyclerAdapter.ViewHolder holder, int position) {

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

            voteButton = itemView.findViewById(R.id.propose_button);

            voteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (clickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            clickListener.onItemClick(position);
                            voteButton.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            });
        }
    }
}
