 package com.example.planit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import javax.annotation.Nullable;

import util.TripApi;

 public class LoginActivity extends AppCompatActivity {

    private Button loginButton;
    private Button createAcctButton;
    private ProgressBar progressBar;

    private AutoCompleteTextView emailAddress;
    private EditText password;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        emailAddress = findViewById(R.id.email);
        password = findViewById(R.id.password);
        progressBar = findViewById(R.id.login_progress);


        loginButton = findViewById(R.id.email_login_button);
        createAcctButton = findViewById(R.id.create_account_button_login);

        createAcctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class ));
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginEmailPasswordUser(emailAddress.getText().toString().trim(),
                        password.getText().toString().trim());

            }
        });
    }

     private void loginEmailPasswordUser(String email, String pwd) {
        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(email)
            && !TextUtils.isEmpty(pwd)) {

            firebaseAuth.signInWithEmailAndPassword(email, pwd)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            final String currentUserId = user.getUid();

                            collectionReference.whereEqualTo("userId", currentUserId)
                            .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                @Override
                                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                                    @Nullable FirebaseFirestoreException e) {

                                    if (e != null) {
                                        // there is an exception which means there's a problem
                                    }

                                    assert queryDocumentSnapshots != null;
                                    if (!queryDocumentSnapshots.isEmpty()) {

                                        progressBar.setVisibility(View.INVISIBLE);

                                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                                            TripApi tripApi = TripApi.getInstance();
                                            tripApi.setUsername(snapshot.getString("username"));
                                            tripApi.setUserId(currentUserId);

                                            // Go to ListActivity
                                            startActivity(new Intent(LoginActivity.this,
                                                    TripListActivity.class));

                                        }

                                    }
                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);

                            Toast.makeText(LoginActivity.this,
                                    "Login failed. Please try again",
                                    Toast.LENGTH_SHORT)
                                    .show();

                        }
                    });

        } else {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(LoginActivity.this,
                    "Please enter both email and password",
                    Toast.LENGTH_SHORT)
                    .show();
        }
     }
 }
