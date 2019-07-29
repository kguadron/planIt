package com.example.planit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.DragStartHelper;

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
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import model.Trip;
import util.TripApi;

public class PostTripActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Post Trip Activity";
    private Button createTripButton;
    private ProgressBar progressBar;
    private EditText nameEditText;
    private EditText descriptionEditText;


    private String currentUserId;
    private String currentUserName;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    // Connection to Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    // private StorageReference storageReference; : add only if images are incorporated

    private CollectionReference collectionReference = db.collection("Trip");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_trip);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        nameEditText = findViewById(R.id.input_trip_name);
        descriptionEditText = findViewById(R.id.input_trip_description);
        createTripButton = findViewById(R.id.create_trip_button);
        createTripButton.setOnClickListener(this);

        progressBar.setVisibility(View.INVISIBLE);

        if (TripApi.getInstance() != null) {
            currentUserId = TripApi.getInstance().getUserId();
            currentUserName = TripApi.getInstance().getUsername();
            // Use this in TripDetails Activity to show the users included on a trip
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // if there is a current user
                } else {
                    // do something if there isn't a current user
                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.create_trip_button:
                // save trip
                saveTrip();
                break;
                // Another case here
        }
    }

    private void saveTrip() {
        String name = nameEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(name)) {
            // && !TextUtils.isEmpty(description)) : add only if description is mandatory
            // which it is not atm

            // Creating Trip object
            final Trip trip = new Trip();
            trip.setName(name);
            trip.setTimeAdded(new Timestamp(new Date()));
            trip.setUserOwnerName(currentUserName);
            trip.setUserOwnerId(currentUserId);
            trip.getUsers().add(currentUserId);

            // Description is optional, so set only if provided
            if (description != "") {
                trip.setDescription(description);
            }

            // Invoke collectionReference
            collectionReference.add(trip)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressBar.setVisibility(View.INVISIBLE);

                            Toast.makeText(PostTripActivity.this,
                                    "Trip created!",
                                    Toast.LENGTH_LONG)
                                    .show();

                            String tripId = documentReference.getId();
                            Map<String, Object> data = new HashMap<>();
                            data.put("tripId", tripId);


                            documentReference.set(data, SetOptions.merge());

                            startActivity(new Intent(PostTripActivity.this,
                                    TripListActivity.class));

                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "OnFailure for adding trip to collection" + e.getMessage());
                            Toast.makeText(PostTripActivity.this,
                                    "Trip not created. Try again",
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(PostTripActivity.this,
                    "Trip must have a name",
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
                    startActivity(new Intent(PostTripActivity.this,
                            TripListActivity.class));
                    finish();
                }
                break;
            case R.id.action_signout:
                // sign user out
                if (user != null && firebaseAuth != null) {
                    firebaseAuth.signOut();

                    startActivity(new Intent(PostTripActivity.this,
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

        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}
