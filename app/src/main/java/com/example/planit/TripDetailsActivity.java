package com.example.planit;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.common.collect.Table;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.auth.User;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import model.FlightItinerary;
import model.Trip;
import ui.ProposedFlightsRecyclerAdapter;
import ui.SearchResultsRecyclerAdapter;
import ui.UsersRecyclerAdapter;
import util.TripApi;

public class TripDetailsActivity extends AppCompatActivity  {

    private String tripId;
    private TextView tripNameText;
    private TextView idText;
    private TextView flightCloseX;
    private TextView userCloseX;
    private Button flightSearchButton;
    private Button seeAllFlightsButton;
    private Button seeAllUsersButton;
    String tripName;

    private RecyclerView proposedFlightsRecycler;
    private List<FlightItinerary> flightList = new ArrayList<>();
    private ProposedFlightsRecyclerAdapter proposedFlightsRecyclerAdapter;


    private RecyclerView travelBudsRecycler;
    private  List<String> userList = new ArrayList<>();
    private UsersRecyclerAdapter usersRecyclerAdapter;

    // Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference tripsCollection = db.collection("Trip");
    private CollectionReference flightsCollection = db.collection("Flights");
    private CollectionReference usersCollection = db.collection("Users");

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    Dialog flightDialog;
    Dialog userDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        tripNameText = findViewById(R.id.trip_details_name);
        idText = findViewById(R.id.trip_detials_id);
        flightSearchButton = findViewById(R.id.flight_search_button);

        seeAllFlightsButton = findViewById(R.id.see_all_flights);
        seeAllFlightsButton.setVisibility(View.INVISIBLE);

        seeAllUsersButton = findViewById(R.id.see_all_users);
        seeAllUsersButton.setVisibility(View.INVISIBLE);

        flightDialog = new Dialog(this);
        userDialog = new Dialog(this);

        travelBudsRecycler = findViewById(R.id.travel_buds_recycler);
        travelBudsRecycler.setHasFixedSize(true);
        travelBudsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));

        proposedFlightsRecycler = findViewById(R.id.proposed_flights_recycler);
        proposedFlightsRecycler.setHasFixedSize(true);
        proposedFlightsRecycler.setLayoutManager(new LinearLayoutManager(this));


        // Gets tripId value from TripListActivity
        tripId = getIntent().getStringExtra("TRIP_ID");
        idText.setText("TRIP ID: " + tripId);

        // db query to display trip name
        tripsCollection.whereEqualTo("tripId", tripId).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (TextUtils.isEmpty(tripNameText.getText())) {
                                    tripName = document.get("name").toString().trim();
                                    Log.d("TRIP NAME", "onSuccess: DOCUMENT " + document);
                                    Log.d("TRIP NAME", "onSuccess: trip name " + tripName);
                                    tripNameText.setText(tripName);
                                } else {
                                    Log.d("TRIP NAME", "onComplete: tripName is not empty: " + tripNameText.getText());
                                }
                            }
                        } else {
                            Log.d("TRIP NAME", "onComplete: task not successful" + tripName);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("TRIP NAME", "onFailure: trip query " + e.getMessage());
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

        // query to populate users list
        tripsCollection.whereEqualTo("tripId", tripId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot trips : queryDocumentSnapshots) {
                                Trip trip = trips.toObject(Trip.class);
                                List<String> userIdList = trip.getUsers();
                                for (String userId : userIdList) {
                                    usersCollection.whereEqualTo("userId", userId)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            userList.add(document.get("username").toString().trim());
                                                            Log.d("USERNAME", "the username" + document.get("username"));
                                                            Log.d("USERNAME", "the usernames" + userList);
                                                        }

                                                        if (userList.size() > 1) {
                                                            seeAllUsersButton.setVisibility(View.VISIBLE);
                                                        }
                                                        usersRecyclerAdapter = new UsersRecyclerAdapter(TripDetailsActivity.this,
                                                                userList);
                                                        travelBudsRecycler.setAdapter(usersRecyclerAdapter);
                                                        usersRecyclerAdapter.notifyDataSetChanged();
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    // failure with query
                                                }
                                            });
                                }
                            }
                        } else {
                            // tripId query empty
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // failure with trip id query
                    }
                });

        // query to populate proposed flights view
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

                            if (flightList.size() > 1) {
                                seeAllFlightsButton.setVisibility(View.VISIBLE);
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
//            case R.id.action_add:
//                // Take users to post trip
//                if (user != null && firebaseAuth != null) {
//                    startActivity(new Intent(TripDetailsActivity.this,
//                            PostTripActivity.class));
//                    finish();
//                }
//                break;
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

    public void FlightShowPopup (View v) {
        flightDialog.setContentView(R.layout.flights_popup);

        RecyclerView flightPopupRecycler = flightDialog.findViewById(R.id.flight_popup_recycler);
        flightPopupRecycler.setHasFixedSize(true);
        flightPopupRecycler.setLayoutManager(new LinearLayoutManager(TripDetailsActivity.this));


        final ProposedFlightsRecyclerAdapter flightPopupAdapter = new ProposedFlightsRecyclerAdapter(TripDetailsActivity.this,
                flightList);
        flightPopupRecycler.setAdapter(flightPopupAdapter);
        flightPopupAdapter.notifyDataSetChanged();


        flightPopupAdapter.setOnItemClickListner(new ProposedFlightsRecyclerAdapter.OnItemClickListener() {
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
                flightPopupAdapter.notifyDataSetChanged();
            }
        });



        flightCloseX = flightDialog.findViewById(R.id.flight_popup_close);
        Log.d("POP", "ShowPopup: flight list : " + flightList);

        flightCloseX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flightDialog.dismiss();
            }
        });
        flightDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        flightDialog.show();
    }

    public void UserShowPopup (View v) {
        userDialog.setContentView(R.layout.users_popup);

        RecyclerView userPopupRecycler = userDialog.findViewById(R.id.user_popup_recycler);
        userPopupRecycler.setHasFixedSize(true);
        userPopupRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayout.HORIZONTAL, false));


        final UsersRecyclerAdapter userPopupAdapter = new UsersRecyclerAdapter(TripDetailsActivity.this,
                userList);
        userPopupRecycler.setAdapter(userPopupAdapter);
        userPopupAdapter.notifyDataSetChanged();

        userCloseX = userDialog.findViewById(R.id.user_popup_close);
        Log.d("POP", "ShowPopup: flight list : " + flightList);

        userCloseX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDialog.dismiss();
            }
        });
//        userDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        userDialog.show();
    }

}