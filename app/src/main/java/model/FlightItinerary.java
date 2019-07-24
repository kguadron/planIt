package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class FlightItinerary implements Parcelable {
    private List<Flight> flights = new ArrayList<>();
    private Double totalPrice;
    private Integer votes;

    public FlightItinerary() { } // empty constructor so that the flightItinerary doesn't need all the parameters

    public FlightItinerary(List<Flight> flights, Double totalPrice, Integer votes) {
        this.flights = flights;
        this.totalPrice = totalPrice;
        this.votes = votes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.totalPrice);
        dest.writeInt(this.votes);

        dest.writeTypedList(this.flights);
    }

    protected FlightItinerary(Parcel in) {
        this.totalPrice = in.readDouble();
        this.votes = in.readInt();

        in.readTypedList(this.flights, Flight.CREATOR);
    }

    public static final Creator<FlightItinerary> CREATOR = new Creator<FlightItinerary>() {
        public FlightItinerary createFromParcel(Parcel source) {
            return new FlightItinerary(source);
        }
        public FlightItinerary[] newArray(int size) {
            return new FlightItinerary[size];
        }
    };

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

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }
}