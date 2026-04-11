package core.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Booking {
    private int bookingid;

    @JsonCreator
    public Booking(@JsonProperty("bookingid") int bookingid) {
        this.bookingid = bookingid;
    }

    public int getBookingId() {
        return bookingid;
    }

    public void setBookingId(int bookingid) {
        this.bookingid = bookingid;
    }
}
