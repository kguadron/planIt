package com.example.planit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import model.Trip;
import util.TripApi;

public class FindTripActivity extends AppCompatActivity {

    // User related
    private String currentUserId;
    private String currentUserName;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    // Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Trip");

    // Layout items
    private Button findButton;
    private EditText findTripId;
    private ProgressBar findProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_trip);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        findButton = findViewById(R.id.find_button);

        findTripId = findViewById(R.id.enter_trip_id_find);
        findProgressBar = findViewById(R.id.progressBarFindTrip);

        findProgressBar.setVisibility(View.INVISIBLE);

        if (TripApi.getInstance() != null) {
            currentUserId = TripApi.getInstance().getUserId();
            currentUserName = TripApi.getInstance().getUsername();
            // Use this in TripDetails Activity to show the users included on a trip
        }

//        authStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                user = firebaseAuth.getCurrentUser();
//                if (user != null) {
//                    // if there is a current user
//                } else {
//                    // do something if there isn't a current user
//                }
//            }
//        };

        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findTrip();
                startActivity(new Intent(FindTripActivity.this,
                        TripListActivity.class));
                finish();
            }
        });
    }

    private void findTrip() {
        String id = findTripId.getText().toString().trim();

        findProgressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(id)) {
            collectionReference.whereEqualTo("tripId", id)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            // if query goes through, queryDocumentSnapshots is the data
                            if (!queryDocumentSnapshots.isEmpty()) {
                                for (QueryDocumentSnapshot trips : queryDocumentSnapshots) {
                                    Trip trip = trips.toObject(Trip.class);
                                    trip.getUsers().add(currentUserId);
                                    collectionReference.document(trip.getTripId()).set(trip, SetOptions.merge());
                                }

                                findProgressBar.setVisibility(View.INVISIBLE);

                                Toast.makeText(FindTripActivity.this,
                                        "Trip added!",
                                        Toast.LENGTH_LONG)
                                        .show();
                                // make a trip card to display trip with add button
                            } else {
                                // if querySnapshot is empty
                                findProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(FindTripActivity.this,
                                        "This Trip ID does not exist",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
        } else {
            // if the TextEdit is empty
            findProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(FindTripActivity.this,
                    "Please enter a Trip ID",
                    Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        menu.findItem(R.id.back_to_details).setVisible(false);
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
            case R.id.back_to_trip_list:
                if (user != null && firebaseAuth != null) {
                    startActivity(new Intent(FindTripActivity.this,
                            TripListActivity.class));
                    finish();
                }
                break;
            case R.id.action_signout:
                // sign user out
                if (user != null && firebaseAuth != null) {
                    firebaseAuth.signOut();

                    startActivity(new Intent(FindTripActivity.this,
                            MainActivity.class));
//                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
