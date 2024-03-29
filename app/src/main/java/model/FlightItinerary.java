package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class FlightItinerary {
    private List<Flight> flights = new ArrayList<>();
    private Double totalPrice;
    private List<String> userVoted = new ArrayList<>();
    private Integer voteCount = 0;
    private String tripId;
    private String flightId;

    public FlightItinerary() { } // empty constructor so that the flightItinerary doesn't need all the parameters

    public FlightItinerary(List<Flight> flights, Double totalPrice, List<String> userVoted, String tripId, String flightId, Integer voteCount) {
        this.flights = flights;
        this.totalPrice = totalPrice;
        this.userVoted = userVoted;
        this.tripId = tripId;
        this.flightId = flightId;
        this.voteCount = voteCount;
    }

//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeDouble(this.totalPrice);
//        dest.writeInt(this.userVoted);
//
//        dest.writeTypedList(this.flights);
//    }
//
//    protected FlightItinerary(Parcel in) {
//        this.totalPrice = in.readDouble();
//        this.votes = in.readInt();
//
//        in.readTypedList(this.flights, Flight.CREATOR);
//    }
//
//    public static final Creator<FlightItinerary> CREATOR = new Creator<FlightItinerary>() {
//        public FlightItinerary createFromParcel(Parcel source) {
//            return new FlightItinerary(source);
//        }
//        public FlightItinerary[] newArray(int size) {
//            return new FlightItinerary[size];
//        }
//    };

    public List<Flight> getFlights() {
        return flights;
    }

    public void setFlights(List<Flight> flights) {
        this.flights = flights;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<String> getUserVoted() {
        return userVoted;
    }

    public void setUserVoted(List<String> userVoted) {
        this.userVoted = userVoted;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }
}