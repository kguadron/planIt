package com.example.planit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import model.Trip;
import ui.TripRecyclerAdapter;
import util.TripApi;

public class TripListActivity extends AppCompatActivity {

    private Button createTripButton;
    private Button findTripButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private List<Trip> tripList;

    private RecyclerView recyclerView;
    private TripRecyclerAdapter tripRecyclerAdapter;

    private CollectionReference collectionReference = db.collection("Trip");
    private TextView noTrips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_list);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        noTrips = findViewById(R.id.no_trips_hint);
        tripList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        createTripButton = findViewById(R.id.add_trip_to_list_button);
        findTripButton = findViewById(R.id.find_existing_trip_button);

        createTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TripListActivity.this, PostTripActivity.class ));
            }
        });

        findTripButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TripListActivity.this, FindTripActivity.class ));
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
//                    startActivity(new Intent(TripListActivity.this,
//                            PostTripActivity.class));
////                    finish();
//                }
//                break;
            case R.id.action_signout:
                // sign user out
                if (user != null && firebaseAuth != null) {
                    firebaseAuth.signOut();

                    startActivity(new Intent(TripListActivity.this,
                            MainActivity.class));
//                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        tripList.clear();

        // Get all trips from Firestore
        collectionReference.whereArrayContains("users", TripApi.getInstance()
            .getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        // Query documentSnapshot for data
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot trips : queryDocumentSnapshots) {
                                Trip trip = trips.toObject(Trip.class);
                                tripList.add(trip);
                            }

                            //Invoke Recycler view
                            tripRecyclerAdapter = new TripRecyclerAdapter(TripListActivity.this,
                                    tripList);
                            recyclerView.setAdapter(tripRecyclerAdapter);
                            tripRecyclerAdapter.notifyDataSetChanged();

                            tripRecyclerAdapter.setOnItemClickListner(new TripRecyclerAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    Intent intent = new Intent(TripListActivity.this,
                                            TripDetailsActivity.class);
                                    intent.putExtra("TRIP_ID", tripList.get(position).getTripId());
                                    startActivity(intent);

//                                    startActivity(new Intent(TripListActivity.this,
//                                            TripDetailsActivity.class));
                                }
                            });

                        } else {
                            noTrips.setVisibility(View.VISIBLE);
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
    protected void onPause() {
        super.onPause();
        // Prevents trip list from being repopulated with the same trip cards
        // when the activity is left and returned to
        tripList.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        tripList.clear();
    }
}
