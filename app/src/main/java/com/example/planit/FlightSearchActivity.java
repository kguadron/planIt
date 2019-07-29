package com.example.planit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import controller.AppController;
import io.grpc.okhttp.internal.Util;
import model.Flight;
import model.FlightItinerary;
import model.FlightParams;
import ui.SearchResultsRecyclerAdapter;
import util.RequestQueueSingleton;

public class FlightSearchActivity extends AppCompatActivity implements View.OnClickListener {

    RequestQueue queue;

    private String tripId;

    private EditText originText;
    private EditText destinationText;
    private RadioButton oneWayButton;
    private EditText departureDate;
    private EditText returnDate;
    private RadioGroup radioGroup;
    private Button submitSearchButton;
    private ProgressBar progressBar;
    private String url;
    private List<FlightParams> flightParams = new ArrayList<>();
    private List<FlightItinerary> flightList = new ArrayList<>();

    private RecyclerView recyclerView;
    private SearchResultsRecyclerAdapter resultsRecyclerAdapter;

    DatePickerDialog.OnDateSetListener dateSetListener;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Flights");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flight_search);

        queue = RequestQueueSingleton.getInstance(this.getApplicationContext())
                .getRequestQueue();

        originText = findViewById(R.id.origin_text);
        destinationText = findViewById(R.id.destination_text);
        oneWayButton = findViewById(R.id.one_way_button);
        departureDate = findViewById(R.id.departure_date_text);
        returnDate = findViewById(R.id.return_date_text);
        radioGroup = findViewById(R.id.radio_group);
        progressBar = findViewById(R.id.flight_search_progress);
        submitSearchButton = findViewById(R.id.submit_search_button);
        submitSearchButton.setOnClickListener(this);

        Calendar calendar = Calendar.getInstance();


