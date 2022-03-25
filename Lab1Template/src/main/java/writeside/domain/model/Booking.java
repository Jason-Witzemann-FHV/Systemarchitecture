package writeside.domain.model;

import java.util.UUID;
import java.time.LocalDate;
import java.util.Set;

public class Booking {

    private UUID bookingId;
    private LocalDate arrivalDate;
    private LocalDate departureDate;
    private Set<String> rooms;
    private String customer;

    public Booking(UUID bookingId, LocalDate arrivalDate, LocalDate departureDate, Set<String> rooms, String customer) {
        this.bookingId = bookingId;
        this.arrivalDate = arrivalDate;
        this.departureDate = departureDate;
        this.rooms = rooms;
        this.customer = customer;
    }

    public UUID getBookingId() {
        return bookingId;
    }

    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }

    public LocalDate getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(LocalDate arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public Set<String> getRooms() {
        return rooms;
    }

    public void setRooms(Set<String> rooms) {
        this.rooms = rooms;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", arrivalDate=" + arrivalDate +
                ", departureDate=" + departureDate +
                ", rooms=" + rooms +
                ", customer=" + customer +
                '}';
    }
}
