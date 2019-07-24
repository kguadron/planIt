package com.example.planit;

import java.util.ArrayList;
import java.util.List;

import model.Flight;
import model.FlightItinerary;

public interface AnswerListAsyncResponse {
    void processFinished(List<FlightItinerary> flightArrayList);
}
