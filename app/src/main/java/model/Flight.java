package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Time;

public class Flight implements Parcelable {
    private String origin;
    private String destination;
    private String departureDate;
    private String departureTime;
    private String returnDate;
    private String returnTime;
    private Double totalPrice;
    private String duration;
    private String travelClass;
    private String airline;
    private Integer votes;
    // private String adults;
    // private String children;
    // private String infants;
    // private String seniors;


    public Flight() { } // empty constructor so that the flight doesn't need all the parameters

    public Flight(String origin, String destination, String departureDate, String departureTime,
                  String returnDate, String returnTime,  Double totalPrice, String duration,
                  String travelClass, String airline, Integer votes) {
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
        this.departureTime = departureTime;
        this.returnDate = returnDate;
        this.returnTime = returnTime;
        this.totalPrice = totalPrice;
        this.duration = duration;
        this.travelClass = travelClass;
        this.airline = airline;
        this.votes = votes;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.origin);
        dest.writeString(this.destination);
        dest.writeString(this.departureDate);
        dest.writeString(this.departureTime);
        dest.writeString(this.returnDate);
        dest.writeString(this.returnTime);
        dest.writeDouble(this.totalPrice);
        dest.writeString(this.duration);
        dest.writeString(this.travelClass);
        dest.writeString(this.airline);
        dest.writeInt(this.votes);
    }

    protected Flight(Parcel in) {
        this.origin = in.readString();
        this.destination = in.readString();
        this.departureDate = in.readString();
        this.departureTime = in.readString();
        this.returnDate = in.readString();
        this.returnTime = in.readString();
        this.totalPrice = in.readDouble();
        this.duration = in.readString();
        this.travelClass = in.readString();
        this.airline = in.readString();
        this.votes = in.readInt();
    }

    public static final Creator<Flight> CREATOR = new Creator<Flight>() {
        public Flight createFromParcel(Parcel source) {
            return new Flight(source);
        }
        public Flight[] newArray(int size) {
            return new Flight[size];
        }
    };







    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(String returnTime) {
        this.returnTime = returnTime;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public Integer getVotes() {
        return votes;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getTravelClass() {
        return travelClass;
    }

    public void setTravelClass(String travelClass) {
        this.travelClass = travelClass;
    }
}
