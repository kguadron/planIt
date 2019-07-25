package com.example.planit;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.common.collect.Table;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import model.Trip;

public class TripDetailsActivity extends AppCompatActivity {

    private String tripId;

    private TextView tripNameText;
    private TextView idText;
    private Button flightSearchButton;
    private RecyclerView proposedFlightsRecycler;
    private RecyclerView travelBudsRecycler;

    // Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Trip");

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_details);

        tripNameText = findViewById(R.id.trip_details_name);
        idText = findViewById(R.id.trip_detials_id);
        flightSearchButton = findViewById(R.id.flight_search_button);
        proposedFlightsRecycler = findViewById(R.id.proposed_flights_recycler);
        travelBudsRecycler = findViewById(R.id.travel_buds_recycler);

        // Gets tripId value from TripListActivity
        tripId = getIntent().getStringExtra("TRIP_ID");
        idText.setText("TRIP ID: " + tripId);

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
