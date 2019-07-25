package com.example.planit;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.common.collect.Table;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.FlightItinerary;
import model.Trip;
import ui.ProposedFlightsRecyclerAdapter;
import ui.SearchResultsRecyclerAdapter;
import util.TripApi;

public class TripDetailsActivity extends AppCompatActivity  {

    private String tripId;
    private TextView tripNameText;
    private TextView idText;
    private Button flightSearchButton;

    private RecyclerView proposedFlightsRecycler;
    private List<FlightItinerary> flightList = new ArrayList<>();
    private ProposedFlightsRecyclerAdapter proposedFlightsRecyclerAdapter;

    private RecyclerView travelBudsRecycler;

    // Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Trip");
    private CollectionReference flightsCollection = db.collection("Flights");

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        tripNameText = findViewById(R.id.trip_details_name);
        idText = findViewById(R.id.trip_detials_id);
        flightSearchButton = findViewById(R.id.flight_search_button);

        travelBudsRecycler = findViewById(R.id.travel_buds_recycler);

        proposedFlightsRecycler = findViewById(R.id.proposed_flights_recycler);
        proposedFlightsRecycler.setHasFixedSize(true);
        proposedFlightsRecycler.setLayoutManager(new LinearLayoutManager(this));


        // Gets tripId value from TripListActivity
        tripId = getIntent().getStringExtra("TRIP_ID");
        idText.setText("TRIP ID: " + tripId);

        // db query to display trip name
        collectionReference.whereEqualTo("tripId", tripId).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot trips : queryDocumentSnapshots) {
                            Trip trip = trips.toObject(Trip.class);
                            tripNameText.setText(trip.getName());
                        }
                    }
                });

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        flightSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TripDetailsActivity.this,
                        FlightSearchActivity.class);
                intent.putExtra("TRIP_ID", tripId);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        flightsCollection.whereEqualTo("tripId", tripId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot flights : queryDocumentSnapshots) {
                                FlightItinerary flight = flights.toObject(FlightItinerary.class);
                                flightList.add(flight);
                            }

                            proposedFlightsRecyclerAdapter = new ProposedFlightsRecyclerAdapter(TripDetailsActivity.this,
                                    flightList);
                            proposedFlightsRecycler.setAdapter(proposedFlightsRecyclerAdapter);
                            proposedFlightsRecyclerAdapter.notifyDataSetChanged();

                            proposedFlightsRecyclerAdapter.setOnItemClickListner(new ProposedFlightsRecyclerAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    FlightItinerary chosenFlight = flightList.get(position);
                                    Log.d("on click", "onItemClick: VOTE BUTTON");
                                    Log.d("on VOTE click", "user id: " + TripApi.getInstance().getUserId());
                                    Log.d("on VOTE click", "flight id: " + chosenFlight.getFlightId());


                                    chosenFlight.getUserVoted().add(TripApi.getInstance().getUserId());
                                    chosenFlight.setVoteCount(chosenFlight.getUserVoted().size());
                                    Log.d("on VOTE click", "user array size: " + chosenFlight.getUserVoted().size());

                                    flightsCollection.document(chosenFlight.getFlightId()).set(chosenFlight, SetOptions.merge());
                                    proposedFlightsRecyclerAdapter.notifyDataSetChanged();
                                }
                            });

                        } else {
                        // the query was empty
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add:
                // Take users to post trip
                if (user != null && firebaseAuth != null) {
                    startActivity(new Intent(TripDetailsActivity.this,
                            PostTripActivity.class));
//                    finish();
                }
                break;
            case R.id.action_signout:
                // sign user out
                if (user != null && firebaseAuth != null) {
                    firebaseAuth.signOut();

                    startActivity(new Intent(TripDetailsActivity.this,
                            MainActivity.class));
//                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