        tripId = getIntent().getStringExtra("TRIP_ID");
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        departureDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        FlightSearchActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                month = month + 1;
                                String date;
                                if (month < 10 && day < 10) {
                                    date = year + "-0" + month + "-0" + day;
                                } else if (day < 10) {
                                    date = year + "-" + month + "-0" + day;
                                } else if (month < 10) {
                                    date = year + "-0" + month + "-" + day;
                                } else {
                                    date = year + "-" + month + "-" + day;
                                }
                                departureDate.setText(date);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        returnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        FlightSearchActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day) {
                                month = month + 1;
                                String date;
                                if (month < 10 && day < 10) {
                                    date = year + "-0" + month + "-0" + day;
                                } else if (day < 10) {
                                    date = year + "-" + month + "-0" + day;
                                } else if (month < 10) {
                                    date = year + "-0" + month + "-" + day;
                                } else {
                                    date = year + "-" + month + "-" + day;
                                }
                                returnDate.setText(date);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_search_button:
                // send request with params
                generateFlightList(new AnswerListAsyncResponse() {
                    @Override
                    public void processFinished(final List<FlightItinerary> flightArrayList) {
                        // this is where the list is ready and the response data can be used
                        // to generate the list that users see ==> recycler view built here
                        progressBar.setVisibility(View.INVISIBLE);
                        Log.d("at end" ,"flightList:" + flightArrayList.size());

                        setContentView(R.layout.activity_search_results);

                        recyclerView = findViewById(R.id.search_results_recycler);
                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(FlightSearchActivity.this));

                        resultsRecyclerAdapter = new SearchResultsRecyclerAdapter(FlightSearchActivity.this,
                                flightArrayList);
                        recyclerView.setAdapter(resultsRecyclerAdapter);
                        resultsRecyclerAdapter.notifyDataSetChanged();

                        resultsRecyclerAdapter.setOnItemClickListner(new SearchResultsRecyclerAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                FlightItinerary chosenFlight = flightArrayList.get(position);
                                chosenFlight.setTripId(tripId);

                                collectionReference.add(chosenFlight)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                Toast.makeText(FlightSearchActivity.this,
                                                        "Flight proposed!",
                                                        Toast.LENGTH_LONG)
                                                        .show();

                                                String flightId = documentReference.getId();
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("flightId", flightId);


                                                documentReference.set(data, SetOptions.merge());

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d("TAG", "OnFailure for proposing flight" + e.getMessage());
                                                Toast.makeText(FlightSearchActivity.this,
                                                        "Flight not proposed. Please try again",
                                                        Toast.LENGTH_LONG)
                                                        .show();
                                            }
                                        });
                            }
                        });
                    }
                });
                break;
            // Another case here
        }
    }

    private List<FlightParams> generateParams() {
        String origin = originText.getText().toString().trim();
        String destination = destinationText.getText().toString().trim();
        String departure = departureDate.getText().toString().trim();
        String returnD = returnDate.getText().toString().trim();

        flightParams.add(new FlightParams("origin", origin));
        flightParams.add(new FlightParams("destination", destination));
        flightParams.add(new FlightParams("departureDate", departure));

        if (!TextUtils.isEmpty(returnDate.getText().toString().trim())) {
            flightParams.add(new FlightParams("returnDate", returnD));
        }

        return flightParams;
    }

    public static String generateUrl(List<FlightParams> params) {
        String baseUrl = "https://test.api.amadeus.com/v1/shopping/flight-offers?currency=USD&nonStop=true&max=20";
        if (params.size() > 0) {
            for (FlightParams parameter: params) {
                if (parameter.getKey().trim().length() > 0)
                    baseUrl += "&" + parameter.getKey() + "=" + parameter.getValue();
            }
        }
        return baseUrl;
    }

    public List<FlightItinerary> generateFlightList(final AnswerListAsyncResponse callBack) {
        generateParams();
        url = generateUrl(flightParams);
        Log.d("B4REQUEST", "URL:" + url);
        progressBar.setVisibility(View.VISIBLE);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {

                                JSONArray data = null;
                                try {
                                    data = response.getJSONArray("data");
                                    for (int i = 0; i < data.length(); i++) {

                                        // instantiating FlightItinerary to add to flightList
                                        FlightItinerary flightItinerary = new FlightItinerary();

                                        JSONObject specificFlight = data.getJSONObject(i);
                                        JSONArray offerItems = specificFlight.getJSONArray("offerItems");
                                        JSONObject specificOfferItem = offerItems.getJSONObject(0); //swirl
                                        JSONArray services = specificOfferItem.getJSONArray("services");

                                            for (int j = 0; j < services.length(); j++) {

                                                Flight flight = new Flight();

                                                JSONObject specificService = services.getJSONObject(j);
                                                JSONArray segments = specificService.getJSONArray("segments");
                                                JSONObject specificSegment = segments.getJSONObject(0); //star
                                                JSONObject flightSegment = specificSegment.getJSONObject("flightSegment");

                                                // origin
                                                JSONObject departureFlightSegment = flightSegment.getJSONObject("departure");
                                                String origin = departureFlightSegment.getString("iataCode");
                                                Log.d("JSON REQUEST", "ORIGIN: " + origin);


                                                // departure date and time
                                                String unformattedDateAndTimeDept = departureFlightSegment.getString("at");
                                                String[] dateTimeDeptArr = unformattedDateAndTimeDept.split("T");
                                                String date = dateTimeDeptArr[0];
                                                String time = dateTimeDeptArr[1];
                                                String departureDate = date.substring(5, 10);
                                                String departureTime = time.substring(0, 5);
                                                Log.d("JSON REQUEST", "DEPDATE: " + departureDate);
                                                Log.d("JSON REQUEST", "DEPTIME: " + departureTime);

                                                // destination
                                                JSONObject arrivalFlightSegment = flightSegment.getJSONObject("arrival");
                                                String destination = arrivalFlightSegment.getString("iataCode");
                                                Log.d("JSON REQUEST", "DESTINATION: " + destination);

                                                // return date and time
                                                String unformattedDateAndTimeArri = arrivalFlightSegment.getString("at");
                                                String[] dateTimeArriArr = unformattedDateAndTimeArri.split("T");
                                                String dateArri = dateTimeArriArr[0];
                                                String timeArri = dateTimeArriArr[1];
                                                String returnDate = dateArri.substring(5, 10);
                                                String returnTime = timeArri.substring(0, 5);
                                                Log.d("JSON REQUEST", "RETDATE: " + returnDate);
                                                Log.d("JSON REQUEST", "RETUTIME: " + returnTime);

                                                // airline
                                                String airline = flightSegment.getString("carrierCode");
                                                Log.d("JSON REQUEST", "AIRLINE: " + airline);

                                                //duration
                                                String duration = flightSegment.getString("duration");
                                                duration = duration.substring(3);
                                                Log.d("JSON REQUEST", "DURATION: " + duration);

                                                flight.setOrigin(origin);
                                                flight.setDepartureDate(departureDate);
                                                flight.setDepartureTime(departureTime);
                                                flight.setDestination(destination);
                                                flight.setReturnDate(returnDate);
                                                flight.setReturnTime(returnTime);
                                                flight.setDuration(duration);
                                                flight.setAirline(airline);
                                                flightItinerary.getFlights().add(flight);

                                            }

                                            DecimalFormat df = new DecimalFormat("###.##");
                                        JSONObject priceObject = specificOfferItem.getJSONObject("price");
                                        double price = Double.parseDouble(priceObject.getString("total"));
                                        double tax = Double.parseDouble(priceObject.getString("totalTaxes"));
                                        double totalPrice = price + tax;
                                        String formattedPrice = df.format(totalPrice);

                                        flightItinerary.setTotalPrice(Double.parseDouble(formattedPrice));

                                        flightList.add(flightItinerary);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(FlightSearchActivity.this,
                                            "Search timed out. \n Please try again",
                                            Toast.LENGTH_LONG)
                                            .show();
                                }

                                if (null != callBack) callBack.processFinished(flightList);

                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", "onErrorResponse: " + error.getMessage());
                        Toast.makeText(FlightSearchActivity.this,
                                "Search timed out. \n Please try again",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }) {
            //Passing some request headers*
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap();
                headers.put("Authorization", "Bearer FsRbzGMRoGC7Cg8fneUy8VQAG7wO");
                return headers;
            }
        };


        AppController.getInstance().addToRequestQueue(jsonObjectRequest, "headerRequest");


        return flightList;
    }
}
